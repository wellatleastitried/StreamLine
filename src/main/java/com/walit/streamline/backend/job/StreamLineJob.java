package com.walit.streamline.backend.job;

import com.walit.streamline.utilities.internal.Config;

public abstract class StreamLineJob {

    protected final Config config;
    protected boolean isCompleted = false;
    protected boolean isRunning = false;
    protected String jobId;

    public StreamLineJob(Config config) {
        this.config = config;
        this.jobId = generateJobId();
    }

    protected String generateJobId() {
        return this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
    }

    public abstract void execute();

    public void start() {
        if (!isRunning) {
            isRunning = true;
            execute();
        }
    }

    public void cancel() {
        isRunning = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isRunning() {
        return isRunning();
    }

    public String getJobId() {
        return jobId;
    }
}
