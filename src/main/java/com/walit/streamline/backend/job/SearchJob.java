package com.walit.streamline.backend.job;

import com.walit.streamline.utilities.RetrievedStorage;
import com.walit.streamline.utilities.internal.Config;

public class SearchJob extends StreamLineJob {

    private final String searchTerm;

    public SearchJob(Config config, String searchTerm) {
        super(config);
        this.searchTerm = searchTerm;
    }

    public void execute() {}

    public RetrievedStorage getResults() {
        return null;
    }

}
