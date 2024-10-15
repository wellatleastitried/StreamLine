package com.walit.streamline.Utilities;

import com.walit.streamline.Communicate.StreamLineMessages;

public final class CacheManager {

    public static void clearExpiredCacheOnStartup(String dirName) {
        System.out.println("Clearing cache...");
        if (cacheDirectory == null || cacheDirectory.isEmpty()) {
            System.err.println(StreamLineMessages.CacheDirectoryCleanupFailure.getMessage());
            return;
        }
        File cacheDirectory = new File(dirName);
        File[] cachedSongs = cacheDirectory.listFiles();
        if (cachedSongs.length == 0) {
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
