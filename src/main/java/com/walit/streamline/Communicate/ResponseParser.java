package com.walit.streamline.Communicate;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;
import com.walit.streamline.Audio.Song;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseParser {
    
    public static List<Song> listFromSearchResponse(String jsonResponse) {
        try {
            if (!isValidJson(jsonResponse)) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<SearchResult> allSearchResults = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<SearchResult>>() {}
                    );
             List<VideoSearchResult> videos = allSearchResults.stream()
                .filter(result -> result instanceof VideoSearchResult)
                .peek(result -> System.out.println("Video ID: " + ((VideoSearchResult) result).getVideoId() + " Video Length in Seconds: " + ((VideoSearchResult) result).getLengthSeconds()))
                .map(result -> (VideoSearchResult) result)
                .collect(Collectors.toList());
             return videos.stream()
                 .map(x -> searchResultToSong(x))
                 .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println(StreamLineMessages.JsonParsingException.getMessage());
            return null;
        }
    }

    public static boolean isValidJson(String jsonString) {
        try {
            JsonFactory factory = new JsonFactory();
            factory.createParser(jsonString).nextToken();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Song searchResultToSong(VideoSearchResult toConvert) {
        return new Song(
                -1,
                toConvert.getTitle(),
                toConvert.getAuthor(),
                null, // this is for the video url that is retrieved from a different API call 
                toConvert.getVideoId()
                );
    }
}
