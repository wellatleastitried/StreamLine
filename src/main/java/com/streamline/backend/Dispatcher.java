package com.streamline.backend;

import com.streamline.audio.Song;
import com.streamline.backend.handle.*;
import com.streamline.backend.jobs.*;
import com.streamline.database.DatabaseLinker;
import com.streamline.database.DatabaseRunner;
import com.streamline.database.utils.QueryLoader;
import com.streamline.utilities.CacheManager;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.tinylog.Logger;

/**
 * Schedules various jobs to act as runners for the front-end.
 * @author wellatleastitried
 */
public final class Dispatcher {
    
    public final Config config;
    private final ConcurrentHashMap<String, AbstractStreamLineJob> activeJobs;
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

    public <T extends AbstractStreamLineJob> String submitJob(T job) {
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
            Thread.onSpinWait();
        }
        Logger.debug("Job is no longer running.");

        RetrievedStorage results = searchJob.getResults();
        Logger.debug(results != null ? "results contains data" : "results is null");
        return results;
    }

    public void changeLanguage(String languageCode) {
        submitJob(new LanguageJob(config, languageCode));
    }

    public Song getSongFromName(String songName) {
        return dbRunner.searchForSongName(songName);
    }
    
    private void killCurrentAudioJobIfExists() {
        if (audioJobId != null) {
            AbstractStreamLineJob currentAudioJob = activeJobs.get(audioJobId);
            currentAudioJob.cancel();
        }
    }

    public RetrievedStorage getRecentlyPlayedSongs() {
        RecentlyPlayedSongsJob recPlayJob = new RecentlyPlayedSongsJob(config, dbRunner);
        submitJob(recPlayJob);

        while (!recPlayJob.resultsAreReady()) {
            Thread.onSpinWait();
        }
        Logger.debug("Job is no longer running.");

        return recPlayJob.getResults();
    }

    public void handleSongLikeStatus(Song song) {
        LikeSongJob likeJob = new LikeSongJob(config, dbRunner, song);
        submitJob(likeJob);
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

    public void shutdown() {
        /* Uncomment the line below if needed for debugging. */
        // getThreadStates();
        if (!exitedGracefully) {
            for (AbstractStreamLineJob job : activeJobs.values()) {
                job.cancel();
            }

            dbRunner.shutdown();

            if (DockerManager.containerIsAlive()) {
                DockerManager.stopContainer();
            }

            jobExecutor.shutdown();
            exitedGracefully = true;
        }
    }

    /*
     * This is a method that I am leaving for potential debugging in the future.
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
