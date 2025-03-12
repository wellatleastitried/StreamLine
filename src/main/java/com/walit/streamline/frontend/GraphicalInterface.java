package com.walit.streamline.frontend;

import com.walit.streamline.backend.JobDispatcher;

public final class GraphicalInterface extends FrontendInterface {

    public GraphicalInterface(JobDispatcher backend) {
        super(backend);
    }

    public boolean run() {
        return true;
    }

    public void shutdown() {
        return;
    }
}
