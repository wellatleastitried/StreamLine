package com.walit.streamline.Utilities;

import com.walit.streamline.Communicate.StreamLineMessages;

import java.io.File;

public final class CacheManager {

    /**
     * Check the cache directory for any files that were downloaded and delete them to clear up disk space.
     */
    public static void clearExpiredCacheOnStartup(String dirName) {
        System.out.println("Clearing cache...");
        if (dirName == null || dirName.isEmpty()) {
            System.err.println(StreamLineMessages.CacheDirectoryCleanupFailure.getMessage());
            return;
        }
        File cacheDirectory = new File(dirName);
        File[] cachedSongs = cacheDirectory.listFiles();
        if (cachedSongs == null || cachedSongs.length == 0) {
            System.err.println("Cache directory is empty, nothing to clear.");
            return;
        }
        for (File song : cachedSongs) {
            if (song.isFile()) {
                song.delete();
            }
        }
        System.out.println("Cache has been cleared.");
    }
}
