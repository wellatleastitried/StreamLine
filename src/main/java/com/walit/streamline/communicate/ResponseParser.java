package com.walit.streamline.communicate;

import com.walit.streamline.audio.Song;
import com.walit.streamline.communicate.jsonobjects.*;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Parse API responses from Invidious and validate JSON strings.
 * @author wellatleastitried
 */
public class ResponseParser {

    /**
     * Parse the incoming JSON response into a List of {@link Song}.
     * @param jsonResponse The JSON response from the API in String format.
     * @return The List of {@link Song} for the backend to manage.
     */
    public static List<Song> listFromInvidiousSearchResponse(String jsonResponse) {
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
                // .peek(result -> System.out.println("Video ID: " + ((VideoSearchResult) result).getVideoId() + " Video Length in Seconds: " + ((VideoSearchResult) result).getLengthSeconds()))
                .map(result -> (VideoSearchResult) result)
                .collect(Collectors.toList());
            return videos.stream()
                .map(x -> searchResultToSong(x))
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println(StreamLineMessages.JsonParsingException.getMessage());
        }
        return null;
    }

    /**
     * Validate whether the incoming string is valid JSON.
     * @param jsonString The string needing validation.
     * @return True if the string is valid JSON, False otherwise.
     */
    public static boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(jsonString);
            parser.nextToken();
            while (parser.nextToken() != null) {
                // Keep parsing until the end of the input
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse the JSON object {@link VideoSearchResult} into a {@link Song} object.
     * @param toConvert The {@link VideoSearchResult} object to convert.
     * @return The {@link Song} object with the attributes of the {@link VideoSearchResult}.
     */
    public static Song searchResultToSong(VideoSearchResult toConvert) {
        return new Song(
                -1,
                toConvert.getTitle(),
                toConvert.getAuthor(),
                null, // this is for the video url that is retrieved from a different API call 
                toConvert.getVideoId()
                );
    }

    /**
     * Retrieves the best audio URL from the passed JSON response. This method prioritizes mp4 audio files.
     * @param jsonResponse The JSON response to parse for the URLs.
     * @return The audio URL for the given song with the best quality.
     */
    public static String urlFromInvidividualVideoResponse(String jsonResponse) {
        try {
            if (!isValidJson(jsonResponse)) {
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            VideoResult result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<VideoResult>() {}
                    );

            String mp4BestUrl = null;
            int mp4BestQuality = 0;

            String otherBestUrl = null;
            int otherBestQuality = 0;

            for (VideoResult.AdaptiveFormat format : result.getAdaptiveFormats()) {
                if (format.getType().contains("video")) {
                    continue;
                }

                String mimeType = format.getType().split(";")[0];
                boolean isMp4 = mimeType.contains("audio/mp4");

                int qualityLevel = 0;
                String quality = format.getAudioQuality();
                if (quality.contains("LOW")) {
                    qualityLevel = 1;
                } else if (quality.contains("MEDIUM")) {
                    qualityLevel = 2;
                } else if (quality.contains("HIGH")) {
                    qualityLevel = 3;
                }

                if (isMp4 && qualityLevel > mp4BestQuality) {
                    mp4BestUrl = format.getUrl();
                    mp4BestQuality = qualityLevel;
                } else if (!isMp4 && qualityLevel > otherBestQuality) {
                    otherBestUrl = format.getUrl();
                    otherBestQuality = qualityLevel;
                }
            }

            return mp4BestUrl != null ? mp4BestUrl : otherBestUrl;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
