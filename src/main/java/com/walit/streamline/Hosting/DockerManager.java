package com.walit.streamline.Hosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;

import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.ProcessBuilder;

import java.math.BigInteger;

import java.net.URL;
import java.net.HttpURLConnection;

import java.security.SecureRandom;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.walit.streamline.Core;
import com.walit.streamline.Utilities.Internal.StreamLineConstants;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class DockerManager {

    private DockerManager() {}

    public static String startInvidiousContainer(Logger logger) {
        if (!isDockerInstalled()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotInstalledError.getMessage());
            return null;
        } else if (!isDockerRunning()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotRunningError.getMessage());
            return null;
        } else if (!invidiousDirectoryExists()) {
            logger.log(Level.WARNING, StreamLineMessages.InvidiousRepositoryHasNotBeenClonedWarning.getMessage());
            return null;
        }
        System.out.println("Starting invidious instance through Docker...");
        Core.runCommand("docker compose -f " + StreamLineConstants.DOCKER_COMPOSE_PATH + " up");
        try {
            if (isContainerRunning()) {
                String host = "http://localhost:" + StreamLineConstants.INVIDIOUS_PORT + "/";
                System.out.println("Invidious instance is now live at " + host);
                return host;
            }
        } catch (InterruptedException iE) {
            System.out.println("[!] Error pinging instance!");
        }
        return null;
    }

    private static boolean invidiousDirectoryExists() {
        File invidiousDirectory = new File("./invidious");
        return invidiousDirectory.exists();
    }

    public static void cloneInvidiousRepo() {
        if (!invidiousDirectoryExists()) {
            System.out.println("Cloning Invidious repo...");
            Process process = Core.runCommandExpectWait("git clone " + StreamLineConstants.INVIDIOUS_REPO_ADDRESS + " invidious");
            try {
                displayLoading(process, StreamLineConstants.CLONING_REPO_MESSAGE);
            } catch (InterruptedException iE) {
                System.out.println(StreamLineMessages.ErrorCloningRepository.getMessage());
            }
        } else {
            System.out.println("Invidious repository has already been cloned.");
        }
    }

    private static void displayLoading(Process process, String message) throws InterruptedException {
        int i = 0;
        char[] spinner = StreamLineConstants.SPINNER_SYMBOLS;
        while (process.isAlive()) {
            System.out.print("\r[" + spinner[i] + "] " + message);
            i = (i + 1) % spinner.length;
            Thread.sleep(200);
        }
        System.out.print("\r" + StreamLineConstants.LOADING_COMPLETE_MESSAGE);
    }

    public static boolean writeDockerCompose() {
        try {
            String[] tokens = retrieveTokensFromYoutubeValidator();
            String hmacKey = generateHmacKey();
            String poToken = tokens[0];
            String visitorData = tokens[1];
            if (poToken == null || visitorData == null || hmacKey == null) {
                return false;
            }
            System.out.println("Setting variables in docker-compose.yml...");
            String yamlFilePath = "./invidious/docker-compose.yml";
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(yamlFilePath);
            Map<String, Object> yamlData = yaml.load(inputStream);
            inputStream.close();

            Map<String, Object> services = (Map<String, Object>) yamlData.get("services");
            Map<String, Object> invidiousService = (Map<String, Object>) services.get("invidious");

            List<String> ports = (List<String>) invidiousService.get("ports");
            for (int i = 0; i < ports.size(); i++) {
                if (ports.get(i).contains("127.0.0.1:3000:3000")) {
                    ports.set(i, "3000:3000");
                }
            }

            Map<String, Object> environment = (Map<String, Object>) invidiousService.get("environment");

            String invidiousConfig = (String) environment.get("INVIDIOUS_CONFIG");
            List<String> configLines = List.of(invidiousConfig.split("\n"));

            StringBuilder updatedConfig = new StringBuilder();
            for (String line : configLines) {
                if (line.contains("hmac_key:")) {
                    updatedConfig.append("hmac_key: ").append(hmacKey + "\n");
                    updatedConfig.append("po_token: ").append(poToken + "\n");
                    updatedConfig.append("visitor_data: ").append(visitorData + "\n");
                } else if (line.contains("po_token") || line.contains("visitor_data")) {
                    System.out.println("Docker-compose has already been setup.");
                    return true;
                } else {
                    updatedConfig.append(line).append("\n");
                }
            }

            environment.put("INVIDIOUS_CONFIG", updatedConfig.toString().trim());

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yamlWriter = new Yaml(options);
            FileWriter writer = new FileWriter(yamlFilePath);
            yamlWriter.dump(yamlData, writer);
            writer.close();
            System.out.println("Docker-compose.yml has been successfully modified!");
        } catch (IOException iE) {
            return false;
        }
        return true;
    }

    private static String generateHmacKey() {
        System.out.println("Generating HMAC key...");
        SecureRandom random = new SecureRandom();
        String key = new BigInteger(256, random).toString(16);
        System.out.println("Successfully generated HMAC key.");
        return key;
    }


    private static String[] retrieveTokensFromYoutubeValidator() {
        String[] tokensToReturn = new String[3];
        try {
            ProcessBuilder pb = new ProcessBuilder(StreamLineConstants.GET_TOKENS_FOR_YOUTUBE_VALIDATOR.split(" "));
            Process process = pb.start();
            displayLoading(process, StreamLineConstants.RETRIEVING_TOKENS_MESSAGE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder commandOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                commandOutput.append(line).append("\n");
            }
            process.waitFor();
            String outputText = commandOutput.toString();
            System.out.println("Parsing tokens from response...");
            tokensToReturn[0] = extractValue(outputText, "po_token:\\s*(\\S+)");
            tokensToReturn[1] = extractValue(outputText, "visitor_data:\\s*(\\S+)");
            System.out.println("Successfully retrieved tokens from Youtube validator.");
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.ErrorRetrievingTokensForDockerCompose.getMessage());
            return null;
        }

        return tokensToReturn;
    }

    private static String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static boolean userHasPermissionsForDocker() {
        return Core.runCommand("docker ps");
    }

    private static boolean isDockerInstalled() {
        return Core.runCommand("docker --version");
    }

    private static boolean isDockerRunning() {
        return Core.runCommand("docker info");
    }

    public static void stopContainer(Logger logger) {
        Core.runCommand("docker compose -f " + StreamLineConstants.DOCKER_COMPOSE_PATH + " stop");
        logger.log(Level.INFO, "Container has been stopped.");
    }

    public static boolean isContainerRunning() throws InterruptedException {
        for (int i = 0; i < 10; i++) {  // Retry for ~10 seconds
            try {
                URL url = new URL(String.format("http://localhost:%d/api/v1/stats", StreamLineConstants.INVIDIOUS_PORT));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (IOException e) {
                Thread.sleep(1000);
            }
        }
        return false;
    }
}
