package com.streamline.database;

import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.utilities.RetrievedStorage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

/**
 * Runs queries with the local database and returns the results in a more convenient format. {@link RetrievedStorage}
 * @author wellatleastitried
 */
public final class DatabaseRunner {

    private final Connection connection;
    private final Map<String, String> queryMap;
    private final DatabaseLinker linker;

    public DatabaseRunner(Connection connection, Map<String, String> queryMap, DatabaseLinker linker) {
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

    public boolean getSongLikeStatus(Song song) {
        final String songLikeStatusQuery = "SELECT ls.song_id FROM LikedSongs ls INNER JOIN Songs s ON ls.song_id = s.id WHERE s.title = ? AND s.artist = ? AND s.videoId = ?;";
        try (PreparedStatement statement = connection.prepareStatement(songLikeStatusQuery)) {
            statement.setString(1, song.getSongName());
            statement.setString(2, song.getSongArtist());
            statement.setString(3, song.getSongVideoId());
            final ResultSet rs = statement.executeQuery();
            if (!rs.isBeforeFirst()) {
                Logger.debug("Song not found in LikedSongs table.");
                return false;
            }
            while (rs.next()) {
                Logger.debug("Song found in LikedSongs table with ID: " + rs.getInt("song_id"));
                return true;
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return false;
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
                        rs.getString("videoId"),
                        rs.getString("duration")
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
                        rs.getString("videoId"),
                        rs.getString("duration"),
                        false,
                        true,
                        false,
                        rs.getString("file_path"),
                        rs.getString("file_hash")
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
                        rs.getString("videoId"),
                        rs.getString("duration")
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
            String duration = rs.getString("duration");

            Song song = new Song(id, title, artist, url, videoId, duration);

            rs.close();
            return song;
        } catch (SQLException e) {
            handleSQLException(e);
            return null;
        }
    }

    public RetrievedStorage getSongsFromPlaylist(int playlistId, String playlistName) {
        final RetrievedStorage songsFromPlaylist = new RetrievedStorage();
        final String playlistSongsQuery = "SELECT s.* FROM Songs s INNER JOIN PlaylistSongs ps ON s.id = ps.song_id WHERE ps.playlist_id = ? AND ps.name = ? ORDER BY ps.date_added_to_playlist DESC;";
        try (final PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setInt(1, playlistId);
            statement.setString(2, playlistName);
            final ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                Logger.info("[*] No playlist found with the name: {}, attempting to search only by id...", playlistName);
                return getSongsFromPlaylist(playlistId);
            }
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId"),
                        rs.getString("duration")
                        );
                songsFromPlaylist.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return songsFromPlaylist;
    }

    public RetrievedStorage getSongsFromPlaylist(int playlistId) {
        final RetrievedStorage songsFromPlaylist = new RetrievedStorage();
        final String playlistSongsQuery = "SELECT s.* FROM Songs s INNER JOIN PlaylistSongs ps ON s.id = ps.song_id WHERE ps.playlist_id = ? ORDER BY ps.date_added_to_playlist DESC;";
        try (final PreparedStatement statement = connection.prepareStatement(playlistSongsQuery)) {
            statement.setInt(1, playlistId);
            final ResultSet rs = statement.executeQuery();
            int index = 0;
            while (rs.next()) {
                final Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("videoId"),
                        rs.getString("duration")
                        );
                songsFromPlaylist.add(++index, song);
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return songsFromPlaylist;
    }

    public List<Playlist> getPlaylists() {
        try (final Statement playlistQuery = connection.createStatement()) {
            final ResultSet rs = playlistQuery.executeQuery(queryMap.get("GET_PLAYLISTS"));
            List<Playlist> playlists = new ArrayList<>();
            while (rs.next()) {
                final Playlist playlist = new Playlist(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                playlists.add(playlist);
            }
            if (playlists.isEmpty()) {
                Logger.debug("No playlists found.");
                return null;
            }
            return playlists;
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return null;
    }

    public int getSongIdFromLikedTable(Song songToSearch) {
        final String likedSongQuery = "SELECT ls.song_id FROM LikedSongs INNER JOIN Songs s ON ls.song_id = s.id WHERE s.title = ? AND s.artist = ? AND s.videoId = ?;";
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
                        rs.getString("duration"),
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
    public boolean handleLikeSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            Logger.debug("Song ID from Songs query: " + songId);
            if (songId == -1) {
                Logger.debug("Song not found in the Songs table; making sure it exists in the Songs table.");
                songId = insertSongIntoSongs(song);
                Logger.debug("Song inserted into Songs table with ID: " + songId);
            }
            if (song.isSongLiked()) {
                Logger.debug("Removing song from LikedSongs table with song_id: " + songId);
                removeSongFromLikedTable(songId);
                Logger.debug("Song removed from LikedSongs table with song_id: " + songId);
            } else {
                Logger.debug("Inserting song into LikedSongs table with song_id: " + songId);
                insertSongIntoLikedTable(songId);
                Logger.debug("Song inserted into LikedSongs table with song_id: " + songId);
            }
            return true;
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
        return false;
    }

    public void downloadSong(Song song) {
        try {
            connection.setAutoCommit(false);
            int songId = getSongId(song.getSongName(), song.getSongArtist());
            if (songId == -1) {
                songId = insertSongIntoSongs(song);
            }
            insertSongIntoDownloadTable(songId, song.getDownloadPath(), song.getFileHash());
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    public void removeDownloadedSong(Song song) {
        try {
            final PreparedStatement statement = connection.prepareStatement("DELETE ds FROM DownloadedSongs ds INNER JOIN Songs s ON ds.song_id = s.id WHERE s.title = ? AND s.artist = ? and s.url = ?");
            statement.setQueryTimeout(5);
            statement.setString(1, song.getSongName());
            statement.setString(2, song.getSongArtist());
            statement.setString(3, song.getSongLink());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        } finally {
            restoreAutoCommit();
        }
    }

    public Song getSongFromDownloads(Song song) {
        final String query = "SELECT * FROM DownloadedSongs ds INNER JOIN Songs s ON s.id = ds.song_id WHERE s.title = ? AND s.artist = ? AND s.url = ?;";
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setQueryTimeout(5);
            statement.setString(1, song.getSongName());
            statement.setString(2, song.getSongArtist());
            statement.setString(3, song.getSongLink());
            try (final ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    song = new Song(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("artist"),
                            rs.getString("url"),
                            rs.getString("videoId"),
                            rs.getString("duration"),
                            getSongLikeStatus(song),
                            true,
                            false,
                            rs.getString("file_path"),
                            rs.getString("file_hash")
                            );
                }
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return song;
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

    protected int getSongId(Song song) throws SQLException {
        final String checkIfSongExists = "SELECT id FROM Songs WHERE title = ? AND artist = ? AND url = ? AND videoId = ?;";
        try (PreparedStatement checkSong = connection.prepareStatement(checkIfSongExists)) {
            checkSong.setString(1, song.getSongName());
            checkSong.setString(2, song.getSongArtist());
            checkSong.setString(3, song.getSongLink());
            checkSong.setString(4, song.getSongVideoId());
            try (final ResultSet rs = checkSong.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public int getPlaylistId(String playlistName) {
        final String getPlaylistId = "SELECT id FROM Playlists WHERE name = ?;";
        try (PreparedStatement playlistStatement = connection.prepareStatement(getPlaylistId)) {
            playlistStatement.setString(1, playlistName);
            try (final ResultSet rs = playlistStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
        return -1;
    }

    public void createPlaylist(String playlistName) {
        final String createPlaylist = "INSERT INTO Playlists (name) VALUES (?);";
        try (PreparedStatement playlistStatement = connection.prepareStatement(createPlaylist)) {
            playlistStatement.setString(1, playlistName);
            playlistStatement.executeUpdate();
            connection.commit();
        } catch (SQLException sE) {
            handleSQLException(sE);
        }
    }

    protected int insertSongIntoSongs(Song song) throws SQLException {
        final String insertIntoSongs = "INSERT INTO Songs (title, artist, url, videoId, duration) VALUES (?, ?, ?, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoSongs)) {
            insertSongStatement.setString(1, song.getSongName());
            insertSongStatement.setString(2, song.getSongArtist());
            insertSongStatement.setString(3, song.getSongLink());
            insertSongStatement.setString(4, song.getSongVideoId());
            insertSongStatement.setString(5, song.getDuration());
            insertSongStatement.executeUpdate();
            connection.commit();
        }
        return getSongId(song);
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
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoRecentlyPlayed)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.executeUpdate();
            connection.commit();
        }
    }

    protected void insertSongIntoDownloadTable(int songId, String filePath, String fileHash) throws SQLException {
        final String insertIntoDownloadedSongs = "INSERT INTO DownloadedSongs (song_id, date_downloaded, file_path, file_hash) VALUES (?, CURRENT_TIMESTAMP, ?, ?);";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(insertIntoDownloadedSongs)) {
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

    protected void removeSongFromLikedTable(int songId) throws SQLException {
        final String removeLikedSong = "DELETE FROM LikedSongs WHERE song_id = ?;";
        try (final PreparedStatement insertSongStatement = connection.prepareStatement(removeLikedSong)) {
            insertSongStatement.setInt(1, songId);
            insertSongStatement.executeUpdate();
            connection.commit();
            Logger.debug("Song deletion query committed into LikedSongs table with ID: " + songId);
        }
    }

    private void handleSQLException(SQLException sE) {
        Logger.warn("[!] Unable to execute query on the database, please try restarting the app.");
        Logger.debug("[!] SQL error message: " + sE.getMessage());
        Writer buffer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(buffer);
        sE.printStackTrace(printWriter);
        Logger.debug("[!] SQL error stack trace: " + buffer.toString());
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            Logger.error("[!] Fatal database error:");
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
