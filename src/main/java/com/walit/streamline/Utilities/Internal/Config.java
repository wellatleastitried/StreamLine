package com.walit.streamline.Utilities.Internal;

import java.util.logging.Logger;

public class Config {

    private Mode mode;
    private OS os;
    private volatile boolean isOnline;
    private String host;
    private Logger logger;

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

    public Config(Mode mode, OS os, boolean isOnline, Logger logger) {
        this.mode = mode;
        this.os = os;
        this.isOnline = isOnline;
        this.logger = logger;
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

    public synchronized void setHost(String host) {
        this.host = host;
    }

    public synchronized String getHost() {
        return host;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }
}
