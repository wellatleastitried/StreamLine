package com.streamline.backend.jobs;

import com.streamline.audio.AudioPlayer;
import com.streamline.audio.Song;
import com.streamline.utilities.internal.Config;

public class SongPlaybackJob extends AbstractStreamLineJob {

    private final Song song;

    public SongPlaybackJob(Config config, Song song) {
        super(config);
        this.song = song;
    }

    public void execute() {
        AudioPlayer audioPlayer = new AudioPlayer(song);
        audioPlayer.run();
        finish();
    }
}
