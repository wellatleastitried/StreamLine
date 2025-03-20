package com.streamline.database;

import com.streamline.utilities.internal.StreamLineConstants;
import com.streamline.utilities.internal.StreamLineMessages;
import com.streamline.utilities.internal.OS;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.tinylog.Logger;

/**
 * Handles the creation of the database and generates a connection.
 * @author wellatleastitried
 */
public final class DatabaseLinker {

    protected final OS osName;
    protected final String PATH;
    private Connection connection;
    protected final boolean databaseExists;

    public DatabaseLinker(OS osName, String tableCreationQuery) {
        this.osName = osName;
        this.PATH = setupPath(this.osName);
        new File(PATH).getParentFile().mkdirs();
        this.databaseExists = isDatabaseSetupAtPath(PATH);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            if (!this.databaseExists) {
                if (setupNewDatabase(tableCreationQuery)) {
                    System.out.println("[*] Database has been successfully set up.");
                } else {
                    Logger.error(StreamLineMessages.DBCreationFailure.getMessage());
                    System.exit(0);
                }
            }
        } catch (SQLException sE) {
            System.err.println(StreamLineMessages.GetDBConnectionFailure.getMessage());
            System.exit(1);
        }
    }

    /**
     * Allows com.streamline.Interact.DatabaseRunner to use the existing connection so that it does not have to create its own.
     * @return The established connection to the database.
     */
    public Connection getConnection() {
        return this.connection;
    }

    private boolean setupNewDatabase(String query) {
        try {
            final Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(query);
        } catch (SQLException sE) {
            Logger.error(StreamLineMessages.DBCreationFailure.getMessage());
            System.exit(1);
        } catch (Exception e) {
            Logger.error(StreamLineMessages.UnknownDBFatalError.getMessage());
            System.exit(1);
        }
        return true;
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
        return (new File(path).exists());
    }

    private String setupPath(OS os) {
        if (os == OS.WINDOWS) {
            return StreamLineConstants.WINDOWS_DB_ADDRESS;          
        } else if (os == OS.MAC) {
            return StreamLineConstants.MAC_DB_ADDRESS;
        } else if (os == OS.TESTING) {
            return StreamLineConstants.LINUX_TESTING_DB_ADDRESS;
        }
        return StreamLineConstants.LINUX_DB_ADDRESS;
    }
}
