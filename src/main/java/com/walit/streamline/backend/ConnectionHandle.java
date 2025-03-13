package com.walit.streamline.backend;

import com.walit.streamline.audio.Song;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for different types of connections. (e.g. YouTube or Invidious)
 * @author wellatleastitried
 */
public interface ConnectionHandle {

    /**
     * URL encode the given string.
     * @param base The text to be URL encoded.
     * @return The URL encoded string.
     */
    default String urlEncodeString(String base) {
        return URLEncoder.encode(base, StandardCharsets.UTF_8);
    }

    /**
     * Asynchronous method to allow the connection handlers to retrieve search results for the given query.
     * @param term The term to search for using the specified connection handler.
     * @return The async process that will be used to return the search results once they are ready.
     */
    public CompletableFuture<List<Song>> retrieveSearchResults(String term);

    /**
     * Get the URL for a given video from the specified connection handler.
     * @param id The video ID to use for fetching the audio URL.
     * @return String The URL that directs to the audio stream of the specified video.
     */
    public String getAudioUrlFromVideoId(String id);
}
