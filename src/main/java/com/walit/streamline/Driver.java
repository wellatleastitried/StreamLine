package com.walit.streamline;

import com.walit.streamline.Utilities.Internal.Mode;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
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
        options.addOption("i", "import-library", true, "Import your music library from other devices into your current setup and then exit (e.g., --import-library=/path/to/library.json");
        options.addOption("e", "export-library", false, "Generate a file (library.json) that contains all of your music library that can be used to import this library on another device and then exit");
        options.addOption("q", "quiet", true, "Headless start with ability to access the application at http://localhost:PORT");
        options.addOption("p", "play", true, "Play a single song (e.g., --play=\"songname\") and start headless with CLI commands available");
        options.addOption("d", "delete", true, "Removes all of the song names given from the database and/or the filesystem (e.g., --delete=\"song1,song2\"");
        options.addOption("cc", "clear-cache", false, "Clears all of the existing cache from the filesystem (Clearing up storage space)");
        options.addOption("cec", "clear-expired-cache", false, "Clears cache that has been unused on the filesystem for more than 15 days");

        CommandLine commandLine;

        try {
            commandLine = new DefaultParser().parse(options, args);
            if (commandLine.hasOption("help")) {
                printHelpCli(options);
                System.exit(1);
            } else if (commandLine.hasOption("import-library")) {
                // Do something
                System.exit(1);
            } else if (commandLine.hasOption("export-library")) {
                // Do something
                System.exit(1);
            } else if (commandLine.hasOption("quiet")) {
            } else if (commandLine.hasOption("play")) {
            } else if (commandLine.hasOption("delete")) {
            } else if (commandLine.hasOption("clear-cache")) {
                // Do something
                System.exit(1);
            } else if (commandLine.hasOption("clear-expired-cache")) {
                // Do something
                System.exit(1);
            } else {
                System.out.println("Invalid or no arguments provided. Use --help for usage information.");
                System.exit(1);
            }
        } catch (ParseException pE) {
            System.err.println("Error parsing command line arguments: " + pE.getMessage());
            printHelpCli(options);
            System.exit(1);
        }

        Core streamline = new Core(Mode.TERMINAL);
        if (!streamline.start()) {
            System.err.println(StreamLineMessages.FatalStartError.getMessage());
            System.exit(1);
        }
        System.out.println(StreamLineMessages.Farewell.getMessage());
        System.exit(0);
    }
}
