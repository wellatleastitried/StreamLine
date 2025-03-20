package com.streamline.backend.jobs;

import com.streamline.utilities.internal.Config;

/**
 * Base class for backend jobs that will be needed during runtime.
 * @author wellatleastitried
 */
public abstract class StreamLineJob {

    protected final Config config;
    protected boolean isCompleted = false;
    protected boolean isRunning = false;
    protected String jobId;

    /**
     * Set configuration and job id.
     * @param config The configuration being used for the user's runtime.
     */
    public StreamLineJob(Config config) {
        this.config = config;
        this.jobId = generateJobId();
    }

    /**
     * Generate a unique identifier for a given job.
     * @return The identifier for the specific job.
     */
    protected String generateJobId() {
        return this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
    }

    /**
     * The starting point of the job's logic that will be executed when the job is executed.
     */
    public abstract void execute();

    /**
     * This method starts the job.
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            execute();
        }
    }

    /**
     * End the job's execution early.
     */
    public void cancel() {
        isRunning = false;
    }

    /**
     * Monitor the progress of the job's execution.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Check whether the job is currently running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Retrieve the previously generated job identifier.
     */
    public String getJobId() {
        return jobId;
    }

    protected void finish() {
        isRunning = false;
        isCompleted = true;
    }
}
