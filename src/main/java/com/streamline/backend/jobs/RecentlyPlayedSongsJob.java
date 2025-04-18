package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

public class RecentlyPlayedSongsJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public RecentlyPlayedSongsJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
    }

    public void execute() {
        final RetrievedStorage finalResults = new RetrievedStorage();
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
