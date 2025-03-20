package com.streamline.database;

import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseLinkerTest {

    private DatabaseLinker databaseLinker;

    private final String TEST_TABLE_CREATION_QUERY = 
        "CREATE TABLE IF NOT EXISTS test_table (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "name TEXT NOT NULL)";

    private final String TEST_DB_PATH = StreamLineConstants.LINUX_TESTING_DB_ADDRESS;

    @BeforeEach
    public void setUp() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        dbFile.getParentFile().mkdirs();
    }

    @AfterEach
    public void tearDown() {
        if (databaseLinker != null) {
            databaseLinker.shutdown();
        }

        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testDatabaseCreation() {
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);

        File dbFile = new File(databaseLinker.PATH);
        assertTrue(dbFile.exists(), "Database file should be created");

        Connection connection = databaseLinker.getConnection();
        assertNotNull(connection, "Connection should not be null");

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table'");
            assertTrue(rs.next(), "Test table should exist in the database");
        } catch (SQLException e) {
            fail("Failed to query database: " + e.getMessage());
        }
    }

    @Test
    public void testDatabaseReuse() {
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);

        try {
            Statement statement = databaseLinker.getConnection().createStatement();
            statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Persistence Test')");
        } catch (SQLException e) {
            fail("Failed to insert test data: " + e.getMessage());
        }

        databaseLinker.shutdown();

        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);

        assertTrue(databaseLinker.databaseExists, "Should detect existing database");

        try {
            Statement statement = databaseLinker.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT name FROM test_table WHERE name='Persistence Test'");
            assertTrue(rs.next(), "Test data should persist in the reused database");
        } catch (SQLException e) {
            fail("Failed to query database: " + e.getMessage());
        }
    }

    @Test
    public void testShutdown() {
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);
        Connection connection = databaseLinker.getConnection();

        try {
            assertFalse(connection.isClosed(), "Connection should be open initially");
        } catch (SQLException e) {
            fail("Failed to check connection state: " + e.getMessage());
        }

        boolean result = databaseLinker.shutdown();
        assertTrue(result, "Shutdown should return true on success");

        try {
            assertTrue(connection.isClosed(), "Connection should be closed after shutdown");
        } catch (SQLException e) {
            fail("Failed to check connection state: " + e.getMessage());
        }

        result = databaseLinker.shutdown();
        assertTrue(result, "Second shutdown should still return true");
    }

    @Test
    public void testIsDatabaseSetupAtPath() {
        String nonExistentPath = "/tmp/StreamLine/nonexistent.db";
        File nonExistentFile = new File(nonExistentPath);
        if (nonExistentFile.exists()) {
            nonExistentFile.delete(); // Make sure it actually doesn't exist
        }

        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);

        assertTrue(databaseLinker.isDatabaseSetupAtPath(databaseLinker.PATH), 
                "Should return true for existing database at " + databaseLinker.PATH);

        assertFalse(databaseLinker.isDatabaseSetupAtPath(nonExistentPath), 
                "Should return false for non-existent database");
    }

    @Test
    public void testDatabaseOperations() {
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY);
        Connection connection = databaseLinker.getConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Test Item 1')");
            statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Test Item 2')");

            ResultSet rs = statement.executeQuery("SELECT * FROM test_table ORDER BY id");

            assertTrue(rs.next(), "Should find first inserted item");
            assertEquals("Test Item 1", rs.getString("name"), "First item should match inserted data");

            assertTrue(rs.next(), "Should find second inserted item");
            assertEquals("Test Item 2", rs.getString("name"), "Second item should match inserted data");

            assertFalse(rs.next(), "Should only have two items in the database");

        } catch (SQLException e) {
            fail("Database operations failed: " + e.getMessage());
        }
    }
}
