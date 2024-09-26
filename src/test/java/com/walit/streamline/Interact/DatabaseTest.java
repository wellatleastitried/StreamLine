package com.walit.streamline.Interact;

import java.io.File;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import com.walit.streamline.Communicate.OS;

import static org.hamcrest.CoreMatchers.is;

public class DatabaseTest {

    private DatabaseLinker linker;
    private String testPath1;
    private String testPath2;

    @Before
    public void setup() {
        linker = new DatabaseLinker(OS.TESTING);
        testPath1 = ".config/notTheDatabase.db";
        testPath2 = linker.PATH;
    }

    @Test
    public void databaseExistenceCheck() {
        MatcherAssert.assertThat(linker.needsNewDatabase(testPath1), is(true));
        MatcherAssert.assertThat(linker.needsNewDatabase(testPath2), is(false));
    }

    @Test
    public void closeTest() {
        MatcherAssert.assertThat(linker.close(), is(true));
    }

    @After
    public void shutdown() {
        linker.close();
        new File(testPath1).delete();
        new File(testPath2).delete();
        new File("/tmp/StreamLine").delete();
    }
}
