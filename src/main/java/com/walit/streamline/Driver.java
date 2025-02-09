package com.walit.streamline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.XMLFormatter;

import com.walit.streamline.Communicate.InvidiousHandle;
import com.walit.streamline.Hosting.DockerManager;
import com.walit.streamline.Utilities.Internal.Config;
import com.walit.streamline.Utilities.Internal.Mode;
import com.walit.streamline.Utilities.Internal.OS;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class Driver {

    private static void printHelpCli(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("StreamLine", options);
    }

    public static void main(String [] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Help menu explaining the different flags");
        options.addOption("s", "setup", false, "Initialize the configuration for a locally hosted invidious instance");
        options.addOption("i", "import-library", true, "Import your music library from other devices into your current setup and then exit (e.g., --import-library=/path/to/library.json");
        options.addOption("e", "export-library", false, "Generate a file (library.json) that contains all of your music library that can be used to import this library on another device and then exit");
        options.addOption("q", "quiet", true, "Headless start with ability to access the application at http://localhost:PORT");
        options.addOption("p", "play", true, "Play a single song (e.g., --play=\"songname\") and start headless with CLI commands available");
        options.addOption("d", "delete", true, "Removes all of the song names given from the database and/or the filesystem (e.g., --delete=\"song1,song2\"");
        options.addOption("cm", "cache-manager", false, "Choose whether to clear all cache or only expired cache and then exit");

        CommandLine commandLine;

        try {
            commandLine = new DefaultParser().parse(options, args);
            if (args.length < 1) {
                Config configuration = getConfigurationForRuntime();
                Core streamline = new Core(configuration);
                if (!streamline.start()) {
                    System.err.println(StreamLineMessages.FatalStartError.getMessage());
                    System.exit(1);
                }
            } else if (commandLine.hasOption("setup")) {
                // Create API tokens, initialize docker-compose
                DockerManager.cloneInvidiousRepo();
                boolean didWrite = DockerManager.writeDockerCompose();
                if (!didWrite) {
                    System.out.println(StreamLineMessages.ErrorWritingToDockerCompose.getMessage());
                }
                System.exit(0);
            } else if (commandLine.hasOption("help")) {
                printHelpCli(options);
                System.exit(0);
            } else if (commandLine.hasOption("import-library")) {
                // Take in JSON file and fill database with entries
                System.exit(0);
            } else if (commandLine.hasOption("export-library")) {
                // Transfer database entries to JSON file
                System.exit(0);
            } else if (commandLine.hasOption("quiet")) {
                // Start headless and direct user to local site
            } else if (commandLine.hasOption("play")) {
                // Play a single song
            } else if (commandLine.hasOption("delete")) {
                // Delete a song from library/playlist/etc
            } else if (commandLine.hasOption("cache-manager")) {
                Config config = new Config();
                config.setMode(Mode.CACHE_MANAGEMENT);
                config.setOS(getOSOfUser());
                new Core(config);
                System.exit(0);
            } else {
                System.out.println("Invalid or no arguments provided. Use --help for usage information.");
                System.exit(1);
            }
        } catch (ParseException pE) {
            System.err.println("Error parsing command line arguments: " + pE.getMessage());
            printHelpCli(options);
            System.exit(1);
        }

        System.out.println(StreamLineMessages.Farewell.getMessage());
    }

    private static Config getConfigurationForRuntime() {
        Config config = new Config();
        config.setMode(Mode.TERMINAL);

        OS os = getOSOfUser();
        config.setOS(os);

        Logger logger = initializeLogger(os);
        if (logger == null) {
            System.err.println(StreamLineMessages.LoggerInitializationFailure.getMessage());
        } else {
            config.setLogger(logger);
        }

        String apiHost = InvidiousHandle.canConnectToAPI();
        if (apiHost == null || apiHost.length() < 1) {
            DockerManager dockerManager = new DockerManager(logger);
            config.setDockerConnection(dockerManager);
            new Thread(() -> {
                String dockerHost = dockerManager.startInvidiousContainer();
            }).start();
            config.setIsOnline(false);
            config.setHost(null);
        } else {
            config.setIsOnline(true);
            config.setHost(apiHost);
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
            case WINDOWS -> "%temp%\\Streamline\\";
            default -> "/tmp/StreamLine/";
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
}
