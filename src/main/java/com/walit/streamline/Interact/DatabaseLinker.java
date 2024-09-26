package com.walit.streamline.Interact;

import com.walit.streamline.Communicate.StreamLineMessages;
import com.walit.streamline.Communicate.OS;

import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseLinker {

    protected final OS osName;
    protected final String PATH;
    private Connection connection;
    private final boolean isNewDatabase;

    public DatabaseLinker(OS osName) {
        this.osName = osName;
        this.PATH = setupPath(this.osName);
        new File(this.PATH).getParentFile().mkdirs();
        this.isNewDatabase = needsNewDatabase(PATH);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.PATH);
            if (this.isNewDatabase) {
                setupNewDatabase();
            }
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.GetDBConnectionFailure.getMessage());
            System.exit(1);
        }
    }

    private Connection getConnection() {
        return this.connection;
    }

    private void setupNewDatabase() {
        try {
            final String creationQuery = getDBCreationString();
            final Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(creationQuery);
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.DBCreationFailure.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.UnknownDBFatalError.getMessage());
            System.exit(1);
        }
    }

    public boolean close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.DBCloseError.getMessage());
            return false;
        }
        return true;
    }

    public boolean needsNewDatabase(String path) {
        return !(new File(path).exists());
    }

    private String getDBCreationString() {
        StringBuilder sB = new StringBuilder();
        final String createSongsTable = "CREATE TABLE IF NOT EXISTS Songs (song_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, artist TEXT NOT NULL, url TEXT NOT NULL, downloaded BIT NOT NULL);";
        final String createPlaylistTable = "CREATE TABLE IF NOT EXISTS Playlists (playlist_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);";
        final String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS PlaylistSongs (playlist_id INTEGER NOT NULL, song_id INTEGER NOT NULL, PRIMARY KEY (playlist_id, song_id), FOREIGN KEY (playlist_id) REFERENCES Playlists(playlist_id) ON DELETE CASCADE, FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE);";
        final String createRecentlyPlayedTable = "CREATE TABLE IF NOT EXISTS RecentlyPlayed (song_id INTEGER NOT NULL, last_listen DATETIME DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE);";
        sB.append(createSongsTable);
        sB.append(createPlaylistTable);
        sB.append(createPlaylistSongsTable);
        sB.append(createRecentlyPlayedTable);
        return sB.toString();
    }

    private String setupPath(OS name) {
        switch (name) {
            case WINDOWS -> {
                return System.getProperty("user.home") + "Windows path";
            }
            case LINUX -> {
                return System.getProperty("user.home") + "/.config/StreamLine/storage/streamline.db";
            }
            case MAC -> {
                return System.getProperty("user.home") + "MAC path";
            }
            case TESTING -> {
                return "/tmp/StreamLine/TEST.db";
            }
            default -> {
                System.err.println(StreamLineMessages.FatalPathError.getMessage());
                System.exit(1);
            }
        }
        return null;
    }
}
