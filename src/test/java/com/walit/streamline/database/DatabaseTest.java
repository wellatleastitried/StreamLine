package com.walit.streamline.database;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.hamcrest.MatcherAssert;
import java.util.logging.*;
import java.util.HashMap;

import com.walit.streamline.utilities.internal.OS;
import com.walit.streamline.utilities.StatementReader;
import com.walit.streamline.audio.Song;
import com.walit.streamline.backend.Core;
import com.walit.streamline.utilities.RetrievedStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

public class DatabaseTest {

    private static DatabaseLinker linker;
    private static DatabaseRunner runner;
    private static String testPath1;
    private static String testPath2;
    private static Logger mockLogger;
    private static HashMap<String, String> queries;

    @BeforeClass
    public static void setup() {
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

    @AfterClass
    public static void shutdown() {
        if (!linker.shutdown()) {
            throw new RuntimeException("[!] Could not properly close database linker.");
        }
        new File(testPath1).delete();
        new File(testPath2).delete();
    }
}
