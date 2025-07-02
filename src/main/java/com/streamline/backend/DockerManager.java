package com.streamline.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.streamline.utilities.ConfigManager;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;

import org.tinylog.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Allows for the easy management of the Invidious container.
 * @author wellatleastitried
 */
public class DockerManager {

    private static OS os;
    private static String invidiousDirectoryPath;
    private static String dockerComposeUp;
    private static String dockerComposeStop;
    private static String dockerComposeBuild;

    private static Thread containerRuntime;

    static {
        os = ConfigManager.getOSOfUser();
        if (os == OS.WINDOWS) {
            invidiousDirectoryPath = StreamLineConstants.INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS;
            dockerComposeUp = String.format("docker compose -f %s\\docker-compose.yml up", StreamLineConstants.INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS);
            dockerComposeStop = String.format("docker compose -f %s\\docker-compose.yml stop", StreamLineConstants.INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS);
            dockerComposeBuild = String.format("docker compose -f %s\\docker-compose.yml build", StreamLineConstants.INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS);
        } else if (os == OS.MAC) {
            invidiousDirectoryPath = StreamLineConstants.INVIDIOUS_LOCAL_MAC_REPO_ADDRESS;
            dockerComposeUp = String.format("docker compose -f %s/docker-compose.yml up", StreamLineConstants.INVIDIOUS_LOCAL_MAC_REPO_ADDRESS);
            dockerComposeStop = String.format("docker compose -f %s/docker-compose.yml stop", StreamLineConstants.INVIDIOUS_LOCAL_MAC_REPO_ADDRESS);
            dockerComposeBuild = String.format("docker compose -f %s/docker-compose.yml build", StreamLineConstants.INVIDIOUS_LOCAL_MAC_REPO_ADDRESS);
        } else {
            invidiousDirectoryPath = StreamLineConstants.INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS;
            dockerComposeUp = String.format("docker compose -f %s/docker-compose.yml up", StreamLineConstants.INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS);
            dockerComposeStop = String.format("docker compose -f %s/docker-compose.yml stop", StreamLineConstants.INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS);
            dockerComposeBuild = String.format("docker compose -f %s/docker-compose.yml build", StreamLineConstants.INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS);
        }
        containerRuntime = new Thread(() -> CommandExecutor.runCommand(dockerComposeUp));
        containerRuntime.setName("Invidious Runtime");
    }
    
    private DockerManager() {}

    public static String startInvidiousContainer() {
        try {
            if (!isDockerInstalled()) {
                Logger.warn("[!] Docker does not appear to be installed on this machine, only offline functionality will be available.");
                return null;
            } else if (!isDockerRunning()) {
                Logger.warn("[!] Docker is not currently running on your machine, only offline functionality will be available.");
                return null;
            }
            if (invidiousDirectoryExists()) {
                if (canConnectToContainer(2, 500)) {
                    return StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS;
                }
            } else {
                Logger.warn("[*] The Invidious repository has not been cloned, docker will not be able to produce an Invidious instance without it. Reload the application with the --setup flag to locally clone the repository.");
                return null;
            }
        } catch (InterruptedException iE) {
            Logger.error("[!] An error occured while checking the state of the Invidious image in Docker.");
        }

        System.out.println("[*] Starting invidious instance through Docker...");

        containerRuntime.start();

        try {
            if (canConnectToContainer()) {
                System.out.println("[*] Invidious instance is now live at " + StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS);
                return StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS;
            } else {
                System.out.println("[*] Could not connect to Invidious instance at this time.");
            }
        } catch (InterruptedException iE) {
            Logger.warn("[!] An error occured while pinging instance!");
        }
        return null;
    }

    public static boolean invidiousDirectoryExists() {
        File invidiousDirectory = new File(invidiousDirectoryPath);
        return invidiousDirectory.exists();
    }

    private static boolean gitIsInstalled() {
        return CommandExecutor.runCommand("git --version");
    }

    public static void cloneInvidiousRepo() {
        if (!gitIsInstalled()) {
            Logger.warn("[!] Git is not installed on this device. Git must be installed before running setup!");
            return;
        }
        if (!invidiousDirectoryExists()) {
            Process process = CommandExecutor.runCommandExpectWait("git clone " + StreamLineConstants.INVIDIOUS_GITHUB_REPO_ADDRESS + " " + invidiousDirectoryPath); 
            try {
                displayLoading(process, StreamLineConstants.CLONING_REPO_MESSAGE);
            } catch (InterruptedException iE) {
                System.out.println("[!] There was an error while trying to clone the Invidious repository, please try re-running the app with the --setup flag.");
            }
        } else {
            System.out.println("[*] Invidious repository has already been cloned.");
        }
    }

    public static void clean(Config config) {
    }

    private static Thread getLoadingAnimationThread(Process process, String message) {
        return new Thread(() -> {
            try {
                int i = 0;
                char[] spinner = StreamLineConstants.SPINNER_SYMBOLS;
                while (process.isAlive()) {
                    System.out.print("\r[" + spinner[i] + "] " + message);
                    i = (i + 1) % spinner.length;
                    Thread.sleep(200);
                }
            } catch (InterruptedException iE) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static int displayLoading(Process process, String message) throws InterruptedException {
        Thread loadingAnimation = getLoadingAnimationThread(process, message);
        loadingAnimation.setName("Loading Graphic");
        loadingAnimation.start();
        int exitCode = process.waitFor();
        loadingAnimation.interrupt();
        if (exitCode == 0) {
            System.out.println("\r" + StreamLineConstants.LOADING_COMPLETE_SYMBOL + message);
        } else {
            System.out.println("\r" + StreamLineConstants.LOADING_ERROR_MESSAGE); 
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
            } catch (IOException e) {
                Logger.warn("[!] Failed to read process error output", e);
            }
        }
        return exitCode;
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
            System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Setting variables in docker-compose.yml...");
            String yamlFilePath = invidiousDirectoryPath + "/docker-compose.yml";
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
                    System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Docker-compose has already been setup.");
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
            System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "docker-compose.yml has been successfully written!");
        } catch (IOException iE) {
            Logger.warn("[!] An error occured while writing the docker-compose.yml for the Invidious instance.");
            return false;
        }
        return true;
    }

    public static boolean buildInstance() {
        Process process = CommandExecutor.runCommandExpectWait(dockerComposeBuild);
        int exitCode;
        try {
            exitCode = displayLoading(process, StreamLineConstants.BUILD_INVIDIOUS_IMAGE);
            String host = startInvidiousContainer();
            if (host == null) {
                return false;
            }
        } catch (InterruptedException iE) {
            return false;
        }
        if (exitCode != 0) {
            return false;
        }
        return true;
    }

    private static String generateHmacKey() {
        System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Generating HMAC key...");
        SecureRandom random = new SecureRandom();
        String key = new BigInteger(256, random).toString(16);
        System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Successfully generated HMAC key.");
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
            System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Parsing tokens from response...");
            tokensToReturn[0] = extractValue(outputText, "po_token:\\s*(\\S+)");
            tokensToReturn[1] = extractValue(outputText, "visitor_data:\\s*(\\S+)");
            System.out.println(StreamLineConstants.LOADING_COMPLETE_SYMBOL + "Successfully retrieved tokens from Youtube validator.");
        } catch (InterruptedException | IOException iE) {
            System.out.println("[!] There was an error while retrieving the youtube validator tokens, please try again later.");
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
        return CommandExecutor.runCommand("docker ps");
    }

    private static boolean isDockerInstalled() {
        return CommandExecutor.runCommand("docker --version");
    }

    private static boolean isDockerRunning() {
        return CommandExecutor.runCommand("docker info");
    }

    public static void stopContainer() {
        containerRuntime.interrupt();
        CommandExecutor.runCommand(dockerComposeStop);
    }

    public static boolean containerIsAlive() {
        return containerRuntime.isAlive();
    }

    public static boolean canConnectToContainer(int maxRetryAttempts, int timeout) throws InterruptedException {
        int attempts = 0;
        while (attempts < maxRetryAttempts) {
            try {
                URL url = new URL(StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    return true;
                }
            } catch (IOException e) {
                Thread.sleep(timeout);
            }
            attempts++;
        }
        return false;
    }

    /*
     * By default: 30 maxRetryAttempts, 1 second timeout
     */
    public static boolean canConnectToContainer() throws InterruptedException {
        return canConnectToContainer(30, 1000);
    }
}
