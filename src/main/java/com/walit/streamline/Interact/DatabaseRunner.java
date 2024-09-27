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
    }

    public void ensureCorrectSizeOfRecentlyPlayedTable() {
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(queryMap.get("ensureRecentlyPlayedCount"));
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.SQLQueryError.getMessage());
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
            System.err.println(StreamLineMessages.SQLQueryError.getMessage());
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
            System.err.println(StreamLineMessages.SQLQueryError.getMessage());
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
            System.err.println(StreamLineMessages.SQLQueryError.getMessage());
        }
        return recentlyPlayedSongs;
    }

    public void likeSong(String title, String artist, String url) {
        final String checkIfSongExists = "SELECT song_id FROM Songs WHERE title = ? AND artist = ?;";
        final String insertIntoSongs = "INSERT INTO Songs (title, artist, url) VALUES(?, ?, ?);";
        final String insertIntoLikedSongs = "INSERT INTO LikedSongs (song_id, date_liked) VALUES (?, CURRENT_TIMESTAMP);";
        boolean isAlreadyInSongs;
        int songIdFromTable;
        try (PreparedStatement checkSong = connection.prepareStatement(checkIfSongExists)) {
            checkSong.setString(1, title);
            checkSong.setString(2, artist);
            try (ResultSet rs = checkSong.executeQuery()) {
                if (!rs.next()) {
                    songIdFromTable = -1;
                    isAlreadyInSongs = false;
                } else {
                    songIdFromTable = rs.getInt(1);
                    isAlreadyInSongs = true;
                }
            }
            connection.setAutoCommit(false);
            try (PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs, Statement.RETURN_GENERATED_KEYS)) {
                insertSongStatement.setString(1, title);
                insertSongStatement.setString(2, artist);
                insertSongStatement.setString(3, url);
                if (!isAlreadyInSongs) {
                    insertSongStatement.executeUpdate();
                    try (ResultSet generatedKeys = insertSongStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int songId = generatedKeys.getInt(1);
                            try (PreparedStatement insertLikedSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
                                if (songIdFromTable == -1) {
                                    throw new Exception("[!] Fatal logic error, ID pulled from database is invalid.");
                                }
                                insertLikedSongStatement.setInt(1, songId);
                                insertLikedSongStatement.executeUpdate();
                            }
                        } else {
                            throw new SQLException("Failed to insert song, no ID was generated.");
                        }
                    }
                } else {
                    try (PreparedStatement insertLikedSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
                        insertLikedSongStatement.setInt(1, songIdFromTable);
                        insertLikedSongStatement.executeUpdate();
                    }
                }
            }
            connection.commit();
        } catch (SQLException sE) {
            try {
                System.err.println(StreamLineMessages.SQLQueryError.getMessage());
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.err.println(StreamLineMessages.RollbackError.getMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException sE) {
                System.err.println(StreamLineMessages.AutoCommitRestoreFailure.getMessage());
                System.exit(1);
            }
        }
    }
}
