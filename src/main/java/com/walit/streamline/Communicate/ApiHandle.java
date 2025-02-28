package com.walit.streamline.Communicate;

import com.walit.streamline.Audio.Song;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ApiHandle {

    public default String urlEncodeString(String base) {
        return URLEncoder.encode(base, StandardCharsets.UTF_8);
    }

    public CompletableFuture<List<Song>> retrieveSearchResults(String term);
}
