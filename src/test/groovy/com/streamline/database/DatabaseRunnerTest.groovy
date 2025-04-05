package com.streamline.database

import com.streamline.audio.Song
import com.streamline.database.utils.QueryLoader
import com.streamline.utilities.RetrievedStorage
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class DatabaseRunnerTest extends Specification {

    Connection mockConnection
    HashMap<String, String> queryMap
    DatabaseLinker mockLinker
    DatabaseRunner databaseRunner
    Statement mockStatement
    PreparedStatement mockPreparedStatement
    ResultSet mockResultSet
    
    @TempDir
    Path tempDir
    
    def setup() {
        mockConnection = Mock(Connection)
        mockLinker = Mock(DatabaseLinker)
        mockStatement = Mock(Statement)
        mockPreparedStatement = Mock(PreparedStatement)
        mockResultSet = Mock(ResultSet)
        
        queryMap = QueryLoader.getMapOfQueries()
        mockConnection.createStatement() >> mockStatement
        mockStatement.executeQuery(_ as String) >> mockResultSet
        mockConnection.prepareStatement(_ as String) >> mockPreparedStatement
        mockPreparedStatement.executeQuery() >> mockResultSet
        
        databaseRunner = new DatabaseRunner(mockConnection, queryMap, mockLinker)
    }
    
    def "constructor sets autoCommit to false"() {
        given:
        def mockConnection = Mock(Connection)

        when:
        def myObject = new DatabaseRunner(mockConnection, queryMap, mockLinker)

        then:
        1 * mockConnection.setAutoCommit(false)
    }

    def "getLikedSongs returns correct data"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.getInt("id") >> 1
        mockResultSet.getString("title") >> "Test Song"
        mockResultSet.getString("artist") >> "Test Artist"
        mockResultSet.getString("url") >> "http://example.com"
        mockResultSet.getString("videoId") >> "abc123"
        
        when:
        def result = databaseRunner.getLikedSongs()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeQuery(queryMap.get("GET_LIKED_SONGS"))
        
        and:
        result.size() == 1
        def song = result.getSongFromIndex(1)
        song.songName == "Test Song"
        song.songArtist == "Test Artist"
        song.songLink == "http://example.com"
        song.songVideoId == "abc123"
        song.songLiked
    }
    
    def "getDownloadedSongs returns correct data"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.getInt("id") >> 2
        mockResultSet.getString("title") >> "Downloaded Song"
        mockResultSet.getString("artist") >> "Another Artist"
        mockResultSet.getString("url") >> "http://example.com/2"
        mockResultSet.getString("videoId") >> "def456"
        
        when:
        def result = databaseRunner.getDownloadedSongs()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeQuery(queryMap.get("GET_DOWNLOADED_SONGS"))
        
        and:
        result.size() == 1
        def song = result.getSongFromIndex(1)
        song.songName == "Downloaded Song"
        song.songArtist == "Another Artist"
    }
    
    def "getRecentlyPlayedSongs returns correct data"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.getInt("id") >> 3
        mockResultSet.getString("title") >> "Recent Song"
        mockResultSet.getString("artist") >> "Recent Artist"
        mockResultSet.getString("url") >> "http://example.com/3"
        mockResultSet.getString("videoId") >> "ghi789"
        
        when:
        def result = databaseRunner.getRecentlyPlayedSongs()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeQuery(queryMap.get("GET_RECENTLY_PLAYED_SONGS"))
        
        and:
        result.size() == 1
        def song = result.getSongFromIndex(1)
        song.songName == "Recent Song"
        song.songRecentlyPlayed
    }
    
    def "searchForSongName returns song when exactly one match is found"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.isBeforeFirst() >> true
        mockResultSet.last() >> true
        mockResultSet.getRow() >> 1
        mockResultSet.first() >> true
        mockResultSet.getInt("id") >> 4
        mockResultSet.getString("title") >> "Searchable Song"
        mockResultSet.getString("artist") >> "Search Artist"
        mockResultSet.getString("url") >> "http://example.com/4"
        mockResultSet.getString("videoId") >> "jkl012"
        
        when:
        def result = databaseRunner.searchForSongName("Searchable")
        
        then:
        1 * mockConnection.prepareStatement("SELECT * FROM Songs s WHERE s.title LIKE ?;")
        1 * mockPreparedStatement.setString(1, "%Searchable%")
        1 * mockPreparedStatement.setQueryTimeout(10)
        
        and:
        result != null
        result.songName == "Searchable Song"
        result.songArtist == "Search Artist"
    }
    
    def "searchForSongName returns null when multiple matches found"() {
        given:
        mockResultSet.next() >>> [true, true, false]
        
        when:
        def result = databaseRunner.searchForSongName("Common")
        
        then:
        result == null
    }
    
    def "getSongsFromPlaylist returns correct songs"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.getInt("id") >> 5
        mockResultSet.getString("title") >> "Playlist Song"
        mockResultSet.getString("artist") >> "Playlist Artist"
        mockResultSet.getString("url") >> "http://example.com/5"
        mockResultSet.getString("videoId") >> "mno345"
        
        when:
        def result = databaseRunner.getSongsFromPlaylist("My Playlist")
        
        then:
        1 * mockPreparedStatement.setString(1, "My Playlist")
        1 * mockPreparedStatement.executeQuery()
        
        and:
        result.size() == 1
        def song = result.getSongFromIndex(1)
        song.songName == "Playlist Song"
    }
    
    def "getExpiredCache returns correct data"() {
        given:
        mockResultSet.next() >>> [true, false]
        mockResultSet.getInt("id") >> 6
        mockResultSet.getString("title") >> "Expired Song"
        mockResultSet.getString("artist") >> "Expired Artist"
        mockResultSet.getString("url") >> "http://example.com/6"
        mockResultSet.getString("videoId") >> "pqr678"
        mockResultSet.getString("file_path") >> "/path/to/song.mp3"
        mockResultSet.getString("file_hash") >> "abcdef1234567890"
        
        when:
        def result = databaseRunner.getExpiredCache()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeQuery(queryMap.get("GET_EXPIRED_CACHE"))
        
        and:
        result.size() == 1
        def song = result.getSongFromIndex(1)
        song.songName == "Expired Song"
        song.downloadPath == "/path/to/song.mp3"
        song.fileHash == "abcdef1234567890"
    }
    
    def "clearExpiredCache executes correct statement"() {
        when:
        databaseRunner.clearExpiredCache()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeUpdate(queryMap.get("CLEAR_EXPIRED_CACHE"))
        1 * mockConnection.commit()
    }
    
    def "clearCachedSongs executes correct statement"() {
        when:
        databaseRunner.clearCachedSongs()
        
        then:
        1 * mockStatement.setQueryTimeout(30)
        1 * mockStatement.executeUpdate(queryMap.get("CLEAR_CACHE"))
        1 * mockConnection.commit()
    }
    
    def "likeSong handles existing song"() {
        given:
        def song = new Song(0, "Existing Song", "Artist", "http://example.com", "vid123")
        def spyRunner = Spy(databaseRunner)
        spyRunner.getSongId(*_) >> 7
        
        when:
        spyRunner.likeSong(song)
        
        then:
        1 * mockConnection.setAutoCommit(false)
        1 * spyRunner.insertSongIntoLikedTable(7)
        1 * mockConnection.commit()
        1 * mockConnection.setAutoCommit(true)
    }
    
    def "likeSong handles new song"() {
        given:
        def song = new Song(0, "New Song", "New Artist", "http://example.com", "vid456")
        def spyRunner = Spy(databaseRunner)
        spyRunner.getSongId(*_) >> -1
        spyRunner.insertSongIntoSongs(_ as Song) >> 8
        
        when:
        spyRunner.likeSong(song)
        
        then:
        1 * mockConnection.setAutoCommit(false)
        1 * spyRunner.insertSongIntoSongs(song)
        1 * spyRunner.insertSongIntoLikedTable(8)
        1 * mockConnection.commit()
        1 * mockConnection.setAutoCommit(true)
    }
    
    def "addToRecents adds song to recently played"() {
        given:
        def song = new Song(0, "Recent Song", "Recent Artist", "http://example.com", "vid789")
        def spyRunner = Spy(databaseRunner)
        spyRunner.getSongId(*_) >> 9
        
        when:
        spyRunner.addToRecents(song)
        
        then:
        1 * mockConnection.setAutoCommit(false)
        1 * spyRunner.insertSongIntoRecentlyPlayed(9)
        1 * mockConnection.commit()
        1 * mockConnection.setAutoCommit(true)
    }
    
    def "downloadSong downloads and stores in database"() {
        given:
        def song = new Song(0, "Download Song", "Download Artist", "http://example.com", "vid101112")
        def downloadedSong = new Song(10, "Download Song", "Download Artist", "http://example.com", "vid101112", 
                false, true, false, "/path/to/download.mp3", "hash123456")
        def spyRunner = Spy(databaseRunner)
        spyRunner.getSongId(*_) >> 10
        spyRunner.download(_ as Song) >> downloadedSong
        
        when:
        spyRunner.downloadSong(song)
        
        then:
        1 * mockConnection.setAutoCommit(false)
        1 * spyRunner.download(song)
        1 * spyRunner.insertSongIntoDownloadTable(10, "/path/to/download.mp3", "hash123456")
        1 * mockConnection.commit()
        1 * mockConnection.setAutoCommit(true)
    }
    
    def "generateHashFromFile produces expected hash"() {
        given:
        def tempFile = tempDir.resolve("test.mp3").toFile()
        tempFile.text = "test data for hashing"
        
        when:
        def result = databaseRunner.generateHashFromFile(tempFile.absolutePath)
        
        then:
        result != null
        result.length() == 64
    }
    
    def "insertSongIntoSongs adds song to database"() {
        given:
        def song = new Song(0, "Insert Test", "Insert Artist", "http://example.com", "vidInsert")
        mockPreparedStatement.getGeneratedKeys() >> mockResultSet
        mockResultSet.next() >> true
        mockResultSet.getInt(1) >> 11
        
        when:
        def result = databaseRunner.insertSongIntoSongs(song)
        
        then:
        1 * mockPreparedStatement.setString(1, "Insert Test")
        1 * mockPreparedStatement.setString(2, "Insert Artist")
        1 * mockPreparedStatement.setString(3, _ as String)
        1 * mockPreparedStatement.setString(4, _ as String)
        1 * mockPreparedStatement.executeUpdate()
        1 * mockConnection.commit()
        1 * mockPreparedStatement.getGeneratedKeys()
        
        and:
        result == 11
    }
    
    def "insertSongIntoLikedTable adds song to liked table"() {
        when:
        databaseRunner.insertSongIntoLikedTable(12)
        
        then:
        1 * mockPreparedStatement.setInt(1, 12)
        1 * mockPreparedStatement.executeUpdate()
        1 * mockConnection.commit()
    }
    
    def "insertSongIntoDownloadTable adds song to download table"() {
        when:
        databaseRunner.insertSongIntoDownloadTable(13, "/path/file.mp3", "filehash123")
        
        then:
        1 * mockPreparedStatement.setInt(1, 13)
        1 * mockPreparedStatement.setString(2, "/path/file.mp3")
        1 * mockPreparedStatement.setString(3, "filehash123")
        1 * mockPreparedStatement.executeUpdate()
        1 * mockConnection.commit()
    }
    
    def "insertSongIntoRecentlyPlayed adds song to recently played"() {
        when:
        databaseRunner.insertSongIntoRecentlyPlayed(14)
        
        then:
        1 * mockPreparedStatement.setInt(1, 14)
        1 * mockPreparedStatement.executeUpdate()
        1 * mockConnection.commit()
    }
    
    def "shutdown calls linker shutdown"() {
        when:
        databaseRunner.shutdown()
        
        then:
        1 * mockLinker.shutdown()
    }
    
    def "handleSQLException performs rollback"() {
        given:
        mockStatement.executeQuery(_ as String) >> { String query -> throw new SQLException("Test exception") }
        
        when:
        databaseRunner.getLikedSongs()
        
        then:
        1 * mockConnection.rollback()
    }
}
