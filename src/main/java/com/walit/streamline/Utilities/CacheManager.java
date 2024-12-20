package com.walit.streamline.Utilities;

import com.walit.streamline.Audio.Song;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import java.io.File;

public final class CacheManager {

    /**
     * Check the cache directory for any files that were downloaded and delete them to clear up disk space.
     */
    public static void clearCache(String dirName) {
        System.err.println("Clearing cache...");
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
        System.err.println("Cache has been cleared.");
    }

    /**
     * Check the cache directory for any files that haven't been played in 30 days or more and delete them to clear up disk space.
     */
    public static void clearExpiredCacheOnStartup(String dirName, RetrievedStorage expiredSongs) {
        System.err.println("Clearing expired cache...");
        if (dirName == null || dirName.isEmpty()) {
            System.err.println(StreamLineMessages.CacheDirectoryCleanupFailure.getMessage());
            return;
        }
        for (Song song: expiredSongs.getArrayOfSongs()) {
            File songFile = new File(song.getDownloadPath()); 
            if (songFile.isFile()) {
                songFile.delete();
            }
        }
        System.err.println("Expired cache has been cleared.");
    }
}
