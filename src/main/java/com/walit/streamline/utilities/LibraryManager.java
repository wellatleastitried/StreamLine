package com.walit.streamline.utilities;

import com.walit.streamline.backend.Dispatcher;
import com.walit.streamline.utilities.internal.Config;

public class LibraryManager {

    private final Dispatcher backend;

    public LibraryManager(Config config) {
        this.backend = new Dispatcher(config);
    }

    public boolean importExistingLibrary(String pathOfLibrary) {
        return true;
    }

    public String exportExistingLibrary() {
        return "";
    }
}
