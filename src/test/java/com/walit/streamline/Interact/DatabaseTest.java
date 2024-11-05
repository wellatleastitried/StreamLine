package com.walit.streamline.Interact;

import java.io.File;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import java.util.logging.*;
import java.util.HashMap;

import com.walit.streamline.Utilities.Internal.OS;
import com.walit.streamline.Utilities.StatementReader;
import com.walit.streamline.AudioHandle.Song;
import com.walit.streamline.Core;
import com.walit.streamline.Utilities.RetrievedStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class DatabaseTest {

    private DatabaseLinker linker;
    private DatabaseRunner runner;
    private String testPath1;
    private String testPath2;
    private Logger mockLogger;
    private HashMap<String, String> queries;

    @Before
    public void setup() {
        try {
            linker = new DatabaseLinker(OS.TESTING, StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
        } catch (Exception e) {
            throw new RuntimeException("[!] Could not initialize test db.");
        }
        testPath1 = ".config/notTheDatabase.db";
        testPath2 = linker.PATH;
        mockLogger = mock(Logger.class);
        queries = Core.getMapOfQueries();
        runner = new DatabaseRunner(linker.getConnection(), queries, mockLogger);
    }

    @Test
    public void databaseExistenceCheck() {
        MatcherAssert.assertThat(linker.needsNewDatabase(testPath1), is(true));
        MatcherAssert.assertThat(linker.needsNewDatabase(testPath2), is(false));
    }

    @Test
    public void runInsert() {
        Song song = new Song(1, "SongName", "SongArtist", "URL");
        try {
            runner.likeSong(song);
        } catch (Exception e) {
            throw new RuntimeException("Failed INSERT statement on Songs in TEST!");
        }
        RetrievedStorage result = runner.getLikedSongs();
        System.out.println("Number of results from database: " + result.size());
        if (result.size() != 1) {
            throw new RuntimeException("Incorrect number of results from table in SELECT!");
        }
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
