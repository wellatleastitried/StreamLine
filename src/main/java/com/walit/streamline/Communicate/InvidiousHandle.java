package com.walit.streamline.Communicate;

import com.walit.streamline.Utilities.Internal.Config;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;
import com.walit.streamline.Audio.Song;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InvidiousHandle {

    public static InvidiousHandle instance;

    private final String[] possibleHosts = new String[] { "https://inv.nadeko.net/", "https://yewtu.be/"};
    private final Config config;
    // private final String host = "https://inv.nadeko.net/"; // This could change, also allow for self-hosting
    // private final String invidiousHost = "https://yewtu.be/";

    public InvidiousHandle(Config config) {
        this.config = config;
    }

    public static String canConnectToAPI() {
        Map<String, Integer> workingHosts = new HashMap<>();
        // Check through hosts in possibleHosts
        if (workingHosts.isEmpty()) {
            return null;
        }
        String hostname = null;
        Integer fastestTime = null;
        for (Map.Entry<String, Integer> entry : workingHosts.entrySet()) {
            if (fastestTime == null || entry.getValue() < fastestTime) {
                fastestTime = entry.getValue();
                hostname = entry.getKey();
            }
        }
        return hostname != null ? hostname : null; 
    }

    /**
     * Singleton structure for this class as more than one instance is unnecessary and wasteful.
     */
    public static InvidiousHandle getInstance(Config config) {
        if (instance == null) {
            instance = new InvidiousHandle(config);
        }
        return instance;
    }

    // TODO: Delete this if not used
    private String getHostname() {
        return config.getHost();
    }

    public String urlEncodeString(String base) {
        return URLEncoder.encode(base, StandardCharsets.UTF_8);
    }

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
                System.err.println(StreamLineMessages.UnableToCallAPIError.getMessage());
                return null;
            }
        });
    }

    /**
     * Being used as a test function to ensure the proper handling and execution of API calls.
     */
    public String retrieveStats() {
        StringBuilder result = new StringBuilder();
        BufferedReader reader;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(config.getHost() + "api/v1/stats").openConnection();

            if (connection.getResponseCode() >= 400) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString(); 

        } catch (Exception e) {
            System.err.println(StreamLineMessages.UnableToCallAPIError.getMessage());
            return null;
        }
    }
}
