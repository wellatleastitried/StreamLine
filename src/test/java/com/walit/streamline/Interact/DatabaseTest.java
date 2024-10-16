package com.walit.streamline.Interact;

import java.io.File;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import com.walit.streamline.Communicate.OS;
import com.walit.streamline.Utilities.StatementReader;

import static org.hamcrest.CoreMatchers.is;

public class DatabaseTest {

    private DatabaseLinker linker;
    private String testPath1;
    private String testPath2;

    @Before
    public void setup() {
        linker = new DatabaseLinker(OS.TESTING, StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
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
        MatcherAssert.assertThat(linker.shutdown(), is(true));
    }

    @After
    public void shutdown() {
        linker.shutdown();
        new File(testPath1).delete();
        new File(testPath2).delete();
    }
}
