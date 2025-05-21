package com.streamline.backend.jobs;

import com.streamline.audio.AudioPlayer;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

public class QueuePlaybackJob extends AbstractStreamLineJob {

    private final RetrievedStorage queue;

    public QueuePlaybackJob(Config config, RetrievedStorage queue) {
        super(config);
        this.queue = queue;
    }

    @Override
    public void execute() {
        AudioPlayer audioPlayer = new AudioPlayer(queue);
        audioPlayer.run();
        finish();
    }
}
