package com.walit.streamline.frontend;

import com.walit.streamline.backend.Dispatcher;
import com.walit.streamline.backend.JobDispatcher;
import com.walit.streamline.utilities.RetrievedStorage;

abstract class FrontendInterface {

    public final JobDispatcher backend;

    public FrontendInterface(JobDispatcher backend) {
        this.backend = backend;
    }

    public abstract boolean run();

    public abstract void shutdown();

    public RetrievedStorage fetchSearchResults(String search) {
        return backend.doSearch(search);
    }
}
