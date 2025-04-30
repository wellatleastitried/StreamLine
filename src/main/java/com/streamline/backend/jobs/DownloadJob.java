package com.streamline.backend.jobs;

import com.streamline.audio.Song;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class DownloadJob extends AbstractStreamLineJob {

    private final Song song;

    public DownloadJob(Config config, Song song) {
        super(config);
        this.song = song;
    }

    @Override
    public void execute() {
        Logger.debug("DownloadJob: Not yet implemented");
    }

}
