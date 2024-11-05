package com.walit.streamline.Communicate;
    
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.*;

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
        System.out.println(String.format("\nInvidious Stats: %s\n", handle.retrieveStats().replace("},", "},\n")));
    }
}
