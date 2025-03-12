package com.walit.streamline.backend;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.walit.streamline.audio.AudioPlayer;
import com.walit.streamline.audio.Song;
import com.walit.streamline.database.DatabaseLinker;
import com.walit.streamline.database.DatabaseRunner;
import com.walit.streamline.database.utils.QueryLoader;
import com.walit.streamline.utilities.CacheManager;
import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;
import com.walit.streamline.utilities.internal.StreamLineConstants;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import org.tinylog.Logger;

public final class Dispatcher {

    public Thread audioThread;

    private final DatabaseRunner dbRunner;
    private HashMap<String, String> queries;

    private final String cacheDirectory;

    private boolean exitedGracefully = false;

    private final Config config;

    public Dispatcher(Config config) {
        this.config = config;
        this.cacheDirectory = getCacheDirectory();
        setShutdownHandler();
        this.audioThread = null;
        this.queries = QueryLoader.getMapOfQueries();
        DatabaseLinker dbLink = initializeDatabaseConnection();
        this.dbRunner = new DatabaseRunner(dbLink.getConnection(), queries, dbLink);
        if (config.getAudioSource() == 'd') {
            config.setHandle(InvidiousHandle.getInstance(config));
        } else {
            config.setHandle(YoutubeHandle.getInstance(config));
        }
        clearExpiredCacheOnStartup();
        if (config.getAudioSource() != 'y' && !config.getIsOnline()) {
            checkIfConnectionEstablished();
        }
    }

    private DatabaseLinker initializeDatabaseConnection() {
        return new DatabaseLinker(config.getOS(), queries.get("INITIALIZE_TABLES"));
    }

    public void handleCacheManagement() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Would you like to:\n1) Clear all existing cache\n2) Clear expired cache\nEnter a 1 or 2 to choose: ");
            String response = scanner.nextLine();
            if (response.trim().equals("1")) {
                clearCache();
            } else if (response.trim().equals("2")) {
                clearExpiredCacheOnStartup();
            }
        }
    }

    private boolean canReachDocker() throws InterruptedException {
        return DockerManager.containerIsAlive() && DockerManager.canConnectToContainer();
    }

    private void checkIfConnectionEstablished() {
        Thread connectionTesting = new Thread(() -> {
            try {
                String reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
                boolean dockerIsResponding = canReachDocker();
                while (reachableHost == null && !dockerIsResponding) {
                    reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
                    if (reachableHost == null) {
                        dockerIsResponding = canReachDocker();
                    }
                    Thread.sleep(1000);
                }
                if (reachableHost != null) {
                    config.setHost(reachableHost);
                    config.setIsOnline(true);
                } else if (dockerIsResponding) {
                    config.setHost(StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS);
                    config.setIsOnline(true);
                }
            } catch (InterruptedException iE) {
                Logger.warn(StreamLineMessages.PeriodicConnectionTestingError.getMessage());
            }
        });
        connectionTesting.setDaemon(true);
        connectionTesting.start();
    }

    public RetrievedStorage doSearch(String searchTerm) {
        RetrievedStorage finalResults = new RetrievedStorage();
        config.getHandle().retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            if (searchResults != null) {
                for (int i = 0; i < searchResults.size(); i++) {
                    finalResults.add(i, searchResults.get(i));
                }
            } else {
                System.out.println("Unable to retrieve search results at this time.");
            }
        }).join();
        return finalResults.size() > 0 ? finalResults : null;
    }

    public static Process runCommandExpectWait(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            return process;
        } catch (IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return null;
        }
    }

    public static Process runCommandExpectWait(String[] splitCommand) {
        try {
            Process process = new ProcessBuilder(splitCommand).start();
            return process;
        } catch (IOException iE) {
            StringBuilder sB = new StringBuilder();
            Arrays.stream(splitCommand).forEach(str -> sB.append(str + " "));
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + sB.toString().trim());
            iE.printStackTrace();
            return null;
        }
    }
    
    public static boolean runCommand(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return false;
        }
    }

    public static boolean runCommand(String[] splitCommand) {
        try {
            Process process = new ProcessBuilder(splitCommand).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            StringBuilder sB = new StringBuilder();
            Arrays.stream(splitCommand).forEach(str -> sB.append(str));
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + sB.toString());
            return false;
        }
    }

    private void setShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
    }

    protected String getCacheDirectory() {
        return CacheManager.getCacheDirectory(config.getOS());
    }
        
    // Temporary function for getting TUI figured out
    public String testStatsCall() {
        InvidiousHandle handle = InvidiousHandle.getInstance(config);
        return handle.retrieveStats();
    }

    public Song getSongFromName(String songName) {
        return dbRunner.searchForSongName(songName);
    }

    public void playSong(Song song) {
        AudioPlayer audioPlayer = new AudioPlayer(song);
        audioThread = new Thread(audioPlayer);
        audioThread.start();
    }

    public void playQueue(RetrievedStorage songQueue) {
        AudioPlayer audioPlayer = new AudioPlayer(songQueue);
        audioThread = new Thread(audioPlayer);
        audioThread.start();
    }

    public void clearCache() {
        CacheManager.clearCache(cacheDirectory);
        dbRunner.clearCachedSongs();
    }

    private void clearExpiredCacheOnStartup() {
        CacheManager.clearExpiredCacheOnStartup(cacheDirectory, dbRunner.getExpiredCache());
        dbRunner.clearExpiredCache();
    }

    public void logSevere(String message) {
        Logger.error(message);
    }

    public void logWarning(String message) {
        Logger.warn(message);
    }

    public void logInfo(String message) {
        Logger.info(message);
    }

    public void shutdown() {
        if (!exitedGracefully) {
            if (dbRunner != null) {
                dbRunner.shutdown();
            }
            try {
                if (DockerManager.containerIsAlive()) {
                    DockerManager.stopContainer();
                }
            } catch (IllegalStateException iE) {
                Logger.warn(StreamLineMessages.IllegalStateExceptionInShutdown.getMessage() + iE.getMessage());
            }
            if (audioThread != null && audioThread.isAlive()) {
                audioThread.interrupt();
            }
            exitedGracefully = true;
            System.out.println(StreamLineMessages.Farewell.getMessage());
        }
    }

    /*
     * This is a method I am leaving for potential debugging in the future.
     */
    private void getThreadStates() {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        System.out.println("Currently running threads:");
        for (Map.Entry<Thread, StackTraceElement[]> entry : threads.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] sTE = entry.getValue();
            if (thread.getId() != 3) {
                System.out.println(thread.getName() + " (ID: " + thread.getId() + ") -> " + thread.getState());
            } else {
                System.out.println("Thread: " + thread.getName() + " (ID: " + thread.getId() + ") -> " + thread.getState());
                for (StackTraceElement element : sTE) {
                    System.out.println("\t" + element);
                }
            }
        }
    }
}
