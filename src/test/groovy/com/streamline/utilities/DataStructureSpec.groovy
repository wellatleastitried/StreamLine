package com.streamline.utilities

import com.streamline.audio.Song
import spock.lang.Specification
import spock.lang.Unroll

class DataStructureSpec extends Specification {
    private final RetrievedStorage structure = new RetrievedStorage()
    
    def setup() {
        for (int i = 0; i < 20; i++) {
            structure.add(i, new Song(i + 1, "title", "artist", "url", String.valueOf(i)))
        }
        assert structure.size() == 20
    }
    
    def "remove elements from structure"() {
        when:
        structure.remove(5)
        
        then:
        structure.size() == 19
        structure.getSongFromIndex(5) == null
    }
    
    def "get song from index"() {
        when:
        def song = structure.getSongFromIndex(10)
        
        then:
        song != null
        song.songName == "title"
        song.songArtist == "artist"
        song.songVideoId == "10"
    }
    
    def "get index from song"() {
        given:
        def song = structure.getSongFromIndex(15)
        
        when:
        def index = structure.getIndexFromSong(song)
        
        then:
        index == 15
    }
    
    @Unroll
    def "test structure operations with #count elements"() {
        given:
        def testStructure = new RetrievedStorage()
        
        when:
        for (int i = 0; i < count; i++) {
            testStructure.add(i, new Song(i + 1, "title$i", "artist$i", "url$i", String.valueOf(i)))
        }
        
        then:
        testStructure.size() == count
        
        where:
        count << [0, 1, 5, 10, 20]
    }
    
    def "clear structure"() {
        when:
        structure.clear()
        
        then:
        structure.size() == 0
        structure.getSongFromIndex(0) == null
    }
    
    def cleanup() {
        structure.clear()
        assert structure.size() == 0
    }
} 