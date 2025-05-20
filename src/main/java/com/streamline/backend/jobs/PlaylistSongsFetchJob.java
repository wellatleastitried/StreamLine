package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class PlaylistSongsFetchJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;
    private final int playlistId;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public PlaylistSongsFetchJob(Config config, DatabaseRunner dbRunner, int playlistId) {
        super(config);
        this.dbRunner = dbRunner;
        this.playlistId = playlistId;
    }

    public void execute() {
        try {
            results = dbRunner.getSongsFromPlaylist(playlistId);
            Logger.debug("Number of songs fetched from playlist: " + results.size());
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
