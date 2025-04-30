package com.streamline;

import com.streamline.backend.DockerManager;
import com.streamline.backend.handle.YoutubeHandle;
import com.streamline.utilities.internal.Config;

import org.apache.commons.cli.CommandLine;

public class SetupManager {

    private SetupManager() {}
    
    protected static void setupApi(CommandLine commandLine) {
        String choice = commandLine.getOptionValue("setup").toLowerCase();
        if (choice.contains("docker")) {
            handleDockerSetup();
        } else if (choice.contains("youtube")) {
            handleYoutubeSetup();
        } else {
            System.out.println("[!] Invalid argument passed for --setup");
        }
    }

    protected static void handleDockerSetup() {
        DockerManager.cloneInvidiousRepo();
        boolean didWrite = DockerManager.writeDockerCompose();
        if (!didWrite) {
            System.out.println("[!] There was an error while parsing and writing docker-compose.yml, please re-run the app with the --setup flag");
        }
        if (DockerManager.buildInstance()) {
            System.out.println("\n[*] Invidious image built successfully!\n");
        } else {
            System.out.println("[!] An error occured while building the image for Invidious with Docker. Please try re-running the app with the --setup flag.");
        }
        System.exit(0);
    }

    protected static void handleYoutubeSetup() {
        Config config = ConfigManager.getConfigurationForRuntime();
        if (YoutubeHandle.setupYoutubeInterop(config)) {
            System.out.println("[*] yt-dlp has been successfully downloaded.");
        } else {
            System.out.println("[!] An error was encountered while setting up yt-dlp.");
        }
        System.exit(0);
    }

    protected static void cleanYoutubeInstall(Config config) {
        YoutubeHandle.clean(config);
    }

    protected static void cleanDockerInstall(Config config) {
        DockerManager.clean(config);
    }
}
