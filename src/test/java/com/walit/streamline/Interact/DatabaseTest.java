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
import com.walit.streamline.Audio.Song;
import com.walit.streamline.Backend.Core;
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
        mockLogger = mock(Logger.class);
        try {
            linker = new DatabaseLinker(OS.TESTING, StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"), mockLogger);
        } catch (Exception e) {
            System.err.println("[!] Could not initialize test db.");
            throw new RuntimeException("[!] Could not initialize test db.");
        }
        testPath1 = ".config/notTheDatabase.db";
        testPath2 = linker.PATH;
        queries = Core.getMapOfQueries(mockLogger);
        runner = new DatabaseRunner(linker.getConnection(), queries, mockLogger);
        System.out.println("Setup complete.");
    }

    @Test
    public void databaseExistenceCheck() {
        MatcherAssert.assertThat(linker.isDatabaseSetupAtPath(testPath1), is(false));
        MatcherAssert.assertThat(linker.isDatabaseSetupAtPath(testPath2), is(true));
        System.out.println("Database existence check complete.");
    }

    @Test
    public void simulateLikingSong() {
        Song song = new Song(1, "SongName", "SongArtist", "URL", "VideoId");
        try {
            runner.likeSong(song);
        } catch (Exception e) {
            System.err.println("[!] Failed INSERT statement on Songs in TEST!");
            throw new RuntimeException("[!] Failed INSERT statement on Songs in TEST!");
        }
        RetrievedStorage result = runner.getLikedSongs();
        System.out.println("Number of results from database: " + result.size());
        MatcherAssert.assertThat(result.size(), is(1));
        if (result.size() != 1) {
            System.err.println("[!] Incorrect number of results from table in SELECT!");
            throw new RuntimeException("[!] Incorrect number of results from table in SELECT!");
        }
        System.out.println("Simulating liking a song complete.");
    }

    @After
    public void shutdown() {
        if (!linker.shutdown()) {
            throw new RuntimeException("[!] Could not properly close database linker.");
        }
        new File(testPath1).delete();
        new File(testPath2).delete();
    }
}
