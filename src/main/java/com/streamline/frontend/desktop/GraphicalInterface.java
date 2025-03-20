package com.streamline.frontend.desktop;

import com.streamline.backend.Dispatcher;

public final class GraphicalInterface extends com.streamline.frontend.FrontendInterface {

    public GraphicalInterface(Dispatcher backend) {
        super(backend);
    }

    public boolean run() {
        return true;
    }

    public void shutdown() {
        return;
    }
}
