package com.walit.streamline.Backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.walit.streamline.Audio.AudioPlayer;
import com.walit.streamline.Communicate.InvidiousHandle;
import com.walit.streamline.Interact.DatabaseLinker;
import com.walit.streamline.Interact.DatabaseRunner;
import com.walit.streamline.Hosting.DockerManager;
import com.walit.streamline.Utilities.CacheManager;
import com.walit.streamline.Utilities.RetrievedStorage;
import com.walit.streamline.Utilities.StatementReader;
import com.walit.streamline.Utilities.Internal.Config;
import com.walit.streamline.Utilities.Internal.StreamLineConstants;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

public final class Core {

    private final Logger logger;

    private final DatabaseLinker dbLink;
    private final DatabaseRunner dbRunner;
    private final InvidiousHandle apiHandle;
    private HashMap<String, String> queries;

    private final String cacheDirectory;

    private boolean exitedGracefully = false;

    private final Config config;

    public Core(Config config) {
        this.config = config;
        this.logger = config.getLogger();
        this.cacheDirectory = getCacheDirectory();
        setShutdownHandler();
        this.queries = Core.getMapOfQueries(logger);
        this.dbLink = initializeDatabaseConnection();
        this.dbRunner = new DatabaseRunner(dbLink.getConnection(), queries, logger);
        this.apiHandle = initializeAPI();
        clearExpiredCacheOnStartup();
        if (!config.getIsOnline()) {
            checkIfConnectionEstablished();
        }
    }

    private DatabaseLinker initializeDatabaseConnection() {
        return new DatabaseLinker(config.getOS(), queries.get("INITIALIZE_TABLES"), logger);
    }

    private InvidiousHandle initializeAPI() {
        return InvidiousHandle.getInstance(config, logger);
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
        return DockerManager.containerIsAlive() && DockerManager.canConnectToContainer(logger);
    }

    private void checkIfConnectionEstablished() {
        Thread connectionTesting = new Thread(() -> {
            try {
                String reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker(logger);
                boolean dockerIsResponding = canReachDocker();
                while (reachableHost == null && !dockerIsResponding) {
                    reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker(logger);
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
                logger.log(Level.WARNING, StreamLineMessages.PeriodicConnectionTestingError.getMessage());
            }

        });
        connectionTesting.setDaemon(true);
        connectionTesting.start();
    }

    public RetrievedStorage doSearch(String searchTerm) {
        RetrievedStorage finalResults = new RetrievedStorage();
        apiHandle.retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            if (searchResults != null) {
                for (int i = 0; i < searchResults.size(); i++) {
                    finalResults.add(i, searchResults.get(i));
                }
            } else {
                System.out.println("Unable to reach the API at this time.");
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

    private void setShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
    }

    protected String getCacheDirectory() {
        switch (config.getOS()) {
            case WINDOWS:
                return StreamLineConstants.WINDOWS_CACHE_ADDRESS;
            case MAC:
                return StreamLineConstants.MAC_CACHE_ADDRESS;
            case LINUX:
            default:
                return StreamLineConstants.LINUX_CACHE_ADDRESS;
        }
    }
        
    /**
     * Reaches out to the SQL files in the resources folder that house the queries needed at runtime.
     * @return Map containing the full queries with a key for easy access
     */
    public static HashMap<String, String> getMapOfQueries(Logger logger) {
        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("INITIALIZE_TABLES", StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
            map.put("CLEAR_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearCachedSongs.sql"));
            map.put("CLEAR_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearExpiredCache.sql"));
            map.put("GET_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/queries/GetExpiredCache.sql"));
            map.put("GET_LIKED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForLikedMusicScreen.sql"));
            map.put("GET_DOWNLOADED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForDownloadedScreen.sql"));
            map.put("GET_RECENTLY_PLAYED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForRecPlayedScreen.sql"));
            map.put("ENSURE_RECENTLY_PLAYED_SONG_COUNT", StatementReader.readQueryFromFile("/sql/updates/UpdateRecentlyPlayed.sql"));
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.SQLFileReadError.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.MissingConfigurationFiles.getMessage());
            System.exit(1);
        }
        if (map.isEmpty()) {
            logger.log(Level.SEVERE, StreamLineMessages.DatabaseQueryCollectionError.getMessage());
            System.exit(1);
        }
        return map;
    }

    // Temporary function for getting TUI figured out
    public String testStatsCall() {
        InvidiousHandle handle = InvidiousHandle.getInstance(config, logger);
        return handle.retrieveStats();
    }

    private void playQueue(RetrievedStorage songQueue) {
        // Something like this...
        AudioPlayer audioPlayer = new AudioPlayer(songQueue);
        // new Thread(() -> audioPlayer).start(); OR
        // CompletableFuture.runAsync(() -> audioPlayer);
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
        logger.log(Level.SEVERE, message);
    }

    public void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public void shutdown() {
        if (!exitedGracefully) {
            if (dbLink != null) {
                dbLink.shutdown();
            }
            try {
                if (canReachDocker()) {
                    DockerManager.stopContainer(logger);
                }
            } catch (InterruptedException iE) {
                logger.log(Level.SEVERE, StreamLineMessages.UnexpectedErrorInShutdown.getMessage());
            } catch (IllegalStateException iE) {
                logger.log(Level.WARNING, StreamLineMessages.IllegalStateExceptionInShutdown.getMessage() + iE.getMessage());
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
