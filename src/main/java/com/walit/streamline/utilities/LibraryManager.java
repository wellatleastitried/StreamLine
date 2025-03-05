package com.walit.streamline.utilities;

import com.walit.streamline.backend.Core;
import com.walit.streamline.utilities.internal.Config;

public class LibraryManager {

    private final Core backend;

    public LibraryManager(Config config) {
        this.backend = new Core(config);
    }

    public boolean importExistingLibrary(String pathOfLibrary) {
        return true;
    }

    public String exportExistingLibrary() {
        return "";
    }
}
