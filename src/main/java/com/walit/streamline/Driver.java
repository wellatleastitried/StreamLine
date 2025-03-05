package com.walit.streamline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.XMLFormatter;

import com.walit.streamline.audio.AudioPlayer;
import com.walit.streamline.audio.Song;
import com.walit.streamline.backend.Core;
import com.walit.streamline.backend.DockerManager;
import com.walit.streamline.backend.InvidiousHandle;
import com.walit.streamline.backend.YoutubeHandle;
import com.walit.streamline.frontend.TerminalInterface;
import com.walit.streamline.utilities.LibraryManager;
import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;
import com.walit.streamline.utilities.internal.Mode;
import com.walit.streamline.utilities.internal.OS;
import com.walit.streamline.utilities.internal.StreamLineConstants;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Driver {

    private static Logger logger;
    private static OS os;

    static {
        os = getOSOfUser();
        logger = initializeLogger(os);
    }

    private Driver() {}

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
        options.addOption("c", "clean", true, "Remove unwanted files from the Docker or Youtube install.\nExample:\n\tstreamline --Docker\t=> Removes the Invidious repository from the filesystem\n\n\tstreamline --YouTube\t=> Removes the binary for yt-dlp from the filesystem.");
        options.addOption("i", "import-library", true, "Import your music library from other devices into your current setup and then exit (e.g., --import-library=/path/to/library.json");
        options.addOption("e", "export-library", false, "Generate a file (library.json) that contains all of your music library that can be used to import this library on another device and then exit");
        options.addOption("q", "quiet", true, "Headless start with ability to access the application at http://localhost:PORT");
        options.addOption("p", "play", true, "Play a single song (e.g., --play=\"songname\") and start headless with CLI commands available");
        options.addOption("cm", "cache-manager", false, "Choose whether to clear all cache or only expired cache and then exit");
        return options;
    }

    private static void handleArguments(String[] arguments, Options options) {
        CommandLine commandLine;

        try {
            commandLine = new DefaultParser().parse(options, arguments);
            if (arguments.length > 2 && !commandLine.hasOption("delete")) {
                System.out.println(StreamLineMessages.TooManyArgumentsProvided.getMessage());
                System.exit(0);
            }
            if (arguments.length < 1) handleStandardRuntime(commandLine);
            else if (commandLine.hasOption("setup")) handleSetup(commandLine);
            else if (commandLine.hasOption("help")) printHelpCli(options);
            else if (commandLine.hasOption("docker") || commandLine.hasOption("youtube")) handleStandardRuntime(commandLine);
            else if (commandLine.hasOption("import-library")) handleLibraryImport(commandLine);
            else if (commandLine.hasOption("export-library")) handleLibraryExport();
            else if (commandLine.hasOption("quiet")) handleHeadlessMode(commandLine);
            else if (commandLine.hasOption("play")) handlePlay(commandLine);
            else if (commandLine.hasOption("cache-manager")) handleCacheManager();
            else System.out.println("Invalid or no arguments provided. Use --help for usage information.");
        } catch (ParseException pE) {
            System.err.println("Error parsing command line arguments: " + pE.getMessage());
            printHelpCli(options);
        }
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
        LibraryManager libManager = new LibraryManager(config);
        String exportPath = libManager.exportExistingLibrary();
        if (exportPath != null) {
            System.out.println("[*] Your library has been exported to the file: " + exportPath);
        } else {
            System.out.println("[!] There was an error while exporting your library, please try again.");
        }
    }

    private static void handleLibraryImport(CommandLine commandLine) { // Take in JSON file and fill database with entries
        Config config = getConfigurationForRuntime();
        String filename = commandLine.getOptionValue("import-library");
        LibraryManager libManager = new LibraryManager(config);
        boolean success = libManager.importExistingLibrary(filename);
        if (!success) {
            System.out.println("[!] An error occured while importing your library, please try again.");
        }
    }

    private static void handleHeadlessMode(CommandLine commandLine) { // Start headless and direct user to local site
    }

    private static boolean handlePlayingSingleSong(String songName) {
        Config configuration = new Config();
        configuration.setOS(os);
        Core streamlineBackend = new Core(configuration);
        Song song = streamlineBackend.getSongFromName(songName);
        if (song == null) {
            return false;
        } else {
            streamlineBackend.playSong(song);
            try {
                streamlineBackend.audioThread.join();
                return true;
            } catch (InterruptedException iE) {
                logger.log(Level.WARNING, "[!] An error occured during playback, please try again.");
            }
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
        Core streamlineBackend = new Core(configuration);
        TerminalInterface tui = new TerminalInterface(streamlineBackend);
        if (!tui.run()) {
            System.err.println(StreamLineMessages.FatalStartError.getMessage());
            return;
        }
    }

    private static void handleCacheManager() {
        Config config = new Config();
        config.setMode(Mode.CACHE_MANAGEMENT);
        config.setOS(os);
        Core streamlineBackend = new Core(config);
        streamlineBackend.handleCacheManagement();
    }

    private static void handlePlay(CommandLine commandLine) {
        Config config = getConfigurationForRuntime();
        String songName = commandLine.getOptionValue("play");
        boolean songWasAlreadyAvailable = handlePlayingSingleSong(songName);
        if (!songWasAlreadyAvailable) {
            try {
                RetrievedStorage results = new Core(config).doSearch("Cold - Give");
                String videoId = results.getArrayOfSongs()[0].getSongVideoId();
                String url = new YoutubeHandle(config, logger).getAudioUrlFromVideoId(videoId);
                new AudioPlayer().playSongFromUrl(url);
            } catch (Exception e) {
                System.out.println("[!] An error occured during song playback, please try restarting the app.");
            }
        }
    }

    public static void handleDockerSetup() {
        DockerManager.cloneInvidiousRepo(logger);
        boolean didWrite = DockerManager.writeDockerCompose(logger);
        if (!didWrite) {
            System.out.println(StreamLineMessages.ErrorWritingToDockerCompose.getMessage());
        }
        if (DockerManager.buildInstance(logger)) {
            System.out.println("\nInvidious image built successfully!\n");
        } else {
            System.out.println(StreamLineMessages.InvidiousBuildError.getMessage());
        }
        System.exit(0);
    }

    public static void handleYoutubeSetup() {
        Config config = getConfigurationForRuntime();
        if (YoutubeHandle.setupYoutubeInterop(config)) {
            System.out.println("yt-dlp has been downloaded.");
        } else {
            System.out.println("An error was encountered while setting up yt-dlp.");
        }
        System.exit(0);
    }

    private static Config getConfigurationForRuntime() {
        Config config = new Config();
        config.setMode(Mode.TERMINAL);

        config.setOS(os);

        if (logger == null) {
            System.err.println(StreamLineMessages.LoggerInitializationFailure.getMessage());
            System.exit(0);
        } else {
            config.setLogger(logger);
        }

        config.setAudioSource('y');
        config.setBinaryPath(getBinaryPath(config));

        return config;
    }

    private static Config getConfigurationForRuntime(CommandLine commandLine) {
        Config config = getConfigurationForRuntime();
        if (commandLine.hasOption("youtube")) {
            config.setAudioSource('y');
            config.setIsOnline(true);
            config.setHost(StreamLineConstants.YOUTUBE_HOST);
        } else {
            config.setAudioSource('d');
            String apiHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker(logger);
            if (apiHost == null || apiHost.length() < 1) {
                new Thread(() -> {
                    DockerManager.startInvidiousContainer(logger);
                }).start();
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

    private static Logger initializeLogger(OS os) {
        Logger logger = Logger.getLogger("Streamline"); 
        String logFileDir = switch (os) {
            case WINDOWS -> StreamLineConstants.WINDOWS_TEMP_DIR_PATH;
            default -> StreamLineConstants.OTHER_OS_TEMP_DIR_PATH;
        };
        String fileName = "streamline.log";
        File logFile = new File(logFileDir);
        if (!logFile.exists() && !logFile.mkdir()) {
            System.err.println("Unable to create tmp directory for log file.");
            return null;
        }
        logFile = new File(logFileDir + fileName);
        FileHandler fileHandle;
        try {
            if (logFile.exists() && logFile.isFile()) {
                new FileWriter(logFile, false).close();
            }
            fileHandle = new FileHandler(logFile.getPath(), true);
            while (logger.getHandlers().length > 0) {
                logger.removeHandler(logger.getHandlers()[0]);
            }
            logger.addHandler(fileHandle);
            fileHandle.setLevel(Level.INFO);
            XMLFormatter xF = new XMLFormatter();
            fileHandle.setFormatter(xF);
            logger.log(Level.INFO, "Log initialized.");
        } catch (IOException iE) {
            iE.printStackTrace();
            logger.log(Level.WARNING, "Could not properly setup logging for program.");
            return null;
        }
        return logger;
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
