package com.walit.streamline.Hosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.walit.streamline.Core;
import com.walit.streamline.Utilities.Internal.StreamLineConstants;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

public class DockerManager {

    private final Logger logger;

    public DockerManager(Logger logger) {
        this.logger = logger;
    }

    public String startInvidiousContainer() {
        if (!isDockerInstalled()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotInstalledError.getMessage());
            return null;
        } else if (!isDockerRunning()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotRunningError.getMessage());
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

    public static void cloneInvidiousRepo() {
        File invidiousDirectory = new File("./invidious");
        if (!invidiousDirectory.exists()) {
            Core.runCommand("git submodule update --init --recursive");
        }
    }

    public static boolean writeDockerCompose() {
        try {
            String[] tokens = retrieveTokensFromYoutubeValidator();
            tokens[3] = generateHmacKey();
            if (tokens[0] == null || tokens[1] == null || tokens[2] == null) {
                return false;
            }
            File file = new File(".invidious/docker-compose.yml");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("po_token:\\s*.*", "po_token: " + tokens[0])
                    .replaceAll("visitor_data:\\s*.*", "visitor_data: " + tokens[1])
                    .replaceAll("hmac_key:\\s*.*", "hmac_key: " + tokens[2]);
                content.append(line).append("\n");
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content.toString());
            writer.close();
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
            ProcessBuilder pb = new ProcessBuilder(StreamLineConstants.GET_TOKENS_FOR_YOUTUBE_VALIDATOR);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder commandOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                commandOutput.append(line).append("\n");
            }
            process.waitFor();
            String outputText = commandOutput.toString();
            tokensToReturn[0] = extractValue(outputText, "po_token:\\s*(\\S+)");
            tokensToReturn[1] = extractValue(outputText, "visitor_data:\\s*(\\S+)");
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.ErrorRetrievingTokensForDockerCompose.getMessage());
            return null;
        }

        if (tokensToReturn[0] == null || tokensToReturn[1] == null) {
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

    public boolean userHasPermissionsForDocker() {
        return Core.runCommand("docker ps");
    }

    private boolean isDockerInstalled() {
        return Core.runCommand("docker --version");
    }

    private boolean isDockerRunning() {
        return Core.runCommand("docker info");
    }

    public void stopContainer() {
        Core.runCommand("docker compose -f " + StreamLineConstants.DOCKER_COMPOSE_PATH + " down");
        logger.log(Level.INFO, "Container has been stopped.");
    }

    public boolean isContainerRunning() throws InterruptedException {
        for (int i = 0; i < 10; i++) {  // Retry for ~10 seconds
            try {
                URL url = new URL("http://localhost:3000/api/v1/stats");
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
