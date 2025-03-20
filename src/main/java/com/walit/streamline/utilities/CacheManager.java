package com.walit.streamline.utilities;

import com.walit.streamline.audio.Song;
import com.walit.streamline.utilities.internal.OS;
import com.walit.streamline.utilities.internal.StreamLineConstants;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import java.io.File;

/**
 * Handles clearing both expired and/or unwanted cached data.
 * @author wellatleastitried
 */
public final class CacheManager {

    /**
     * Check the cache directory for any files that were downloaded and delete them to clear up disk space.
     * @param dirName The name of the directory where the cache is located.
     */
    public static void clearCache(String dirName) {
        if (dirName == null || dirName.isEmpty()) {
            System.err.println(StreamLineMessages.CacheDirectoryCleanupFailure.getMessage());
            return;
        }
        File cacheDirectory = new File(dirName);
        File[] cachedSongs = cacheDirectory.listFiles();
        if (cachedSongs == null || cachedSongs.length == 0) {
            return;
        }
        for (File song : cachedSongs) {
            if (song.isFile()) {
                song.delete();
            }
        }
    }

    /**
     * Check the cache directory for any files that haven't been played in 30 days or more and delete them to clear up disk space.
     * @param dirName The name of the directory where the cache is located.
     * @param expiredSongs The collection of {@link Song} objects that have not been played in at least 30 days.
     */
    public static void clearExpiredCacheOnStartup(String dirName, RetrievedStorage expiredSongs) {
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
    }

    public static void clearExpiredCacheOnStartup(OS os, RetrievedStorage expiredSongs) {
        clearExpiredCacheOnStartup(getCacheDirectory(os), expiredSongs);
    }

    public static String getCacheDirectory(OS os) {
        switch (os) {
            case WINDOWS:
                return StreamLineConstants.WINDOWS_CACHE_ADDRESS;
            case MAC:
                return StreamLineConstants.MAC_CACHE_ADDRESS;
            case LINUX:
            default:
                return StreamLineConstants.LINUX_CACHE_ADDRESS;
        }
    }
}
