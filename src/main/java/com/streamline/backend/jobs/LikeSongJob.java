package com.streamline.backend.jobs;

import com.streamline.audio.Song;
import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.internal.Config;

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
        dbRunner.handleLikeSong(song);
        song.setSongLikeStatus(!song.isSongLiked());
    }
}
