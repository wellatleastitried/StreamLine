package com.streamline;

import java.util.Scanner;

import com.streamline.audio.AudioPlayer;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.backend.handle.YoutubeHandle;
import com.streamline.frontend.FrontendInterface;
import com.streamline.frontend.terminal.TerminalInterface;
import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.LibraryPeer;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.Mode;

import org.apache.commons.cli.CommandLine;

public class RuntimeManager {

    private static boolean exitedGracefully = false;
    private static FrontendInterface frontend;
    private static Dispatcher backend;

    private RuntimeManager() {}

    protected static void standardRuntime(CommandLine commandLine) {
        Config configuration;
        if (commandLine.hasOption("youtube") || commandLine.hasOption("docker")) {
            configuration = ConfigManager.getConfigurationForRuntime(commandLine);
        } else {
            configuration = ConfigManager.getConfigurationForRuntime();
        }
        Dispatcher streamlineBackend = new Dispatcher(configuration);
        TerminalInterface tui = new TerminalInterface(streamlineBackend);
        setShutdownHookParams(tui, streamlineBackend);
        if (!tui.run()) {
            System.err.println("[!] A fatal error has occured while starting StreamLine, please try reloading the app.");
            return;
        }
    }

    private static boolean handlePlayingSingleSong(Config config, String songName) {
        Dispatcher streamlineBackend = new Dispatcher(config);
        setShutdownHookParams(streamlineBackend);
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

    protected static void libraryExport() { // Transfer database entries to JSON file
        Config config = ConfigManager.getConfigurationForRuntime();
        LibraryPeer libManager = new LibraryPeer(config);
        String exportPath = libManager.exportExistingLibrary();
        if (exportPath != null) {
            System.out.println("[*] Your library has been exported to the file: " + exportPath);
        } else {
            System.out.println("[!] There was an error while exporting your library, please try again.");
        }
        System.exit(0);
    }

    protected static void libraryImport(CommandLine commandLine) { // Take in JSON file and fill database with entries
        Config config = ConfigManager.getConfigurationForRuntime();
        String filename = commandLine.getOptionValue("import-library");
        LibraryPeer libManager = new LibraryPeer(config);
        boolean success = libManager.importExistingLibrary(filename);
        if (!success) {
            System.out.println("[!] An error occured while importing your library, please try again.");
        }
        System.exit(0);
    }

    protected static void cleaningProcess(CommandLine commandLine) {
        Config config = ConfigManager.getConfigurationForRuntime();
        String installToClean = commandLine.getOptionValue("clean");
        if ("youtube".equals(installToClean)) {
            SetupManager.cleanYoutubeInstall(config);
        } else if ("docker".equals(installToClean)) {
            SetupManager.cleanDockerInstall(config);
        } else {
            System.out.println("[!] Invalid argument passed for --clean. Please specify either \"youtube\" or \"docker\"");
        }
        System.exit(0);
    }

    protected static void playSong(CommandLine commandLine) {
        Config config = ConfigManager.getConfigurationForRuntime();
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

    protected static void cacheManager() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Would you like to clear the existing cache? Enter y or n: ");
            String response = scanner.nextLine();
            if (response.toLowerCase().trim().equals("y")) {
                Config config = new Config();
                config.setMode(Mode.CACHE_MANAGEMENT);
                config.setOS(ConfigManager.getOSOfUser());
                new Dispatcher(config).clearCache();
            }
        }
    }

    public static void shutdown() {
        if (!exitedGracefully) {
            if (backend != null) {
                backend.shutdown();
            }
            if (frontend != null) {
                frontend.shutdown();
            }
            System.out.println("[*] " + LanguagePeer.getText("app.goodbye"));
            exitedGracefully = true;
        }
    }

    private static void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));
    }

    private static <T extends FrontendInterface> void setShutdownHookParams(T ui, Dispatcher runner) {
        frontend = ui;
        backend = runner;
        setShutdownHook();
    }

    private static void setShutdownHookParams(Dispatcher runner) {
        backend = runner;
        setShutdownHook();
    }
}
