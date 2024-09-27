package com.walit.streamline.Interact;

import com.walit.streamline.Communicate.StreamLineMessages;

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

    public HashMap<Integer, HashMap<String, String>> getLikedSongs() {
        HashMap<Integer, HashMap<String, String>> likedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(60);
            ResultSet rs = statement.executeQuery(queryMap.get("getLikedSongs"));
            while (rs.next()) {
                HashMap<String, String> songDetails = new HashMap<>();
                songDetails.put("title", rs.getString("title"));
                songDetails.put("artist", rs.getString("artist"));
                songDetails.put("url", rs.getString("url"));
                likedSongs.put(rs.getInt("song_id"), songDetails);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return likedSongs;
    }

    public HashMap<Integer, HashMap<String, String>> getDownloadedSongs() {
        HashMap<Integer, HashMap<String, String>> downloadedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(60);
            ResultSet rs = statement.executeQuery(queryMap.get("getDownloadedSongs"));
            while (rs.next()) {
                HashMap<String, String> songDetails = new HashMap<>();
                songDetails.put("title", rs.getString("title"));
                songDetails.put("artist", rs.getString("artist"));
                songDetails.put("url", rs.getString("url"));
                downloadedSongs.put(rs.getInt("song_id"), songDetails);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return downloadedSongs;
    }
    
    public HashMap<Integer, HashMap<String, String>> getRecentlyPlayedSongs() {
        HashMap<Integer, HashMap<String, String>> recentlyPlayedSongs = new HashMap<>();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(queryMap.get("getRecentlyPlayedSongs"));
            while (rs.next()) {
                HashMap<String, String> songDetails = new HashMap<>();
                songDetails.put("title", rs.getString("title"));
                songDetails.put("artist", rs.getString("artist"));
                songDetails.put("url", rs.getString("url"));
                recentlyPlayedSongs.put(rs.getInt("song_id"), songDetails);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return recentlyPlayedSongs;
    }

    public HashMap<Integer, HashMap<String, String>> getSongsFromPlaylist(String playlistName) {
        HashMap<Integer, HashMap<String, String>> songsFromPlaylist = new HashMap<>();
        final String playlistSongsQuery = "SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM PlaylistSongs WHERE playlist_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setString(1, playlistName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                HashMap<String, String> songDetails = new HashMap<>();
                songDetails.put("title", rs.getString("title"));
                songDetails.put("artist", rs.getString("artist"));
                songDetails.put("url", rs.getString("url"));
                songsFromPlaylist.put(rs.getInt("song_id"), songDetails);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return songsFromPlaylist;
    }

    public void likeSong(String title, String artist, String url) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(title, artist);
            if (songId == -1) {
                songId = insertSongIntoSongs(title, artist, url);
            }
            insertSongIntoSpecificTable("LikedSongs", songId);
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    public void downloadSong(String title, String artist, String url) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(title, artist);
            if (songId == -1) {
                songId = insertSongIntoSongs(title, artist, url);
            }
            download(title, artist, url);
            insertSongIntoSpecificTable("DownloadedSongs", songId);
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    private void download(String title, String artist, String url) {
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

    private int insertSongIntoSongs(String title, String artist, String url) throws SQLException {
        final String insertIntoSongs = "INSERT INTO Songs (title, artist, url) VALUES(?, ?, ?);";
        try (PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs)) {
            insertSongStatement.setString(1, title);
            insertSongStatement.setString(2, artist);
            insertSongStatement.setString(3, url);
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
