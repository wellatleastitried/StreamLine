package com.walit.streamline.frontend;

import com.walit.streamline.backend.Dispatcher;
import com.walit.streamline.utilities.RetrievedStorage;

abstract class FrontendInterface {

    public final Dispatcher backend;

    public FrontendInterface(Dispatcher backend) {
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
