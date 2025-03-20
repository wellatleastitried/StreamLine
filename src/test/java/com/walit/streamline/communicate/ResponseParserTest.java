package com.walit.streamline.communicate;

import com.walit.streamline.audio.Song;
import com.walit.streamline.communicate.jsonobjects.VideoSearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResponseParserTest {

    @Mock
    private VideoSearchResult mockVideoSearchResult;

    @Test
    public void testIsValidJsonValidJson() {
        String validJson = "[{\"type\":\"video\",\"title\":\"Test Video\",\"videoId\":\"12345\"}]";
        boolean result = ResponseParser.isValidJson(validJson);
        assertTrue(result);
    }
    
    @Test
    public void testIsValidJsonInvalidJson() {
        String invalidJson = "This is not JSON";
        boolean result = ResponseParser.isValidJson(invalidJson);
        assertFalse(result);
    }
    
    @Test
    public void testIsValidJsonIncompleteJson() {
        String incompleteJson = "{\"type\":\"video\",\"title\":\"Test Video";
        boolean result = ResponseParser.isValidJson(incompleteJson);
        assertFalse(result);
    }
    
    @Test
    public void testSearchResultToSong() {
        when(mockVideoSearchResult.getTitle()).thenReturn("Test Song");
        when(mockVideoSearchResult.getAuthor()).thenReturn("Test Artist");
        when(mockVideoSearchResult.getVideoId()).thenReturn("abc123");
        Song result = ResponseParser.searchResultToSong(mockVideoSearchResult);
        assertNotNull(result);
        assertEquals("Test Song", result.getSongName());
        assertEquals("Test Artist", result.getSongArtist());
        assertEquals("abc123", result.getSongVideoId());
        assertEquals(-1, result.getSongId());
        assertNull(result.getSongLink());
    }
    
    @Test
    public void testListFromInvidiousSearchResponseValidResponse() {
        String validResponse = "[{\"type\":\"video\",\"title\":\"Test Song\",\"author\":\"Test Artist\",\"videoId\":\"abc123\",\"lengthSeconds\":180}]";
        List<Song> result = ResponseParser.listFromInvidiousSearchResponse(validResponse);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Song", result.get(0).getSongName());
        assertEquals("Test Artist", result.get(0).getSongArtist());
        assertEquals("abc123", result.get(0).getSongVideoId());
    }
    
    @Test
    public void testListFromInvidiousSearchResponseInvalidJson() {
        String invalidJson = "This is not JSON";
        List<Song> result = ResponseParser.listFromInvidiousSearchResponse(invalidJson);
        assertNull(result);
    }
    
    @Test
    public void testListFromInvidiousSearchResponseEmptyArray() {
        String emptyArray = "[]";
        List<Song> result = ResponseParser.listFromInvidiousSearchResponse(emptyArray);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testListFromInvidiousSearchResponseMixedTypes() {
        String mixedResponse = "[{\"type\":\"video\",\"title\":\"Test Song\",\"author\":\"Test Artist\",\"videoId\":\"abc123\",\"lengthSeconds\":180},{\"type\":\"playlist\",\"title\":\"Test Playlist\"}]";
        List<Song> result = ResponseParser.listFromInvidiousSearchResponse(mixedResponse);
        assertNotNull(result);
        assertEquals(1, result.size()); // Only video type should be included
        assertEquals("Test Song", result.get(0).getSongName());
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseInvalidJson() {
        String invalidJson = "This is not JSON";
        String result = ResponseParser.urlFromInvidividualVideoResponse(invalidJson);
        assertNull(result);
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseValidResponseWithHighQualityMp4() {
        String validResponse = "{\"adaptiveFormats\":[" +
                "{\"url\":\"https://example.com/video.mp4\",\"type\":\"video/mp4;codecs=avc1.4d401e\",\"audioQuality\":\"MEDIUM\"}," +
                "{\"url\":\"https://example.com/audio-high.mp4\",\"type\":\"audio/mp4;codecs=mp4a.40.2\",\"audioQuality\":\"HIGH\"}," +
                "{\"url\":\"https://example.com/audio-low.webm\",\"type\":\"audio/webm;codecs=opus\",\"audioQuality\":\"LOW\"}" +
                "]}";
        String result = ResponseParser.urlFromInvidividualVideoResponse(validResponse);
        assertEquals("https://example.com/audio-high.mp4", result);
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseValidResponsePrefersMp4() {
        String validResponse = "{\"adaptiveFormats\":[" +
                "{\"url\":\"https://example.com/video.mp4\",\"type\":\"video/mp4;codecs=avc1.4d401e\",\"audioQuality\":\"MEDIUM\"}," +
                "{\"url\":\"https://example.com/audio-medium.mp4\",\"type\":\"audio/mp4;codecs=mp4a.40.2\",\"audioQuality\":\"MEDIUM\"}," +
                "{\"url\":\"https://example.com/audio-high.webm\",\"type\":\"audio/webm;codecs=opus\",\"audioQuality\":\"HIGH\"}" +
                "]}";
        String result = ResponseParser.urlFromInvidividualVideoResponse(validResponse);
        assertEquals("https://example.com/audio-medium.mp4", result);
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseNoAudioFormats() {
        String validResponse = "{\"adaptiveFormats\":[" +
                "{\"url\":\"https://example.com/video1.mp4\",\"type\":\"video/mp4;codecs=avc1.4d401e\",\"audioQuality\":\"MEDIUM\"}," +
                "{\"url\":\"https://example.com/video2.mp4\",\"type\":\"video/mp4;codecs=avc1.4d401e\",\"audioQuality\":\"HIGH\"}" +
                "]}";
        String result = ResponseParser.urlFromInvidividualVideoResponse(validResponse);
        assertNull(result);
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseEmptyAdaptiveFormats() {
        String validResponse = "{\"adaptiveFormats\":[]}";
        String result = ResponseParser.urlFromInvidividualVideoResponse(validResponse);
        assertNull(result);
    }
    
    @Test
    public void testUrlFromInvidividualVideoResponseFallbackToLowQuality() {
        String validResponse = "{\"adaptiveFormats\":[" +
                "{\"url\":\"https://example.com/video.mp4\",\"type\":\"video/mp4;codecs=avc1.4d401e\",\"audioQuality\":\"MEDIUM\"}," +
                "{\"url\":\"https://example.com/audio-low.mp4\",\"type\":\"audio/mp4;codecs=mp4a.40.2\",\"audioQuality\":\"LOW\"}" +
                "]}";
        String result = ResponseParser.urlFromInvidividualVideoResponse(validResponse);
        assertEquals("https://example.com/audio-low.mp4", result);
    }
}
