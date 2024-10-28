package com.walit.streamline.Interact;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;
import com.walit.streamline.AudioHandle.Song;
import com.walit.streamline.Utilities.RetrievedStorage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public RetrievedStorage getLikedSongs() {
        final RetrievedStorage likedSongs = new RetrievedStorage();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            final ResultSet rs = statement.executeQuery(queryMap.get("getLikedSongs"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url")
                );
                song.setSongLikeStatus(true);
                likedSongs.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return likedSongs;
    }

    public RetrievedStorage getDownloadedSongs() {
        final RetrievedStorage downloadedSongs = new RetrievedStorage();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            final ResultSet rs = statement.executeQuery(queryMap.get("getDownloadedSongs"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url")
                );
                downloadedSongs.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return downloadedSongs;
    }
    
    public RetrievedStorage getRecentlyPlayedSongs() {
        final RetrievedStorage recentlyPlayedSongs= new RetrievedStorage();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            final ResultSet rs = statement.executeQuery(queryMap.get("getRecentlyPlayedSongs"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url")
                );
                song.setSongRecentlyPlayedStatus(true);
                recentlyPlayedSongs.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return recentlyPlayedSongs;
    }

    public RetrievedStorage getSongsFromPlaylist(String playlistName) {
        final RetrievedStorage songsFromPlaylist = new RetrievedStorage();
        final String playlistSongsQuery = "SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM PlaylistSongs WHERE playlist_id = ? ORDER BY data_added_to_playlist DESC);";
        try (final PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setString(1, playlistName);
            final ResultSet rs = statement.executeQuery();
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url")
                );
                songsFromPlaylist.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return songsFromPlaylist;
    }

    public RetrievedStorage getExpiredCache() {
        final RetrievedStorage expiredSongs = new RetrievedStorage();
        try (final Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            final ResultSet rs = statement.executeQuery(queryMap.get("GET_EXPIRED_CACHE"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        false,
                        true,
                        false,
                        rs.getString("file_path"),
                        rs.getString("file_hash")
                );
                expiredSongs.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return expiredSongs;
    }
    
    public void clearExpiredCache() {
        try (final Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            statement.executeUpdate(queryMap.get("CLEAR_EXPIRED_CACHE"));
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    public void likeSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            insertSongIntoLikedTable(songId);
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
            final Song storedSong = download(song);
            insertSongIntoDownloadTable(songId, storedSong.getDownloadPath(), storedSong.getFileHash());
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    public void addToRecents(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            insertSongIntoRecentlyPlayed(songId);
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    private Song download(Song song) {
        // Convert video to mp3 and download
        final String filePath = String.format("%s-%s.mp3", song.getSongName(), song.getSongArtist()); 
        final String fileHash = generateHashFromFile(filePath);
        return new Song(
                song.getSongId(),
                song.getSongName(),
                song.getSongArtist(),
                song.getSongLink(),
                song.isSongLiked(),
                true,
                song.isSongRecentlyPlayed(),
                filePath,
                fileHash
        ); 
    }

    private String generateHashFromFile(String path) {
        try (FileInputStream fS = new FileInputStream(new File(path))) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] byteArray = new byte[1024];
            int byteCount;
            while ((byteCount = fS.read(byteArray)) != -1) {
                digest.update(byteArray, 0, byteCount);
            }
            final byte[] bytes = digest.digest();
            final StringBuilder hexStringOfHash = new StringBuilder(new BigInteger(1, bytes).toString(16));
            while (hexStringOfHash.length() < 64) {
                hexStringOfHash.insert(0, '0');
            }
            return hexStringOfHash.toString();
        } catch (NoSuchAlgorithmException nA) {
            System.err.println("There is a typo in the name of the hashing algorithm being used.");
            System.exit(1);
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.HashingFileInputStreamError.getMessage());
            System.exit(1);
        }
        return null;
    }

    private int getSongId(String title, String artist) throws SQLException {
        final String checkIfSongExists = "SELECT song_id FROM Songs WHERE title = ? AND artist = ?;";
        try (PreparedStatement checkSong = connection.prepareStatement(checkIfSongExists)) {
            checkSong.setString(1, title);
            checkSong.setString(2, artist);
            try (final ResultSet rs = checkSong.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private int insertSongIntoSongs(Song song) throws SQLException {
        final String insertIntoSongs = "INSERT OR IGNORE INTO Songs (title, artist, url) VALUES(?, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs)) {
            insertSongStatement.setString(1, song.getSongName());
            insertSongStatement.setString(2, song.getSongArtist());
            insertSongStatement.setString(3, song.getSongLink());
            insertSongStatement.executeUpdate();
            try (final ResultSet generatedKeys = insertSongStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to insert song, no ID was generated.");
                }
            }
        }
    }

    public void clearCachedSongs() {
        try (final Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            statement.executeUpdate(queryMap.get("CLEAR_CACHE"));
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    private void insertSongIntoRecentlyPlayed(int songId) throws SQLException {
        final String insertIntoRecentlyPlayed = "INSERT INTO RecentlyPlayed (song_id, last_listen) VALUES (?, CURRENT_TIMESTAMP);";
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertIntoRecentlyPlayed)) {
            insertStatement.setInt(1, songId);
            insertStatement.executeUpdate();
        }
    }

    private void insertSongIntoDownloadTable(int songId, String filePath, String fileHash) throws SQLException {
        final String insertIntoLikedSongs = "INSERT INTO DownloadedSongs (song_id, date_downloaded, file_path, file_hash) VALUES (?, CURRENT_TIMESTAMP, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.setString(2, filePath);
            insertSongStatement.setString(3, fileHash);
            insertSongStatement.executeUpdate();
        }
    }

    private void insertSongIntoLikedTable(int songId) throws SQLException {
        final String insertIntoLikedSongs = "INSERT INTO LikedSongs (song_id, date_liked) VALUES (?, CURRENT_TIMESTAMP);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setInt(1, songId);
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
