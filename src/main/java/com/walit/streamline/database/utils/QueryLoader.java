package com.walit.streamline.database.utils;

import com.walit.streamline.utilities.internal.StreamLineMessages;

import java.io.IOException;

import java.util.HashMap;

import org.tinylog.Logger;

public final class QueryLoader {

    private QueryLoader() {
        throw new AssertionError("Utility class should not be instantiated!");
    }

    /**
     * Reaches out to the SQL files in the resources folder that house the queries needed at runtime.
     * @return Map containing the full queries with a key for easy access
     */
    public static HashMap<String, String> getMapOfQueries() {
        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("INITIALIZE_TABLES", StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
            map.put("CLEAR_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearCachedSongs.sql"));
            map.put("CLEAR_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearExpiredCache.sql"));
            map.put("GET_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/queries/GetExpiredCache.sql"));
            map.put("GET_LIKED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForLikedMusicScreen.sql"));
            map.put("GET_DOWNLOADED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForDownloadedScreen.sql"));
            map.put("GET_RECENTLY_PLAYED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForRecPlayedScreen.sql"));
            map.put("ENSURE_RECENTLY_PLAYED_SONG_COUNT", StatementReader.readQueryFromFile("/sql/updates/UpdateRecentlyPlayed.sql"));
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.SQLFileReadError.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.MissingConfigurationFiles.getMessage());
            System.exit(1);
        }
        if (map.isEmpty()) {
            Logger.error(StreamLineMessages.DatabaseQueryCollectionError.getMessage());
            System.exit(1);
        }
        return map;
    }

}
