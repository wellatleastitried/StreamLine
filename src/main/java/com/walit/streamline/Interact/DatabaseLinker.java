package com.walit.streamline.Interact;

import com.walit.streamline.Utilities.Internal.StreamLineMessages;
import com.walit.streamline.Utilities.Internal.OS;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseLinker {

    protected final OS osName;
    protected final String PATH;
    private Connection connection;
    private final boolean isNewDatabase;

    public DatabaseLinker(OS osName, String tableCreation) {
        this.osName = osName;
        this.PATH = setupPath(this.osName);
        new File(PATH).getParentFile().mkdirs();
        this.isNewDatabase = needsNewDatabase(PATH);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            if (this.isNewDatabase) {
                setupNewDatabase(tableCreation);
            }
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.GetDBConnectionFailure.getMessage());
            System.exit(1);
        }
    }

    /**
     * Allows com.walit.streamline.Interact.DatabaseRunner to fetch the existing connection so that it does not have to create its own.
     * @return The established connection to the database.
     */
    public Connection getConnection() {
        return this.connection;
    }

    private void setupNewDatabase(String tables) {
        try {
            final Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(tables);
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.DBCreationFailure.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.UnknownDBFatalError.getMessage());
            System.exit(1);
        }
    }

    public boolean shutdown() {
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

    /**
     * Checks if the database exists and, if it doesn't, returns false so that a new one can be created.
     */
    public boolean needsNewDatabase(String path) {
        return !(new File(path).exists());
    }

    private String setupPath(OS name) {
        switch (name) {
            case WINDOWS -> {
                return System.getProperty("APPDATA") + "\\StreamLine\\streamline.db";
            }
            case LINUX -> {
                return System.getProperty("user.home") + "/.config/StreamLine/storage/streamline.db";
            }
            case MAC -> {
                return System.getProperty("user.home") + "/Library/Application Support/StreamLine/streamline.db";
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
