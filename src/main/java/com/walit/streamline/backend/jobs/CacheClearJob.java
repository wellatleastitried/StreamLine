package com.walit.streamline.backend.jobs;

import com.walit.streamline.database.DatabaseRunner;
import com.walit.streamline.utilities.CacheManager;
import com.walit.streamline.utilities.internal.Config;

public class CacheClearJob extends StreamLineJob {

    final DatabaseRunner dbRunner;
    final String cacheDirectory;

    public CacheClearJob(Config config, DatabaseRunner dbRunner, String cacheDirectory) {
        super(config);
        this.dbRunner = dbRunner;
        this.cacheDirectory = cacheDirectory;
    }

    public void execute() {
        CacheManager.clearExpiredCacheOnStartup(cacheDirectory, dbRunner.getExpiredCache());
        dbRunner.clearExpiredCache();
        finish();
    }
}
