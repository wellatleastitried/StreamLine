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
        return CompletableFuture.supplyAsync(() -> {
            List<Song> results = new ArrayList<>();
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp", 
                        "ytsearch10:" + term, 
                        "--print", "%(title)s | %(uploader)s | %(duration>%M:%S)s | %(id)s"
                        );
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(" \\| ");
                    if (fields.length == 4) {
                        String title = fields[0];
                        String author = fields[1];
                        String duration = fields[2];
                        String videoId = fields[3];
                        results.add(new Song(-1, title, author, null, duration, videoId));
                    }
                }
                process.waitFor(); // Ensure process completes before returning results
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return results;
        });
    }

    @Override
    public String getAudioUrlFromVideoId(String id) {
        return "";
    }
}
