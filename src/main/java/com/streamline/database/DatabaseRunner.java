package com.streamline.database;

import com.streamline.audio.Song;
import com.streamline.utilities.RetrievedStorage;

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

import org.tinylog.Logger;

/**
 * Runs queries with the local database and returns the results in a more convenient format. {@link RetrievedStorage}
 * @author wellatleastitried
 */
public final class DatabaseRunner {

    private final Connection connection;
    private final HashMap<String, String> queryMap;
    private final DatabaseLinker linker;

    public DatabaseRunner(Connection connection, HashMap<String, String> queryMap, DatabaseLinker linker) {
        this.connection = connection;
        this.queryMap = queryMap;
        this.linker = linker;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException sE) {
            Logger.error("[!] Unable to disable auto-commit feature with connection, please restart the app.");
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
        final String searchQuery = "SELECT * FROM Songs s WHERE s.title LIKE ?;";
        try (PreparedStatement statement = connection.prepareStatement(searchQuery)) {
            statement.setString(1, "%" + songName + "%");
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                return null;
            }
            
            rs.first();
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String artist = rs.getString("artist");
            String url = rs.getString("url");
            String videoId = rs.getString("videoId");
            
            Song song = new Song(id, title, artist, url, videoId);
            
            rs.close();
            return song;
        } catch (SQLException e) {
            handleSQLException(e);
            return null;
        }
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

    /* TODO: Need to fix this method as it is throwing an error during build tests:
     *
     *
     [ERROR] com.streamline.database.DatabaseRunnerTest.testLikeSongExistingSong -- Time elapsed: 0.036 s <<< FAILURE!
     Wanted but not invoked:
     databaseRunner.insertSongIntoLikedTable(7);
     -> at com.streamline.database.DatabaseRunner.insertSongIntoLikedTable(DatabaseRunner.java:384)

     However, there were exactly 3 interactions with this mock:
     databaseRunner.handleLikeSong(
     com.streamline.audio.Song@7c8b37a8
     );
     -> at com.streamline.database.DatabaseRunnerTest.testLikeSongExistingSong(DatabaseRunnerTest.java:230)

     databaseRunner.getSongIdFromLikedTable(
     com.streamline.audio.Song@7c8b37a8
     );
     -> at com.streamline.database.DatabaseRunner.handleLikeSong(DatabaseRunner.java:228)

     databaseRunner.insertSongIntoSongs(
     com.streamline.audio.Song@7c8b37a8
     );
     -> at com.streamline.database.DatabaseRunner.handleLikeSong(DatabaseRunner.java:232)


     at com.streamline.database.DatabaseRunner.insertSongIntoLikedTable(DatabaseRunner.java:384)
     at com.streamline.database.DatabaseRunnerTest.testLikeSongExistingSong(DatabaseRunnerTest.java:233)
     *
     */
    public int getSongIdFromLikedTable(Song songToSearch) {
        final String likedSongQuery = "SELECT ls.song_id FROM LikedSongs ls WHERE ls.song_id IN (SELECT id FROM Songs s WHERE s.title = ? AND s.artist = ? AND s.videoId = ?);";
        try (final PreparedStatement statement = connection.prepareStatement(likedSongQuery)) {
            statement.setString(1, songToSearch.getSongName());
            statement.setString(2, songToSearch.getSongArtist());
            statement.setString(3, songToSearch.getSongVideoId());
            final ResultSet rs = statement.executeQuery();
            if (!rs.isBeforeFirst()) {
                return -1;
            }
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return -1;
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
    public void handleLikeSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongIdFromLikedTable(song);
            // int songId = getSongId(song.getSongName(), song.getSongArtist());
            Logger.debug("Song ID from LikedSongs query: " + songId);
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            if (song.isSongLiked()) {
                removeSongFromLikedTable(songId);
            } else {
                Logger.debug("Inserting song into LikedSongs table with ID: " + songId);
                insertSongIntoLikedTable(songId);
                Logger.debug("Song inserted into LikedSongs table with ID: " + songId);
            }
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
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    protected Song download(Song song) {
        // Convert video to mp3 and download
        final String filePath = String.format("%s-%s.mp3", song.getSongName(), song.getSongArtist()); 
        final String fileHash = generateHashFromFile(filePath);
        // TODO: Actually download the song
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
            Logger.error("There is a typo in the name of the hashing algorithm being used or Java no longer supports the used algorithm. Either way, it needs to be changed.");
        } catch (IOException iE) {
            Logger.error("[!] There has been an error reading the bytes from the configuration file, please try reloading the app.");
        }
        return null;
    }

    protected int getSongId(String title, String artist) throws SQLException {
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
                if (generatedKeys != null && generatedKeys.next()) {
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
            Logger.debug("Song insertion query committed into LikedSongs table with ID: " + songId);
        }
    }

    protected void removeSongFromLikedTable(int songId) throws SQLException {
        final String insertIntoLikedSongs = "DELETE FROM LikedSongs WHERE song_id = ?;";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoLikedSongs)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.executeUpdate();
            connection.commit();
        }
    }

    private void handleSQLException(SQLException sE) {
        Logger.warn("[!] Unable to execute query on the database, please try restarting the app.");
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            Logger.error("[!] Unable to rollback changes to database after an error.");
            System.exit(1);
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException sE) {
            Logger.error("[!] Failed to restore auto-commit feature to connection, please restart the app.");
            System.exit(1);
        }
    }

    public void shutdown() {
        linker.shutdown();
    }
}
