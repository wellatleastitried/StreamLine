package com.walit.streamline.backend.job;

import com.walit.streamline.utilities.internal.Config;

public class DatabaseShutdownJob extends StreamLineJob {

    public DatabaseShutdownJob(Config config) {
        super(config);
    }

    public void execute() {}
}
