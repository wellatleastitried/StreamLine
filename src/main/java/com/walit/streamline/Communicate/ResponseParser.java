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
            String url = null;
            String audioType = null;
            int bestQuality = 0;
            for (VideoResult.AdaptiveFormat format : result.getAdaptiveFormats()) {
                if (format.getType().contains("video")) {
                    continue;
                }
                if (format.getAudioQuality().contains("LOW") && bestQuality < 1
                        || (audioType != null && !audioType.contains("audio/mp4"))) {

                    url = format.getUrl();
                    audioType = format.getType().split(";")[0].split("/")[1];
                    bestQuality = 1;

                } else if (format.getAudioQuality().contains("MEDIUM") && bestQuality < 2
                        || (audioType != null && !audioType.contains("audio/mp4"))) {

                    url = format.getUrl();
                    audioType = format.getType().split(";")[0].split("/")[1];
                    bestQuality = 2;

                } else if (format.getAudioQuality().contains("HIGH") && bestQuality < 3
                        || (audioType != null && !audioType.contains("audio/mp4"))) {

                    url = format.getUrl();
                    audioType = format.getType().split(";")[0].split("/")[1];
                    bestQuality = 3;

                } else if (url == null && audioType == null && bestQuality == 0) {
                    url = format.getUrl();
                    audioType = format.getType().split(";")[0].split("/")[1];
                    String quality = format.getAudioQuality();
                    bestQuality = quality.contains("LOW") ? 1 : quality.contains("MEDIUM") ? 2 : quality.contains("HIGH") ? 3 : 0;
                } else {
                    return null;
                }
            }
            if (url != null && audioType != null && bestQuality > 0) {
                return url;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
