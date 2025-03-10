package com.walit.streamline.backend.job;

import com.walit.streamline.audio.Song;
import com.walit.streamline.utilities.internal.Config;

public class SongPlaybackJob extends StreamLineJob {

    private final Song song;

    public SongPlaybackJob(Config config, Song song) {
        super(config);
        this.song = song;
    }

    public void execute() {}
}
