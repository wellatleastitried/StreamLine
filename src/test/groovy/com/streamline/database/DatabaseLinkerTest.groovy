package com.streamline.database

import com.streamline.utilities.internal.OS
import com.streamline.utilities.internal.StreamLineConstants
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class DatabaseLinkerTest extends Specification {

    DatabaseLinker databaseLinker
    
    final String TEST_TABLE_CREATION_QUERY = 
        "CREATE TABLE IF NOT EXISTS test_table (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "name TEXT NOT NULL)"
    
    final String TEST_DB_PATH = StreamLineConstants.LINUX_TESTING_DB_ADDRESS
    
    @TempDir
    Path tempDir
    
    def setup() {
        File dbFile = new File(TEST_DB_PATH)
        if (dbFile.exists()) {
            dbFile.delete()
        }
        
        dbFile.getParentFile().mkdirs()
    }
    
    def cleanup() {
        if (databaseLinker != null) {
            databaseLinker.shutdown()
        }
        
        File dbFile = new File(TEST_DB_PATH)
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }
    
    def "database file is created and tables exist"() {
        when:
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        
        then:
        File dbFile = new File(databaseLinker.PATH)
        dbFile.exists()
        
        Connection connection = databaseLinker.getConnection()
        connection != null
        
        and:
        Statement statement = connection.createStatement()
        ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table'")
        rs.next()
    }
    
    def "database persists data between sessions"() {
        given:
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        
        when: "Insert test data in first session"
        Statement statement = databaseLinker.getConnection().createStatement()
        statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Persistence Test')")
        databaseLinker.shutdown()
        
        and: "Reopen database in new session"
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        
        then: "Database should exist and contain test data"
        databaseLinker.databaseExists
        
        and:
        Statement newStatement = databaseLinker.getConnection().createStatement()
        ResultSet rs = newStatement.executeQuery("SELECT name FROM test_table WHERE name='Persistence Test'")
        rs.next()
    }
    
    def "shutdown closes connection properly"() {
        given:
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        Connection connection = databaseLinker.getConnection()
        
        expect:
        !connection.isClosed()
        
        when:
        def result = databaseLinker.shutdown()
        
        then:
        result
        connection.isClosed()
        
        when: "Calling shutdown a second time"
        def secondResult = databaseLinker.shutdown()
        
        then: "Second shutdown still returns true"
        secondResult
    }
    
    def "isDatabaseSetupAtPath returns correct values"() {
        given:
        String nonExistentPath = "${tempDir.toString()}/nonexistent.db"
        File nonExistentFile = new File(nonExistentPath)
        if (nonExistentFile.exists()) {
            nonExistentFile.delete()
        }
        
        and:
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        
        expect:
        databaseLinker.isDatabaseSetupAtPath(databaseLinker.PATH)
        !databaseLinker.isDatabaseSetupAtPath(nonExistentPath)
    }
    
    def "database operations work as expected"() {
        given:
        databaseLinker = new DatabaseLinker(OS.TESTING, TEST_TABLE_CREATION_QUERY)
        Connection connection = databaseLinker.getConnection()
        Statement statement = connection.createStatement()

        when: "Insert test items"
        statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Test Item 1')")
        statement.executeUpdate("INSERT INTO test_table (name) VALUES ('Test Item 2')")
        ResultSet rs = statement.executeQuery("SELECT * FROM test_table ORDER BY id")
        
        then: "First item is retrieved correctly"
        rs.next()
        rs.getString("name") == "Test Item 1"
        
        and: "Second item is retrieved correctly"
        rs.next()
        rs.getString("name") == "Test Item 2"
        
        and: "Only two items exist"
        !rs.next()
    }
}
