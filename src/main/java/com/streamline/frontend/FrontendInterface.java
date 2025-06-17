package com.streamline.frontend;

import com.streamline.backend.Dispatcher;

/**
 * Base class for the different front-ends. Lays out important functionality that will be required for all interfaces.
 * @author wellatleastitried
 */
public abstract class FrontendInterface {

    public final Dispatcher backend;

    public FrontendInterface(Dispatcher backend) {
        this.backend = backend;
    }

    public abstract boolean run();

    public abstract void shutdown();
}
