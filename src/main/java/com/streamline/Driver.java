package com.streamline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;

import com.streamline.audio.AudioPlayer;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.backend.DockerManager;
import com.streamline.backend.InvidiousHandle;
import com.streamline.backend.YoutubeHandle;
import com.streamline.frontend.terminal.TerminalInterface;
import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.LibraryPeer;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.Mode;
import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.tinylog.Logger;

/**
 * Entry point for the app. Handles the user input and determines the appropriate runtime configuration for the app.
 * @author wellatleastitried
 */
public final class Driver {

    private static OS os;

    static {
        os = getOSOfUser();
        if (!initializeLogger(os)) {
            System.out.println("[!] There was an error while initializing the logger, please try reloading the app!");
            System.exit(0);
        }
        checkExistenceOfConfiguration();
    }

    private Driver() {}

    private static void checkExistenceOfConfiguration() {
        String path = switch (os) {
            case WINDOWS -> StreamLineConstants.STREAMLINE_CONFIG_PATH_WINDOWS;
            case MAC -> StreamLineConstants.STREAMLINE_CONFIG_PATH_MAC;
            default -> StreamLineConstants.STREAMLINE_CONFIG_PATH_LINUX;
        };
        if (new File(path).exists()) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(createLine("language", LanguagePeer.getSystemLocale()));
        writeConfigurationFile(path, stringBuilder.toString());
    }

    private static void writeConfigurationFile(String path, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(text);
        } catch (IOException iE) {
            Logger.warn("Error writing configuration file, check permissions and try again.");
        }
    }

    private static String createLine(String id, String value) {
        return String.format("%s=%s\n", id, value);
    }

    private static void printHelpCli(Options options) {
        System.out.println("\nUsage:\n\tstreamline [--OPTION] [ARGUMENT]\n");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("StreamLine", options);
    }

    public static void main(String [] args) {
        Options options = getOptionsForTerminal();
        handleArguments(args, options);
    }

    private static Options getOptionsForTerminal() {
        Options options = new Options();
        options.addOption("h", "help", false, "Help menu explaining the different flags");
        options.addOption("s", "setup", true, "Initialize the configuration for:\n\t- \"--Docker\" for a locally hosted Invidious instance\n\t- \"--YouTube\" for audio conversion from YouTube videos");
        options.addOption("d", "docker", false, "Start StreamLine with an Invidious docker instance being used in the backend.");
        options.addOption("y", "youtube", false, "Start StreamLine with yt-dlp being used in the backend. [THIS IS THE DEFAULT IF NO BACKEND IS SPECIFIED]");
        options.addOption("np", "now-playing", false, "Return the name and artist of the song that is currently playing (no output if there is no song playing).");
        options.addOption("c", "clean", true, "Remove unwanted files from the Docker or Youtube install.\nExample:\n\tstreamline --Docker\t=> Removes the Invidious repository from the filesystem\n\n\tstreamline --YouTube\t=> Removes the binary for yt-dlp from the filesystem.");
        options.addOption("i", "import-library", true, "Import your music library from other devices into your current setup and then exit (e.g., --import-library=/path/to/library.json");
        options.addOption("e", "export-library", false, "Generate a file (library.json) that contains all of your music library that can be used to import this library on another device and then exit");
        options.addOption("p", "play", true, "Play a single song (e.g., --play=\"songname\") and start headless with CLI commands available");
        options.addOption("cm", "cache-manager", false, "Clear the application's cache from the system to free up storage.");
        return options;
    }

    private static void handleArguments(String[] arguments, Options options) {
        CommandLine commandLine;

        try {
            commandLine = new DefaultParser().parse(options, arguments);
            if (arguments.length > 2 && !commandLine.hasOption("delete")) {
                System.out.println("[!] There were too many arguments provided. Only one option can be chosen at a time.\n\tUsage:\n\t\tstreamline [--OPTION] [ARGUMENT]\n");
                System.exit(0);
            }
            if (arguments.length < 1) handleStandardRuntime(commandLine);
            else if (commandLine.hasOption("setup")) handleSetup(commandLine);
            else if (commandLine.hasOption("help")) printHelpCli(options);
            else if (commandLine.hasOption("docker") || commandLine.hasOption("youtube")) handleStandardRuntime(commandLine);
            else if (commandLine.hasOption("clean")) handleCleaningProcess(commandLine);
            else if (commandLine.hasOption("np")) displayCurrentlyPlayingSong();
            else if (commandLine.hasOption("import-library")) handleLibraryImport(commandLine);
            else if (commandLine.hasOption("export-library")) handleLibraryExport();
            else if (commandLine.hasOption("play")) handlePlay(commandLine);
            else if (commandLine.hasOption("cache-manager")) handleCacheManager();
            else System.out.println("[!] Invalid or no arguments provided. Use --help for usage information.");
        } catch (ParseException pE) {
            System.err.println("[!] Error parsing command line arguments: " + pE.getMessage());
            printHelpCli(options);
        }
    }

    private static void displayCurrentlyPlayingSong() {
        // Get the name and artist of the currently playing song
        System.out.println("Not yet implemented.");
    }
    
    private static void handleSetup(CommandLine commandLine) {
        String choice = commandLine.getOptionValue("setup").toLowerCase();
        if (choice.contains("docker")) {
            handleDockerSetup();
        } else if (choice.contains("youtube")) {
            handleYoutubeSetup();
        } else {
            System.out.println("[!] Invalid argument passed for --setup");
        }
    }

    private static void handleLibraryExport() { // Transfer database entries to JSON file
        Config config = getConfigurationForRuntime();
        LibraryPeer libManager = new LibraryPeer(config);
        String exportPath = libManager.exportExistingLibrary();
        if (exportPath != null) {
            System.out.println("[*] Your library has been exported to the file: " + exportPath);
        } else {
            System.out.println("[!] There was an error while exporting your library, please try again.");
        }
        System.exit(0);
    }

    private static void handleLibraryImport(CommandLine commandLine) { // Take in JSON file and fill database with entries
        Config config = getConfigurationForRuntime();
        String filename = commandLine.getOptionValue("import-library");
        LibraryPeer libManager = new LibraryPeer(config);
        boolean success = libManager.importExistingLibrary(filename);
        if (!success) {
            System.out.println("[!] An error occured while importing your library, please try again.");
        }
        System.exit(0);
    }

    private static void handleCleaningProcess(CommandLine commandLine) {
        Config config = getConfigurationForRuntime();
        String installToClean = commandLine.getOptionValue("clean");
        if ("youtube".equals(installToClean)) {
            cleanYoutubeInstall(config);
        } else if ("docker".equals(installToClean)) {
            cleanDockerInstall(config);
        } else {
            System.out.println("[!] Invalid argument passed for --clean. Please specify either \"youtube\" or \"docker\"");
        }
        System.exit(0);
    }

    private static void cleanYoutubeInstall(Config config) {
        YoutubeHandle.clean(config);
    }

    private static void cleanDockerInstall(Config config) {
        DockerManager.clean(config);
    }

    private static boolean handlePlayingSingleSong(Config config, String songName) {
        Dispatcher streamlineBackend = new Dispatcher(config);
        Song song = streamlineBackend.getSongFromName(songName);
        if (song == null) {
            return false;
        } else {
            streamlineBackend.playSong(song);
            /*
            try {
                streamlineBackend.audioThread.join();
                return true;
            } catch (InterruptedException iE) {
                Logger.warn("[!] An error occured during playback, please try again.");
            }
            */
        }
        return false;
    }

    private static void handleStandardRuntime(CommandLine commandLine) {
        Config configuration;
        if (commandLine.hasOption("youtube") || commandLine.hasOption("docker")) {
            configuration = getConfigurationForRuntime(commandLine);
        } else {
            configuration = getConfigurationForRuntime();
        }
        Dispatcher streamlineBackend = new Dispatcher(configuration);
        TerminalInterface tui = new TerminalInterface(streamlineBackend);
        if (!tui.run()) {
            System.err.println("[!] A fatal error has occured while starting StreamLine, please try reloading the app.");
            return;
        }
    }

    private static void handleCacheManager() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Would you like to clear the existing cache? Enter y or n: ");
            String response = scanner.nextLine();
            if (response.toLowerCase().trim().equals("y")) {
                Config config = new Config();
                config.setMode(Mode.CACHE_MANAGEMENT);
                config.setOS(os);
                new Dispatcher(config).clearCache();
            }
        }
    }


    private static void handlePlay(CommandLine commandLine) {
        Config config = getConfigurationForRuntime();
        String songName = commandLine.getOptionValue("play");
        boolean songWasAlreadyAvailable = handlePlayingSingleSong(config, songName);
        if (!songWasAlreadyAvailable) {
            try {
                RetrievedStorage results = new Dispatcher(config).doSearch("Cold - Give");
                String videoId = results.getArrayOfSongs()[0].getSongVideoId();
                String url = new YoutubeHandle(config).getAudioUrlFromVideoId(videoId);
                new AudioPlayer().playSongFromUrl(url);
            } catch (Exception e) {
                System.out.println("[!] An error occured during song playback, please try restarting the app.");
            }
        }
        System.exit(0);
    }

    public static void handleDockerSetup() {
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

    public static void handleYoutubeSetup() {
        Config config = getConfigurationForRuntime();
        if (YoutubeHandle.setupYoutubeInterop(config)) {
            System.out.println("[*] yt-dlp has been successfully downloaded.");
        } else {
            System.out.println("[!] An error was encountered while setting up yt-dlp.");
        }
        System.exit(0);
    }

    private static Config getConfigurationForRuntime() {
        Config config = new Config();
        config.setMode(Mode.TERMINAL);

        config.setOS(os);

        config.setAudioSource('y');
        config.setBinaryPath(getBinaryPath(config));

        return config;
    }

    private static Config getConfigurationForRuntime(CommandLine commandLine) {
        Config config = getConfigurationForRuntime();
        if (commandLine.hasOption("youtube")) {
            config.setAudioSource('y');
        } else {
            config.setAudioSource('d');
            String apiHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
            if (apiHost == null || apiHost.length() < 1) {
                new Thread(() -> DockerManager.startInvidiousContainer()).start();
                config.setIsOnline(false);
                config.setHost(null);
            } else {
                config.setIsOnline(true);
                config.setHost(apiHost);
            }
        }
        return config;
    }

    public static OS getOSOfUser() {
        String osString = System.getProperty("os.name").toLowerCase();
        if (osString.contains("win")) {
            return OS.WINDOWS;
        } else if (osString.contains("nix") || osString.contains("nux")) {
            return OS.LINUX;
        } else if (osString.contains("mac")) {
            return OS.MAC;
        } else {
            return OS.UNKNOWN;
        }
    }

    private static boolean initializeLogger(OS os) {
        String configurationPath;
        String configurationFileContents;
        switch (os) {
            case WINDOWS -> {
                configurationPath = StreamLineConstants.WINDOWS_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.WINDOWS_LOG_CONFIG_CONTENTS;
            }
            case MAC -> {
                configurationPath = StreamLineConstants.MAC_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.UNIX_LOG_CONFIG_CONTENTS;
            }
            default -> {
                configurationPath = StreamLineConstants.LINUX_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.UNIX_LOG_CONFIG_CONTENTS;
            }
        };

        File configDirectory = new File(configurationPath);
        if (!configDirectory.exists()) {
            if (!configDirectory.mkdirs()) {
                return false;
            }
        }

        configurationPath = configurationPath + "tinylog.properties";
        File configFile = new File(configurationPath);
        if (!configFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write(configurationFileContents);
                writer.flush();
            } catch (IOException iE) {
                return false;
            }
        }

        System.setProperty("tinylog.configuration", configurationPath);
        return true;
    }

    private static String getBinaryPath(Config config) {
        if (config.getOS() == OS.WINDOWS) {
            return StreamLineConstants.YT_DLP_BIN_LOCATION_WINDOWS + "yt-dlp.exe";
        } else if (config.getOS() == OS.MAC) {
            return StreamLineConstants.YT_DLP_BIN_LOCATION_MAC + "yt-dlp";
        }
        return StreamLineConstants.YT_DLP_BIN_LOCATION_LINUX + "yt-dlp";
    }
}
