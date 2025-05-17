package com.streamline.backend.jobs;

import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class SearchJob extends AbstractStreamLineJob {

    private final String searchTerm;

    private RetrievedStorage results = null;

    private boolean resultsAreReady = false;

    public SearchJob(Config config, String searchTerm) {
        super(config);
        this.searchTerm = searchTerm;
    }

    public void execute() {
        final RetrievedStorage finalResults = new RetrievedStorage();
        try {
            config.getHandle().retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
                if (searchResults != null) {
                    for (int i = 0; i < searchResults.size(); i++) {
                        finalResults.add(i, searchResults.get(i));
                    }
                } else {
                    System.out.println("Unable to retrieve search results at this time.");
                }
            }).join();
            Logger.debug("Adding results to public var.");
            results = finalResults.size() > 0 ? finalResults : null;
        } finally {
            resultsAreReady = true;
        }
    }

    public synchronized RetrievedStorage getResults() {
        finish();
        return results;
    }

    public boolean resultsAreReady() {
        return resultsAreReady;
    }
}
