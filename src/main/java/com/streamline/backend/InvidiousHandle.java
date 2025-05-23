package com.streamline.backend;

import com.streamline.audio.Song;
import com.streamline.communicate.ResponseParser;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.StreamLineConstants;

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

import org.tinylog.Logger;

public final class InvidiousHandle implements ConnectionHandle {

    public static InvidiousHandle instance;

    public final Config config;

    public InvidiousHandle(Config config) {
        this.config = config;
    }

    private static List<String> getPossibleHosts() {
        try (InputStream inputStream = InvidiousHandle.class.getResourceAsStream(StreamLineConstants.HOST_RESOURCE_PATH);
                BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<String> hostnames = new ArrayList<>();
            String line;
            while ((line = bR.readLine()) != null) {
                hostnames.add(line.trim());
            }
            return hostnames;
        } catch (IOException iE) {
            Logger.warn("[!] Could not read hostnames from internal resource file, the installation may be corrupted!");
        }
        return null;
    }

    public static InvidiousHandle getInstance(Config config) {
        if (instance == null) {
            instance = new InvidiousHandle(config);
        }
        return instance;
    }


    public static String getWorkingHostnameFromApiOrDocker() {
        Map<String, Integer> workingHosts = new HashMap<>();
        List<String> possibleHosts = getPossibleHosts();
        if (possibleHosts == null) {
            return null;
        }
        for (String host : possibleHosts) {
            try {
                URL url = new URL(host + "api/v1/trending");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                long startTime = System.currentTimeMillis();
                connection.connect();
                long responseTime = System.currentTimeMillis();
                
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    continue;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (response.toString().toLowerCase().contains("api is disabled")) {
                    continue;
                }

                workingHosts.put(host, (int) (responseTime - startTime));
            } catch (Exception e) {}
        }
        if (workingHosts.isEmpty()) {
            if (DockerManager.invidiousDirectoryExists()) {
                String host = DockerManager.startInvidiousContainer();
                return host;
            }
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
                List<Song> searchResults = ResponseParser.listFromInvidiousSearchResponse(result.toString());
                if (searchResults != null) {
                    return searchResults;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Logger.warn("[!] Unable to connect to API at this time, please try again later.");
                return null;
            }
        });
    }

    @Override
    public String getAudioUrlFromVideoId(String id) {
        StringBuilder result = new StringBuilder();
        BufferedReader reader;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL("http://localhost:3000/api/v1/videos/" + id).openConnection();
            if (connection.getResponseCode() >= 400) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return ResponseParser.urlFromInvidividualVideoResponse(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if (type.contains("audio/mp4")) {
        return "";
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
            Logger.warn("[!] Unable to connect to API at this time, please try again later.");
            return null;
        }
    }
}
