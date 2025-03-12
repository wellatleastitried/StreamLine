package com.walit.streamline.backend.jobs;

import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;

public class SearchJob extends StreamLineJob {

    private final String searchTerm;

    private RetrievedStorage results = null;

    public SearchJob(Config config, String searchTerm) {
        super(config);
        this.searchTerm = searchTerm;
    }

    public void execute() {
        RetrievedStorage finalResults = new RetrievedStorage();
        config.getHandle().retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            if (searchResults != null) {
                for (int i = 0; i < searchResults.size(); i++) {
                    finalResults.add(i, searchResults.get(i));
                }
            } else {
                System.out.println("Unable to retrieve search results at this time.");
            }
        }).join();
        results = finalResults.size() > 0 ? finalResults : null;
        isRunning = false;
    }

    public RetrievedStorage getResults() {
        finish();
        return results;
    }

}
