package com.walit.streamline.backend;

import com.walit.streamline.audio.Song;
import com.walit.streamline.backend.job.*;
import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class JobDispatcher {
    
    private final Config config;
    private final ConcurrentHashMap<String, StreamLineJob> activeJobs;
    private final ExecutorService jobExecutor;

    public JobDispatcher(Config config) {
        this.config = config;
        this.activeJobs = new ConcurrentHashMap<>();
        this.jobExecutor = Executors.newCachedThreadPool();

        initializeServices();
    }

    private void initializeServices() {
        submitJob(new DatabaseConnectionJob(config));
        submitJob(new CacheInitializationJob(config));
        setShutdownHandler();
    }

    public <T extends StreamLineJob> void submitJob(T job) {
        activeJobs.put(job.getJobId(), job);
        jobExecutor.submit(() -> {
            try {
                job.start();
            } finally {
                if (job.isCompleted()) {
                    activeJobs.remove(job.getJobId());
                }
            }
        });
    }

    public RetrievedStorage doSearch(String searchTerm) {
        SearchJob searchJob = new SearchJob(config, searchTerm);
        submitJob(searchJob);

        while (!searchJob.isCompleted() && searchJob.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return searchJob.getResults();
    }

    public void playSong(Song song) {
        submitJob(new SongPlaybackJob(config, song));;
    }

    public void playQueue(RetrievedStorage songQueue) {
        submitJob(new QueuePlaybackJob(config, songQueue));
    }

    public void cleanCache() {
        submitJob(new CacheClearJob(config));
    }

    private void setShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        for (StreamLineJob job : activeJobs.values()) {
            job.cancel();
        }

        submitJob(new DatabaseShutdownJob(config));

        if (DockerManager.containerIsAlive()) {
            submitJob(new DockerShutdownJob(config));
        }

        jobExecutor.shutdown();
    }
}
