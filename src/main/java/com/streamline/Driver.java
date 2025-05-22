package com.streamline;

import com.streamline.utilities.RuntimeManager;
import com.streamline.utilities.SetupManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Entry point for the app. Handles the user input and determines the appropriate runtime configuration for the app.
 * @author wellatleastitried
 */
public final class Driver {

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
        options.addOption("i", "install", false, "Install StreamLine to your system's PATH for global access");
        options.addOption("u", "uninstall", false, "Uninstall StreamLine from your system");
        options.addOption("d", "docker", false, "Start StreamLine with an Invidious docker instance being used in the backend");
        options.addOption("y", "youtube", false, "Start StreamLine with yt-dlp being used in the backend. [THIS IS THE DEFAULT IF NO BACKEND IS SPECIFIED]");
        options.addOption("np", "now-playing", false, "Return the name and artist of the song that is currently playing (no output if there is no song playing)");
        options.addOption("c", "clean", true, "Remove unwanted files from the Docker or Youtube install.\nExample:\n\tstreamline --Docker\t=> Removes the Invidious repository from the filesystem\n\n\tstreamline --YouTube\t=> Removes the binary for yt-dlp from the filesystem");
        options.addOption("il", "import-library", true, "Import your music library from other devices into your current setup and then exit (e.g., --import-library=/path/to/library.json");
        options.addOption("el", "export-library", false, "Generate a file (library.json) that contains all of your music library that can be used to import this library on another device and then exit");
        options.addOption("p", "play", true, "Play a single song (e.g., --play=\"songname\") and start headless with CLI commands available");
        options.addOption("cm", "cache-manager", false, "Clear the application's cache from the system to free up storage");
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
            if (arguments.length < 1) RuntimeManager.standardRuntime(commandLine);
            else if (commandLine.hasOption("setup")) SetupManager.setupApi(commandLine);
            else if (commandLine.hasOption("help")) printHelpCli(options);
            else if (commandLine.hasOption("install")) SetupManager.installStreamLine();
            else if (commandLine.hasOption("uninstall")) SetupManager.uninstallStreamLine();
            else if (commandLine.hasOption("docker") || commandLine.hasOption("youtube")) RuntimeManager.standardRuntime(commandLine);
            else if (commandLine.hasOption("clean")) RuntimeManager.cleaningProcess(commandLine);
            else if (commandLine.hasOption("np")) displayCurrentlyPlayingSong();
            else if (commandLine.hasOption("import-library")) RuntimeManager.libraryImport(commandLine);
            else if (commandLine.hasOption("export-library")) RuntimeManager.libraryExport();
            else if (commandLine.hasOption("play")) RuntimeManager.playSong(commandLine);
            else if (commandLine.hasOption("cache-manager")) RuntimeManager.cacheManager();
            else System.out.println("[!] Invalid or no arguments provided. Use --help for usage information.");
        } catch (ParseException pE) {
            System.err.println("[!] Error parsing command line arguments: " + pE.getMessage());
            printHelpCli(options);
        }
    }

    private static void displayCurrentlyPlayingSong() {
        System.out.println("Not yet implemented.");
    }
}
