package com.walit.streamline.Utilities.Internal;

import java.util.logging.Logger;

import com.walit.streamline.Hosting.DockerManager;

public class Config {

    private Mode mode;
    private OS os;
    private boolean isOnline;
    private String host;
    private Logger logger;
    private DockerManager dockerManager;

    public Config() {}

    public Config(Mode mode) {
        this.mode = mode;
    }

    public Config(OS os) {
        this.os = os;
    }

    public Config(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Config(String host) {
        this.host = host;
    }

    public Config(Logger logger) {
        this.logger = logger;
    }

    public Config(DockerManager dockerManager) {
        this.dockerManager = dockerManager;
    }

    public Config(Mode mode, OS os, boolean isOnline, Logger logger, DockerManager dockerManager) {
        this.mode = mode;
        this.os = os;
        this.isOnline = isOnline;
        this.logger = logger;
        this.dockerManager = dockerManager;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setOS(OS os) {
        this.os = os;
    }

    public OS getOS() {
        return os;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setDockerConnection(DockerManager dockerManager) {
        this.dockerManager = dockerManager;
    }

    public DockerManager getDockerConnection() {
        return dockerManager;
    }
}
