package com.walit.streamline.Interface;

import com.walit.streamline.Backend.Core;

public class GraphicalInterface extends FrontendInterface {

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
