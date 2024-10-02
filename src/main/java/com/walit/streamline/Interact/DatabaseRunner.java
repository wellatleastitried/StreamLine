package com.walit.streamline.Interact;

import com.walit.streamline.Communicate.StreamLineMessages;
import com.walit.streamline.AudioHandle.Song;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public final class DatabaseRunner {

    private final Connection connection;
    private final HashMap<String, String> queryMap;

    public DatabaseRunner(Connection connection, HashMap<String, String> queryMap) {
        this.connection = connection;
        this.queryMap = queryMap;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.DisableAutoCommitFailure.getMessage());
            System.exit(1);
        }
    }

    public void ensureCorrectSizeOfRecentlyPlayedTable() {
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(queryMap.get("ensureRecentlyPlayedCount"));
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    public HashMap<Integer, Song> getLikedSongs() {
        HashMap<Integer, Song> likedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(queryMap.get("getLikedSongs"));
            while (rs.next()) {
                Song song = new Song(rs.getInt("song_id"), rs.getString("title"), rs.getString("artist"), rs.getString("url"));
                likedSongs.put(rs.getInt("song_id"), song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return likedSongs;
    }

    public HashMap<Integer, Song> getDownloadedSongs() {
        HashMap<Integer, Song> downloadedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(queryMap.get("getDownloadedSongs"));
            while (rs.next()) {
                Song song = new Song(rs.getInt("song_id"), rs.getString("title"), rs.getString("artist"), rs.getString("url"));
                downloadedSongs.put(rs.getInt("song_id"), song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return downloadedSongs;
    }
    
    public HashMap<Integer, Song> getRecentlyPlayedSongs() {
        HashMap<Integer, Song> recentlyPlayedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(queryMap.get("getRecentlyPlayedSongs"));
            while (rs.next()) {
                Song song = new Song(rs.getInt("song_id"), rs.getString("title"), rs.getString("artist"), rs.getString("url"));
                recentlyPlayedSongs.put(rs.getInt("song_id"), song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return recentlyPlayedSongs;
    }

    public HashMap<Integer, Song> getSongsFromPlaylist(String playlistName) {
        HashMap<Integer, Song> songsFromPlaylist = new HashMap<>();
        final String playlistSongsQuery = "SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM PlaylistSongs WHERE playlist_id = ? ORDER BY data_added_to_playlist DESC);";
        try (PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setString(1, playlistName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Song song = new Song(rs.getInt("song_id"), rs.getString("title"), rs.getString("artist"), rs.getString("url"));
                songsFromPlaylist.put(rs.getInt("song_id"), song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return songsFromPlaylist;
    }

    public void likeSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            insertSongIntoSpecificTable("LikedSongs", songId);
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    public void downloadSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            download(song);
            insertSongIntoSpecificTable("DownloadedSongs", songId);
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    private void download(Song song) {
        // Convert video to mp3 and download
    }

    private int getSongId(String title, String artist) throws SQLException {
        final String checkIfSongExists = "SELECT song_id FROM Songs WHERE title = ? AND artist = ?;";
        try (PreparedStatement checkSong = connection.prepareStatement(checkIfSongExists)) {
            checkSong.setString(1, title);
            checkSong.setString(2, artist);
            try (ResultSet rs = checkSong.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private int insertSongIntoSongs(Song song) throws SQLException {
        final String insertIntoSongs = "INSERT INTO Songs (title, artist, url) VALUES(?, ?, ?);";
        try (PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs)) {
            insertSongStatement.setString(1, song.getSongName());
            insertSongStatement.setString(2, song.getSongArtist());
            insertSongStatement.setString(3, song.getSongLink());
            insertSongStatement.executeUpdate();
            try (ResultSet generatedKeys = insertSongStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to insert song, no ID was generated.");
                }
            }
        }
    }

    public void clearCachedSongs() {
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(queryMap.get("CLEAR_CACHE"));
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    private void insertSongIntoSpecificTable(String tableName, int songId) throws SQLException {
        final String insertIntoLikedSongs = "INSERT INTO LikedSongs (song_id, date_liked) VALUES (?, CURRENT_TIMESTAMP);";
        try (PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setString(1, tableName);
            insertSongStatement.setInt(2, songId);
            insertSongStatement.executeUpdate();
        }
    }

    private void handleSQLException(SQLException sE) {
        System.err.println(StreamLineMessages.SQLQueryError.getMessage());
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            System.err.println(StreamLineMessages.RollbackError.getMessage());
            System.exit(1);
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.AutoCommitRestoreFailure.getMessage());
            System.exit(1);
        }
    }
}
