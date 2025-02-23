package com.walit.streamline.Interface;

import com.walit.streamline.Backend.Core;
import com.walit.streamline.Utilities.RetrievedStorage;

public abstract class FrontendInterface {

    public final Core backend;

    public FrontendInterface(Core backend) {
        this.backend = backend;
    }

    public abstract boolean run();

    public abstract void shutdown();

    public void logInfo(String message) {
        backend.logInfo(message);
    }

    public void logWarning(String message) {
        backend.logWarning(message);
    }

    public void logSevere(String message) {
        backend.logSevere(message);
    }

    public RetrievedStorage fetchSearchResults(String search) {
        return backend.doSearch(search);
    }
}
