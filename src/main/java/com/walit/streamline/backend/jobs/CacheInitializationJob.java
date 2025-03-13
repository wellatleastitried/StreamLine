package com.walit.streamline.backend.jobs;

import com.walit.streamline.database.DatabaseRunner;
import com.walit.streamline.utilities.CacheManager;
import com.walit.streamline.utilities.internal.Config;

public class CacheInitializationJob extends StreamLineJob {

    final DatabaseRunner dbRunner;

    public CacheInitializationJob(Config config, DatabaseRunner dbRunner) {
        super(config);
        this.dbRunner = dbRunner;
    }

    public void execute() {
        CacheManager.clearExpiredCacheOnStartup(config.getOS(), dbRunner.getExpiredCache());
        dbRunner.clearExpiredCache();
        finish();
    }
}
