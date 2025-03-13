package com.walit.streamline.backend.jobs;

import com.walit.streamline.audio.AudioPlayer;
import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;

public class QueuePlaybackJob extends StreamLineJob {

    private final RetrievedStorage queue;

    public QueuePlaybackJob(Config config, RetrievedStorage queue) {
        super(config);
        this.queue = queue;
    }

    public void execute() {
        AudioPlayer audioPlayer = new AudioPlayer(queue);
        audioPlayer.run();
        finish();
    }
}
