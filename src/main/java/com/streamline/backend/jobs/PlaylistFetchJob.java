package com.streamline.backend.jobs;

import com.streamline.audio.Playlist;
import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.internal.Config;

import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

public class PlaylistFetchJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;

    private List<Playlist> results = null;

    private boolean resultsAreReady = false;

    public PlaylistFetchJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
        this.results = new ArrayList<>();
    }

    @Override
    public void execute() {
        try {
            results = dbRunner.getPlaylists();
            Logger.debug("Number of playlists fetched: " + results.size());
        } finally {
            resultsAreReady = true;
        }
    }

    public synchronized List<Playlist> getResults() {
        finish();
        return results;
    }

    public boolean resultsAreReady() {
        return resultsAreReady;
    }
}
