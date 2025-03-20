package com.streamline.utilities;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import com.streamline.audio.Song;

import static org.hamcrest.CoreMatchers.is;

public class DataStructureTest {

    private final RetrievedStorage structure = new RetrievedStorage();

    @Before
    public void addElements() {
        for (int i = 0; i < 20; i++) {
            structure.add(i, new Song(i + 1, "title", "artist", "url", String.valueOf(i)));
        }
        MatcherAssert.assertThat(structure.size(), is(20));
    }

    @Test
    public void removeElements() {
        structure.remove(5);
        MatcherAssert.assertThat(structure.size(), is(19));
    }

    @After
    public void close() {
        structure.clear();
        MatcherAssert.assertThat(structure.size(), is(0));
    }
}
