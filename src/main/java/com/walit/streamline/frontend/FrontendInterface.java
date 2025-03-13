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

    public RetrievedStorage fetchSearchResults(String search) {
        return backend.doSearch(search);
    }
}
