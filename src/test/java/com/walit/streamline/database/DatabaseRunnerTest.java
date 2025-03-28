package com.walit.streamline.database;

import com.walit.streamline.audio.Song;
import com.walit.streamline.database.utils.QueryLoader;
import com.walit.streamline.utilities.RetrievedStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DatabaseRunnerTest {

    private Connection mockConnection;
    private HashMap<String, String> queryMap;
    private DatabaseLinker mockLinker;
    private DatabaseRunner databaseRunner;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockLinker = mock(DatabaseLinker.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        queryMap = QueryLoader.getMapOfQueries();
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        databaseRunner = new DatabaseRunner(mockConnection, queryMap, mockLinker);
    }

    @Test
    public void testConstructor() throws SQLException {
        verify(mockConnection).setAutoCommit(false);
    }

    @Test
    public void testGetLikedSongs() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("title")).thenReturn("Test Song");
        when(mockResultSet.getString("artist")).thenReturn("Test Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com");
        when(mockResultSet.getString("videoId")).thenReturn("abc123");

        RetrievedStorage result = databaseRunner.getLikedSongs();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeQuery(queryMap.get("GET_LIKED_SONGS"));

        assertEquals(1, result.size());
        Song song = (Song) result.getSongFromIndex(1);
        assertEquals("Test Song", song.getSongName());
        assertEquals("Test Artist", song.getSongArtist());
        assertEquals("http://example.com", song.getSongLink());
        assertEquals("abc123", song.getSongVideoId());
        assertTrue(song.isSongLiked());
    }

    @Test
    public void testGetDownloadedSongs() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(2);
        when(mockResultSet.getString("title")).thenReturn("Downloaded Song");
        when(mockResultSet.getString("artist")).thenReturn("Another Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com/2");
        when(mockResultSet.getString("videoId")).thenReturn("def456");

        RetrievedStorage result = databaseRunner.getDownloadedSongs();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeQuery(queryMap.get("GET_DOWNLOADED_SONGS"));

        assertEquals(1, result.size());
        Song song = (Song) result.getSongFromIndex(1);
        assertEquals("Downloaded Song", song.getSongName());
        assertEquals("Another Artist", song.getSongArtist());
    }

    @Test
    public void testGetRecentlyPlayedSongs() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(3);
        when(mockResultSet.getString("title")).thenReturn("Recent Song");
        when(mockResultSet.getString("artist")).thenReturn("Recent Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com/3");
        when(mockResultSet.getString("videoId")).thenReturn("ghi789");

        RetrievedStorage result = databaseRunner.getRecentlyPlayedSongs();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeQuery(queryMap.get("GET_RECENTLY_PLAYED_SONGS"));

        assertEquals(1, result.size());
        Song song = (Song) result.getSongFromIndex(1);
        assertEquals("Recent Song", song.getSongName());
        assertTrue(song.isSongRecentlyPlayed());
    }

    @Test
    public void testSearchForSongName() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.isBeforeFirst()).thenReturn(true);
        when(mockResultSet.last()).thenReturn(true);
        when(mockResultSet.getRow()).thenReturn(1);
        when(mockResultSet.first()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(4);
        when(mockResultSet.getString("title")).thenReturn("Searchable Song");
        when(mockResultSet.getString("artist")).thenReturn("Search Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com/4");
        when(mockResultSet.getString("videoId")).thenReturn("jkl012");

        Song result = databaseRunner.searchForSongName("Searchable");

        verify(mockConnection).prepareStatement("SELECT * FROM Songs s WHERE s.title LIKE ?;");

        verify(mockPreparedStatement).setString(1, "%Searchable%");

        verify(mockPreparedStatement).setQueryTimeout(10);

        assertNotNull(result);
        assertEquals("Searchable Song", result.getSongName());
        assertEquals("Search Artist", result.getSongArtist());
    }

    @Test
    public void testSearchForSongNameMultipleSongs() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);

        Song result = databaseRunner.searchForSongName("Common");

        assertNull(result);
    }

    @Test
    public void testGetSongsFromPlaylist() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(5);
        when(mockResultSet.getString("title")).thenReturn("Playlist Song");
        when(mockResultSet.getString("artist")).thenReturn("Playlist Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com/5");
        when(mockResultSet.getString("videoId")).thenReturn("mno345");

        RetrievedStorage result = databaseRunner.getSongsFromPlaylist("My Playlist");

        verify(mockPreparedStatement).setString(1, "My Playlist");
        verify(mockPreparedStatement).executeQuery();

        assertEquals(1, result.size());
        Song song = (Song) result.getSongFromIndex(1);
        assertEquals("Playlist Song", song.getSongName());
    }

    @Test
    public void testGetExpiredCache() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(6);
        when(mockResultSet.getString("title")).thenReturn("Expired Song");
        when(mockResultSet.getString("artist")).thenReturn("Expired Artist");
        when(mockResultSet.getString("url")).thenReturn("http://example.com/6");
        when(mockResultSet.getString("videoId")).thenReturn("pqr678");
        when(mockResultSet.getString("file_path")).thenReturn("/path/to/song.mp3");
        when(mockResultSet.getString("file_hash")).thenReturn("abcdef1234567890");

        RetrievedStorage result = databaseRunner.getExpiredCache();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeQuery(queryMap.get("GET_EXPIRED_CACHE"));

        assertEquals(1, result.size());
        Song song = (Song) result.getSongFromIndex(1);
        assertEquals("Expired Song", song.getSongName());
        assertEquals("/path/to/song.mp3", song.getDownloadPath());
        assertEquals("abcdef1234567890", song.getFileHash());
    }

    @Test
    public void testClearExpiredCache() throws SQLException {
        databaseRunner.clearExpiredCache();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeUpdate(queryMap.get("CLEAR_EXPIRED_CACHE"));
        verify(mockConnection).commit();
    }

    @Test
    public void testClearCachedSongs() throws SQLException {
        databaseRunner.clearCachedSongs();

        verify(mockStatement).setQueryTimeout(30);
        verify(mockStatement).executeUpdate(queryMap.get("CLEAR_CACHE"));
        verify(mockConnection).commit();
    }

    @Test
    public void testLikeSongExistingSong() throws SQLException {
        Song song = new Song(0, "Existing Song", "Artist", "http://example.com", "vid123");

        DatabaseRunner spyRunner = spy(databaseRunner);
        doReturn(7).when(spyRunner).getSongId(anyString(), anyString());

        clearInvocations(mockConnection);

        spyRunner.likeSong(song);

        verify(mockConnection).setAutoCommit(false);
        verify(spyRunner).insertSongIntoLikedTable(7);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testLikeSongNewSong() throws SQLException {
        Song song = new Song(0, "New Song", "New Artist", "http://example.com", "vid456");

        DatabaseRunner spyRunner = spy(databaseRunner);
        doReturn(-1).when(spyRunner).getSongId(anyString(), anyString());
        doReturn(8).when(spyRunner).insertSongIntoSongs(any(Song.class));

        clearInvocations(mockConnection);

        spyRunner.likeSong(song);

        verify(mockConnection).setAutoCommit(false);
        verify(spyRunner).insertSongIntoSongs(song);
        verify(spyRunner).insertSongIntoLikedTable(8);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testAddToRecents() throws SQLException {
        Song song = new Song(0, "Recent Song", "Recent Artist", "http://example.com", "vid789");

        DatabaseRunner spyRunner = spy(databaseRunner);
        doReturn(9).when(spyRunner).getSongId(anyString(), anyString());

        clearInvocations(mockConnection);

        spyRunner.addToRecents(song);

        verify(mockConnection).setAutoCommit(false);
        verify(spyRunner).insertSongIntoRecentlyPlayed(9);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testDownloadSong() throws SQLException {
        Song song = new Song(0, "Download Song", "Download Artist", "http://example.com", "vid101112");
        Song downloadedSong = new Song(10, "Download Song", "Download Artist", "http://example.com", "vid101112", 
                false, true, false, "/path/to/download.mp3", "hash123456");

        DatabaseRunner spyRunner = spy(databaseRunner);
        doReturn(10).when(spyRunner).getSongId(anyString(), anyString());
        doReturn(downloadedSong).when(spyRunner).download(song);

        clearInvocations(mockConnection);

        spyRunner.downloadSong(song);

        verify(mockConnection).setAutoCommit(false);
        verify(spyRunner).download(song);
        verify(spyRunner).insertSongIntoDownloadTable(10, "/path/to/download.mp3", "hash123456");
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testGenerateHashFromFile() throws IOException {
        File tempFile = tempDir.resolve("test.mp3").toFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("test data for hashing".getBytes());
        }

        String result = databaseRunner.generateHashFromFile(tempFile.getAbsolutePath());

        assertNotNull(result);
        assertEquals(64, result.length());
    }

    @Test
    public void testInsertSongIntoSongs() throws SQLException {
        Song song = new Song(0, "Insert Test", "Insert Artist", "http://example.com", "vidInsert");

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(11);

        int result = databaseRunner.insertSongIntoSongs(song);

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> artistCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockPreparedStatement).setString(eq(1), titleCaptor.capture());
        verify(mockPreparedStatement).setString(eq(2), artistCaptor.capture());
        verify(mockPreparedStatement).setString(eq(3), anyString());
        verify(mockPreparedStatement).setString(eq(4), anyString());
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).getGeneratedKeys();

        assertEquals("Insert Test", titleCaptor.getValue());
        assertEquals("Insert Artist", artistCaptor.getValue());
        assertEquals(11, result);
    }

    @Test
    public void testInsertSongIntoLikedTable() throws SQLException {
        databaseRunner.insertSongIntoLikedTable(12);

        verify(mockPreparedStatement).setInt(1, 12);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
    }

    @Test
    public void testInsertSongIntoDownloadTable() throws SQLException {
        databaseRunner.insertSongIntoDownloadTable(13, "/path/file.mp3", "filehash123");

        verify(mockPreparedStatement).setInt(1, 13);
        verify(mockPreparedStatement).setString(2, "/path/file.mp3");
        verify(mockPreparedStatement).setString(3, "filehash123");
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
    }

    @Test
    public void testInsertSongIntoRecentlyPlayed() throws SQLException {
        databaseRunner.insertSongIntoRecentlyPlayed(14);

        verify(mockPreparedStatement).setInt(1, 14);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
    }

    @Test
    public void testShutdown() {
        databaseRunner.shutdown();

        verify(mockLinker).shutdown();
    }

    @Test
    public void testHandleSQLException() throws SQLException {
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Test exception"));

        databaseRunner.getLikedSongs();

        verify(mockConnection).rollback();
    }
}
