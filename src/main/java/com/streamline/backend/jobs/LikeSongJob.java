package com.streamline.backend.jobs;

import com.streamline.audio.Song;
import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class LikeSongJob extends AbstractStreamLineJob {

    private final DatabaseRunner dbRunner;
    private final Song song;

    public LikeSongJob(Config config, DatabaseRunner dbRunner, Song song) {
        super(config);
        this.dbRunner = dbRunner;
        this.song = song;
    }

    @Override
    public void execute() {
        if (dbRunner.handleLikeSong(song)) {
            boolean updatedStatus = dbRunner.getSongLikeStatus(song);
            song.setSongLikeStatus(updatedStatus);
        } else {
            Logger.error("Failed to like song: " + song.getSongName());
        }
    }
}
