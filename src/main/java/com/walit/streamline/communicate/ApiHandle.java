package com.walit.streamline.communicate;

import com.walit.streamline.audio.Song;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ApiHandle {

    default String urlEncodeString(String base) {
        return URLEncoder.encode(base, StandardCharsets.UTF_8);
    }

    public CompletableFuture<List<Song>> retrieveSearchResults(String term);

    public String getAudioUrlFromVideoId(String id);
}
