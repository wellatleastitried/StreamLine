package com.walit.streamline.frontend;

import com.walit.streamline.backend.Core;

public final class GraphicalInterface extends FrontendInterface {

    public GraphicalInterface(Core backend) {
        super(backend);
    }

    public boolean run() {
        return true;
    }

    public void shutdown() {
        return;
    }
}
