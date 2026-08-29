package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class LikedSongsFetchJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public LikedSongsFetchJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
    }

    @Override
    public void execute() {
        try {
            results = dbRunner.getLikedSongs();
            Logger.debug("Number of liked songs fetched: " + results.size());
        } finally {
            resultsAreReady = true;
        }
    }

    public synchronized RetrievedStorage getResults() {
        finish();
        return results;
    }

    public boolean resultsAreReady() {
        return resultsAreReady;
    }
}
