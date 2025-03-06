package com.walit.streamline.database;

import com.walit.streamline.utilities.internal.StreamLineMessages;
import com.walit.streamline.audio.Song;
import com.walit.streamline.utilities.RetrievedStorage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class DatabaseRunner {

    private final Connection connection;
    private final HashMap<String, String> queryMap;
    private final Logger logger;

    public DatabaseRunner(Connection connection, HashMap<String, String> queryMap, Logger logger) {
        this.connection = connection;
        this.queryMap = queryMap;
        this.logger = logger;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException sE) {
            logger.log(Level.SEVERE, StreamLineMessages.DisableAutoCommitFailure.getMessage());
            System.exit(1);
        }
    }

    public RetrievedStorage getLikedSongs() {
        final RetrievedStorage likedSongs = new RetrievedStorage();
        try {
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            final ResultSet rs = statement.executeQuery(queryMap.get("GET_LIKED_SONGS"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId")
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
            final ResultSet rs = statement.executeQuery(queryMap.get("GET_DOWNLOADED_SONGS"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId")
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
            final ResultSet rs = statement.executeQuery(queryMap.get("GET_RECENTLY_PLAYED_SONGS"));
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId")
                );
                song.setSongRecentlyPlayedStatus(true);
                recentlyPlayedSongs.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return recentlyPlayedSongs;
    }

    public Song searchForSongName(String songName) {
        final String searchQuery = "SELECT * FROM Songs s WHERE s.title like '%" + songName + "%'";
        Song song = null;
        boolean hasResult = false;
        try (final Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            final ResultSet rs = statement.executeQuery(searchQuery);
            while (rs.next()) {
                if (hasResult) {
                    song = null;
                    break;
                }
                song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId")
                );
                hasResult = true;
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
            song = null;
        }
        return song;
    }

    public RetrievedStorage getSongsFromPlaylist(String playlistName) {
        final RetrievedStorage songsFromPlaylist = new RetrievedStorage();
        final String playlistSongsQuery = "SELECT * FROM Songs s WHERE s.id IN (SELECT song_id FROM PlaylistSongs WHERE playlist_id = ? ORDER BY data_added_to_playlist DESC);";
        try (final PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setString(1, playlistName);
            final ResultSet rs = statement.executeQuery();
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId")
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
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId"),
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
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    /**
     * This function is called when a song is liked. The other functions (such as insertSongIntoLikedSongs()) are for internal use as they do not provide verification of the existence of the song in other tables on their own.
     */
    public void likeSong(Song song) {
        try {
            connection.setAutoCommit(false);
            // TODO: NEED TO CHECK LIKEDSONGS TABLE AS WELL
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
                song.getSongVideoId(),
                song.isSongLiked(),
                true,
                song.isSongRecentlyPlayed(),
                filePath,
                fileHash
        ); 
    }

    protected String generateHashFromFile(String path) {
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
            logger.log(Level.WARNING, "There is a typo in the name of the hashing algorithm being used.");
            System.exit(1);
        } catch (IOException iE) {
            logger.log(Level.WARNING, StreamLineMessages.HashingFileInputStreamError.getMessage());
            System.exit(1);
        }
        return null;
    }

    private int getSongId(String title, String artist) throws SQLException {
        final String checkIfSongExists = "SELECT id FROM Songs WHERE title = ? AND artist = ?;";
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

    protected int insertSongIntoSongs(Song song) throws SQLException {
        final String insertIntoSongs = "INSERT OR IGNORE INTO Songs (title, artist, url, videoId) VALUES(?, ?, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs)) {
            insertSongStatement.setString(1, song.getSongName());
            insertSongStatement.setString(2, song.getSongArtist());
            insertSongStatement.setString(3, song.getSongLink());
            insertSongStatement.setString(4, song.getSongVideoId());
            insertSongStatement.executeUpdate();
            connection.commit();
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
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    protected void insertSongIntoRecentlyPlayed(int songId) throws SQLException {
        final String insertIntoRecentlyPlayed = "INSERT INTO RecentlyPlayed (song_id, last_listen) VALUES (?, CURRENT_TIMESTAMP);";
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertIntoRecentlyPlayed)) {
            insertStatement.setInt(1, songId);
            insertStatement.executeUpdate();
            connection.commit();
        }
    }

    protected void insertSongIntoDownloadTable(int songId, String filePath, String fileHash) throws SQLException {
        final String insertIntoLikedSongs = "INSERT INTO DownloadedSongs (song_id, date_downloaded, file_path, file_hash) VALUES (?, CURRENT_TIMESTAMP, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.setString(2, filePath);
            insertSongStatement.setString(3, fileHash);
            insertSongStatement.executeUpdate();
            connection.commit();
        }
    }

    protected void insertSongIntoLikedTable(int songId) throws SQLException {
        final String insertIntoLikedSongs = "INSERT INTO LikedSongs (song_id, date_liked) VALUES (?, CURRENT_TIMESTAMP);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.executeUpdate();
            connection.commit();
        }
    }

    private void handleSQLException(SQLException sE) {
        sE.printStackTrace();
        logger.log(Level.WARNING, StreamLineMessages.SQLQueryError.getMessage());
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            logger.log(Level.SEVERE, StreamLineMessages.RollbackError.getMessage());
            System.exit(1);
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException sE) {
            logger.log(Level.SEVERE, StreamLineMessages.AutoCommitRestoreFailure.getMessage());
            System.exit(1);
        }
    }
}
