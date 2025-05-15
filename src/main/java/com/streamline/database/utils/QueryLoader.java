package com.streamline.database.utils;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

/**
 * Generates the {@link HashMap} of queries to be used throughout the runtime in the backend.
 * @author wellatleastitried
 */
public final class QueryLoader {

    private QueryLoader() {
        throw new AssertionError("Utility class should not be instantiated!");
    }

    /**
     * Reaches out to the SQL files in the resources folder that house the queries needed at runtime.
     * @return Map containing the full queries with a key for easy access
     */
    public static Map<String, String> getMapOfQueries() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("INITIALIZE_TABLES", StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
            map.put("CLEAR_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearCachedSongs.sql"));
            map.put("CLEAR_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearExpiredCache.sql"));
            map.put("GET_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/queries/GetExpiredCache.sql"));
            map.put("GET_LIKED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForLikedMusicScreen.sql"));
            map.put("GET_DOWNLOADED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForDownloadedScreen.sql"));
            map.put("GET_RECENTLY_PLAYED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForRecPlayedScreen.sql"));
            map.put("ENSURE_RECENTLY_PLAYED_SONG_COUNT", StatementReader.readQueryFromFile("/sql/updates/UpdateRecentlyPlayed.sql"));
            map = Collections.unmodifiableMap(map);
        } catch (IOException iE) {
            System.err.println("[!] A fatal error has occured while reading premade SQL queries from configuration files.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[!] Missing configuration files! You may need to rebuild the project.");
            System.exit(1);
        }
        if (map.isEmpty()) {
            Logger.error("[!] Encountered an error while retrieving queries from internal files. This is either due to the files being modified outside of the programs runtime, or a corrupted install.");
            System.exit(1);
        }
        return map;
    }

}
