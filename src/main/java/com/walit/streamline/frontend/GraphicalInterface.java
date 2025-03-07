package com.walit.streamline.frontend;

import com.walit.streamline.backend.Dispatcher;

public final class GraphicalInterface extends FrontendInterface {

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
