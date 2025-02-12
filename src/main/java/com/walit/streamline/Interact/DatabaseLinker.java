package com.walit.streamline.Interact;

import com.walit.streamline.Utilities.Internal.StreamLineConstants;
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
        this.isNewDatabase = isDatabaseSetupAtPath(PATH);
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
     * Allows com.walit.streamline.Interact.DatabaseRunner to use the existing connection so that it does not have to create its own.
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

    public boolean isDatabaseSetupAtPath(String path) {
        return !(new File(path).exists());
    }

    private String setupPath(OS name) {
        switch (name) {
            case WINDOWS -> {
                return StreamLineConstants.WINDOWS_DB_ADDRESS;          
            }
            case LINUX -> {
                return StreamLineConstants.LINUX_DB_ADDRESS;
            }
            case MAC -> {
                return StreamLineConstants.MAC_DB_ADDRESS;
            }
            case TESTING -> {
                return StreamLineConstants.TESTING_DB_ADDRESS;
            }
            default -> {
                return StreamLineConstants.LINUX_DB_ADDRESS;
            }
        }
    }
}
