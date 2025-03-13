package com.walit.streamline.backend;

import com.walit.streamline.audio.Song;
import com.walit.streamline.backend.jobs.*;
import com.walit.streamline.database.DatabaseLinker;
import com.walit.streamline.database.DatabaseRunner;
import com.walit.streamline.database.utils.QueryLoader;
import com.walit.streamline.utilities.CacheManager;
import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;
import com.walit.streamline.utilities.internal.*;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.tinylog.Logger;

public final class Dispatcher {
    
    private final Config config;
    private final ConcurrentHashMap<String, StreamLineJob> activeJobs;
    private final ExecutorService jobExecutor;

    private final DatabaseRunner dbRunner;

    private String audioJobId = null;

    private boolean exitedGracefully = false;

    public Dispatcher(Config config) {
        this.config = config;
        this.activeJobs = new ConcurrentHashMap<>();
        this.jobExecutor = Executors.newCachedThreadPool();
        this.dbRunner = initializeDatabaseConnection();
        initializeServices();
    }

    private void initializeServices() {
        submitJob(new CacheInitializationJob(config, dbRunner));
        setShutdownHandler();
        config.setHandle(getConnectionHandle());
        submitJob(new ConnectionMonitorJob(config));
    }

    private ConnectionHandle getConnectionHandle() {
        if (config.getAudioSource() == 'd') {
            return new InvidiousHandle(config);
        }
        return new YoutubeHandle(config);
    }

    private DatabaseRunner initializeDatabaseConnection() {
        Map<String, String> queries = QueryLoader.getMapOfQueries();
        DatabaseLinker linker = new DatabaseLinker(config.getOS(), queries.get("INITIALIZE_TABLES"));
        return new DatabaseRunner(linker.getConnection(), QueryLoader.getMapOfQueries(), linker);
    }

    public <T extends StreamLineJob> String submitJob(T job) {
        String id = job.getJobId();
        activeJobs.put(id, job);
        jobExecutor.submit(() -> {
            try {
                job.start();
            } finally {
                if (job.isCompleted()) {
                    activeJobs.remove(job.getJobId());
                    Logger.debug("Remove job: {}", job.getJobId());
                }
            }
        });
        return id;
    }

    public RetrievedStorage doSearch(String searchTerm) {
        SearchJob searchJob = new SearchJob(config, searchTerm);
        submitJob(searchJob);

        while (!searchJob.resultsAreReady()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        Logger.debug("Job is no longer running.");

        RetrievedStorage test = searchJob.getResults();
        org.tinylog.Logger.debug(test != null ? "results contains data" : "results is null");
        return test;
    }

    public Song getSongFromName(String songName) {
        return dbRunner.searchForSongName(songName);
    }
    
    private void killCurrentAudioJobIfExists() {
        if (audioJobId != null) {
            StreamLineJob currentAudioJob = activeJobs.get(audioJobId);
            currentAudioJob.cancel();
        }
    }

    public void playSong(Song song) {
        killCurrentAudioJobIfExists();
        audioJobId = submitJob(new SongPlaybackJob(config, song));;
    }

    public void playQueue(RetrievedStorage songQueue) {
        killCurrentAudioJobIfExists();
        audioJobId = submitJob(new QueuePlaybackJob(config, songQueue));
    }

    public void clearCache() {
        submitJob(new CacheClearJob(config, dbRunner, CacheManager.getCacheDirectory(config.getOS())));
    }

    private void setShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        // getThreadStates();
        if (!exitedGracefully) {
            for (StreamLineJob job : activeJobs.values()) {
                job.cancel();
            }

            dbRunner.shutdown();

            if (DockerManager.containerIsAlive()) {
                DockerManager.stopContainer();
            }

            jobExecutor.shutdown();
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
