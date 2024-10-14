package com.walit.streamline.Utilities;

import com.walit.streamline.Communicate.StreamLineMessages;

public final class CacheManager {

    private static String cacheDirectory;

    public static void clearExpiredCacheOnStartup(String dir) {
        if (cacheDirectory == null || cacheDirectory.isEmpty()) {
            System.err.println(StreamLineMessages.CacheDirectoryCleanupFailure.getMessage());
            return;
        }
        System.out.println("Clearing cache");
    }
}
