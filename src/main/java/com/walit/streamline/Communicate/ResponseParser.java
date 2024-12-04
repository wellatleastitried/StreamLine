package com.walit.streamline.Communicate;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseParser {
    
    public static List<SearchResult> listFromSearchResponse(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<SearchResult> allSearchResults = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<SearchResult>>() {}
                    );
            return allSearchResults.stream()
                .filter(result -> result instanceof VideoSearchResult)
                // .peek(result -> System.out.println("Video ID: " + ((VideoSearchResult) result).getVideoId() + " Video Length in Seconds: " + ((VideoSearchResult) result).getLengthSeconds()))
                .map(result -> (VideoSearchResult) result)
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(StreamLineMessages.JsonParsingException.getMessage());
            return null;
        }
    }

    // TODO: Have this return a List<String>??? with the url, title, artist, videoId, etc
    public static void getVideoProperties(String jsonResponse) {
    }
}
