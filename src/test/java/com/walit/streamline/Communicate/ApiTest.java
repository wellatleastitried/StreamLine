package com.walit.streamline.Communicate;
    
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import com.walit.streamline.Audio.Song;

public class ApiTest {

    InvidiousHandle handle;

    @Before
    public void setup() {
        handle = InvidiousHandle.getInstance();
    }

    @Test
    public void checkHandleIsSingleton() {
        InvidiousHandle testHandle = InvidiousHandle.getInstance();
        MatcherAssert.assertThat(handle, is(testHandle));
    }

    // Basic check to make sure the API is reachable
    @Test
    public void checkStatsFromApi() {
        String response = handle.retrieveStats();
        MatcherAssert.assertThat(response, is(notNullValue()));
        MatcherAssert.assertThat(response, not(""));
        System.out.println("API is reachable:");
        System.out.println(String.format("\nInvidious Stats: %s\n", handle.retrieveStats().replace("},", "},\n")));
    }

    @Test
    public void checkURLEncode() {
        MatcherAssert.assertThat("Test+String", is(handle.urlEncodeString("Test String")));
    }

    @Test
    public void checkCanGetVideoId() {
        String searchTerm = "Give Cold";
        System.out.println("\n\nRESULTING VIDEO IDs ARE:");
        handle.retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            MatcherAssert.assertThat(searchResults, is(notNullValue()));
            if (searchResults != null) {
                System.out.println("\n\nNumber of results from searching \"" + searchTerm + "\":" + searchResults.size());
                searchResults.forEach(result -> System.out.println(result.getSongVideoId()));
            }
            MatcherAssert.assertThat(searchResults, not(searchResults.isEmpty()));
        }).join();
        System.out.println("\n");
    }

    @Test
    public void attemptVideoDownloadAndAudioStrip() {
        // Access stream from URL.
        // Try to strip audio from video and save as mp3
    }
}
