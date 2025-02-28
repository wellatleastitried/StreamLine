package com.walit.streamline.Communicate;

import com.walit.streamline.Audio.Song;
import com.walit.streamline.Utilities.Internal.Config;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.logging.Level;

public final class YoutubeHandle implements ApiHandle {

    public static YoutubeHandle instance;

    private final Config config;
    private final Logger logger;

    public YoutubeHandle(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public static YoutubeHandle getInstance(Config config, Logger logger) {
        if (instance == null) {
            instance = new YoutubeHandle(config, logger);
        }
        return instance;
    }

    @Override
    public CompletableFuture<List<Song>> retrieveSearchResults(String term) {
        final String searchTerm = urlEncodeString(term.trim());
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder result = new StringBuilder();
            BufferedReader reader;
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) new URL(config.getHost() + "api/v1/search?q=" + searchTerm).openConnection();
                if (connection.getResponseCode() >= 400) {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                List<Song> searchResults = ResponseParser.listFromSearchResponse(result.toString());
                if (searchResults != null) {
                    return searchResults;
                } else {
                    return null;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, StreamLineMessages.UnableToCallAPIError.getMessage());
                return null;
            }
        });
    }
}
