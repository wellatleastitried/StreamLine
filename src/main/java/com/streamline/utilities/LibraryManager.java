package com.streamline.utilities;

import com.streamline.backend.Dispatcher;
import com.streamline.utilities.internal.Config;

/**
 * Handle importing and exporting StreamLine libraries across different systems.
 * @author wellatleastitried
 */
public class LibraryManager {

    private final Dispatcher backend;

    public LibraryManager(Config config) {
        this.backend = new Dispatcher(config);
    }

    /**
     * Import the JSON file containing the user's library and write it to the local database.
     * @param pathOfLibrary The path to the JSON file containing the user's music library.
     * @return True if the import was successful, False otherwise.
     */
    public boolean importExistingLibrary(String pathOfLibrary) {
        return true;
    }

    /** 
     * Export the user's current music library from the database to a JSON file.
     * @return The path of the JSON file if the method executes successfully, null otherwise.
     */
    public String exportExistingLibrary() {
        return "";
    }
}
