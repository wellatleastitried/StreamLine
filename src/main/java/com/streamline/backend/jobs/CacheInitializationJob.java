package com.streamline.backend.jobs;

import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.CacheManager;
import com.streamline.utilities.internal.Config;

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
