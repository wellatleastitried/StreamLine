package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class DownloadedSongsFetchJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public DownloadedSongsFetchJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
    }

    @Override
    public void execute() {
        try {
            results = dbRunner.getDownloadedSongs();
            Logger.debug("Number of downloaded songs fetched: " + results.size());
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
