package com.walit.streamline.Communicate;
    
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import com.walit.streamline.Audio.Song;
import com.walit.streamline.Utilities.Internal.Config;

public class ApiTest {

    InvidiousHandle handle;
    Config config;
    Logger mockLogger;

    @Before
    public void setup() {
        mockLogger = mock(Logger.class);
        config = new Config();
        config.setHost(InvidiousHandle.canConnectToAPI(mockLogger));
        handle = InvidiousHandle.getInstance(config, mockLogger);
    }

    @Test
    public void checkHandleIsSingleton() {
        InvidiousHandle testHandle = InvidiousHandle.getInstance(config, mockLogger);
        MatcherAssert.assertThat(handle, is(testHandle));
    }

    // Basic check to make sure the API is reachable
    @Test
    public void checkStatsFromApi() {
        if (config.getHost() == null) {
            System.out.println("Skipping " + ApiTest.class.getName() + "#checkStatsFromApi(): No connection to API at this time.");
            return;
        }
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
        if (config.getHost() == null) {
            System.out.println("Skipping " + ApiTest.class.getName() + "#checkCanGetVideoId(): No connection to API at this time.");
            return;
        }
        String searchTerm = "Give Cold";
        System.out.println("\n\nRESULTING VIDEO IDs ARE:");
        handle.retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            if (searchResults != null) {
                System.out.println("\n\nNumber of results from searching \"" + searchTerm + "\":" + searchResults.size());
                searchResults.forEach(result -> System.out.println(result.getSongVideoId()));
                MatcherAssert.assertThat(searchResults, not(searchResults.isEmpty()));
            } else {
                System.out.println("Unable to reach the API at this time.");
            }
        }).join();
        System.out.println("\n");
    }

    @Test
    public void attemptVideoDownloadAndAudioStrip() {
        // Access stream from URL.
        // Try to strip audio from video and save as mp3
    }
}
