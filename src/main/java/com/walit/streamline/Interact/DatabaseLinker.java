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

    public DatabaseLinker(OS osName, String creationString) {
        this.osName = osName;
        this.PATH = setupPath(this.osName);
        new File(this.PATH).getParentFile().mkdirs();
        this.isNewDatabase = needsNewDatabase(PATH);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.PATH);
            if (this.isNewDatabase) {
                setupNewDatabase(creationString);
            }
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.GetDBConnectionFailure.getMessage());
            System.exit(1);
        }
    }

    private Connection getConnection() {
        return this.connection;
    }

    private void setupNewDatabase(String query) {
        try {
            final Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);
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
