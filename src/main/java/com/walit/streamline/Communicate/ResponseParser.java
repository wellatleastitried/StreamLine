package com.walit.streamline.Communicate;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ResponseParser {
    
    public static List<SearchResult> listFromSearchResponse(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<SearchResult> searchResults = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<SearchResult>>() {}
                    );
            for (SearchResult result : searchResults) {
                if (result instanceof VideoResult) {
                    VideoResult video = (VideoResult) result;
                    System.out.println("Video ID: " + video.videoId);
                }
            }

            return searchResults;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(StreamLineMessages.JsonParsingException.getMessage());
            return null;
        }
    }
}
