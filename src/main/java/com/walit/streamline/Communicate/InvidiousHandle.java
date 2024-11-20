package com.walit.streamline.Communicate;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.List;

public class InvidiousHandle {

    public static InvidiousHandle instance;

    public final String key;
    private final String invidiousHost = "https://inv.nadeko.net/";
    private final ResponseParser parser; // Might just be public tools I can use instead of writing my own

    public InvidiousHandle(String key) {
        this.key = key;
        this.parser = new ResponseParser();
    }

    /**
     * Singleton structure for this class as more than one instance is unnecessary and wasteful.
     */
    public static InvidiousHandle getInstance() {
        if (instance == null) {
            instance = new InvidiousHandle(getKeyFromStore());
        }
        return instance;
    }

    /**
     * Get API key from resources folder. This key should be initialized during install.
     */
    private static String getKeyFromStore() {
        return "";
    }

    private String getHostname() {
        return invidiousHost;
    }

    public String urlEncodeString(String base) {
        return URLEncoder.encode(base, StandardCharsets.UTF_8);
    }

    public List<SearchResult> retrieveSearchResults(String searchTerm) {
        StringBuilder result = new StringBuilder();
        BufferedReader reader;
        HttpURLConnection connection;
        searchTerm = urlEncodeString(searchTerm.trim());
        try {
            connection = (HttpURLConnection) new URL(invidiousHost + "api/v1/search?q=" + searchTerm).openConnection();
            if (connection.getResponseCode() >= 400) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            List<SearchResult> searchResults = ResponseParser.listFromSearchResponse(result.toString());
            if (searchResults != null) {
                return searchResults;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println(StreamLineMessages.UnableToCallAPIError.getMessage());
            return null;
        }
    }

    /**
     * Being used as a test function to ensure the proper handling and execution of API calls.
     */
    public String retrieveStats() {
        StringBuilder result = new StringBuilder();
        BufferedReader reader;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(invidiousHost + "api/v1/stats").openConnection();

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
