package com.streamline.utilities.internal;

import com.streamline.backend.ConnectionHandle;

import java.util.logging.Logger;

/**
 * The app's core configuration settings.
 * @author wellatleastitried
 */
public class Config {

    private Mode mode;
    private OS os;
    private volatile boolean isOnline;
    private String host;
    private char audioSource;
    private ConnectionHandle apiHandle;
    private String binaryPath;

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

    public Config(char audioSource) {
        this.audioSource = audioSource;
    }

    public Config(ConnectionHandle apiHandle) {
        this.apiHandle = apiHandle;
    }

    public Config(Mode mode, OS os, boolean isOnline, Logger logger, char audioSource) {
        this.mode = mode;
        this.os = os;
        this.isOnline = isOnline;
        this.audioSource = audioSource;
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

    public void setAudioSource(char audioSource) {
        this.audioSource = audioSource;
    }

    public char getAudioSource() {
        return audioSource;
    }

    public void setHandle(ConnectionHandle apiHandle) {
        this.apiHandle = apiHandle;
    }

    public ConnectionHandle getHandle() {
        return apiHandle;
    }

    public void setBinaryPath(String binaryPath) {
        this.binaryPath = binaryPath;
    }

    public String getBinaryPath() {
        return binaryPath;
    }
}
