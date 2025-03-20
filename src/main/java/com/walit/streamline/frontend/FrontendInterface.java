package com.walit.streamline.frontend;

import com.walit.streamline.backend.Dispatcher;
import com.walit.streamline.utilities.RetrievedStorage;

/**
 * Base class for the different front-ends. Lays out important functionality that will be required for all interfaces.
 * @author wellatleastitried
 */
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
