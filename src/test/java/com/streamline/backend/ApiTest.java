package com.streamline.backend;
    
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.*;

import com.streamline.utilities.internal.Config;

public class ApiTest {

    static InvidiousHandle handle;
    static Config config;

    @BeforeClass
    public static void setup() {
        config = new Config();
        config.setHost(InvidiousHandle.getWorkingHostnameFromApiOrDocker());
        handle = InvidiousHandle.getInstance(config);
    }

    @Test
    public void checkHandleIsSingleton() {
        InvidiousHandle testHandle = InvidiousHandle.getInstance(config);
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
        System.out.println("API is reachable!");
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
        System.out.println("\nRESULTING VIDEO IDs ARE:");
        handle.retrieveSearchResults(searchTerm).thenAccept(searchResults -> {
            if (searchResults != null) {
                System.out.println("\nNumber of results from searching \"" + searchTerm + "\":" + searchResults.size());
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

    @AfterClass
    public static void shutdown() {
        try {
            if (DockerManager.containerIsAlive() && DockerManager.canConnectToContainer()) {
                DockerManager.stopContainer();
            }
        } catch (InterruptedException iE) {
            System.err.println("InterruptedException occured while trying to stop docker instance.");
            iE.printStackTrace();
        }
    }
}
