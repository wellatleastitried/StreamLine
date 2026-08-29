package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

public class RecentlyPlayedSongsFetchJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public RecentlyPlayedSongsFetchJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
    }

    @Override
    public void execute() {
        try {
            results = dbRunner.getRecentlyPlayedSongs();
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
