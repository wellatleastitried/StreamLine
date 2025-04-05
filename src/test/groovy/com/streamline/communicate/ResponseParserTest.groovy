package com.streamline.communicate

import com.streamline.audio.Song
import com.streamline.communicate.jsonobjects.VideoSearchResult
import spock.lang.Specification

class ResponseParserTest extends Specification {

    VideoSearchResult mockVideoSearchResult = Mock()

    def "isValidJson validates proper JSON"() {
        given:
        def validJson = '[{"type":"video","title":"Test Video","videoId":"12345"}]'

        when:
        def result = ResponseParser.isValidJson(validJson)

        then:
        result == true
    }

    def "isValidJson rejects invalid JSON"() {
        given:
        def invalidJson = "This is not JSON"

        when:
        def result = ResponseParser.isValidJson(invalidJson)

        then:
        result == false
    }

    def "isValidJson rejects incomplete JSON"() {
        given:
        def incompleteJson = '{"type":"video","title":"Test Video"'

        when:
        def result = ResponseParser.isValidJson(incompleteJson)

        then:
        result == false
    }

    def "searchResultToSong properly converts search result to song"() {
        given:
        mockVideoSearchResult.getTitle() >> "Test Song"
        mockVideoSearchResult.getAuthor() >> "Test Artist"
        mockVideoSearchResult.getVideoId() >> "abc123"

        when:
        def result = ResponseParser.searchResultToSong(mockVideoSearchResult)

        then:
        result != null
        result.songName == "Test Song" 
        result.songArtist == "Test Artist"
        result.songVideoId == "abc123"
        result.songId == -1
        result.songLink == null
    }

    def "listFromInvidiousSearchResponse processes valid response"() {
        given:
        def validResponse = '[{"type":"video","title":"Test Song","author":"Test Artist","videoId":"abc123","lengthSeconds":180}]'

        when:
        def result = ResponseParser.listFromInvidiousSearchResponse(validResponse)

        then:
        result != null
        result.size() == 1
        result[0].songName == "Test Song"
        result[0].songArtist == "Test Artist"
        result[0].songVideoId == "abc123"
    }

    def "listFromInvidiousSearchResponse returns null for invalid JSON"() {
        given:
        def invalidJson = "This is not JSON"

        when:
        def result = ResponseParser.listFromInvidiousSearchResponse(invalidJson)

        then:
        result == null
    }

    def "listFromInvidiousSearchResponse handles empty array"() {
        given:
        def emptyArray = "[]"

        when:
        def result = ResponseParser.listFromInvidiousSearchResponse(emptyArray)

        then:
        result != null
        result.isEmpty()
    }

    def "listFromInvidiousSearchResponse filters non-video types"() {
        given:
        def mixedResponse = '''[
            {"type":"video","title":"Test Song","author":"Test Artist","videoId":"abc123","lengthSeconds":180},
            {"type":"playlist","title":"Test Playlist"}
        ]'''

        when:
        def result = ResponseParser.listFromInvidiousSearchResponse(mixedResponse)

        then:
        result != null
        result.size() == 1
        result[0].songName == "Test Song"
    }

    def "urlFromInvidividualVideoResponse returns null for invalid JSON"() {
        given:
        def invalidJson = "This is not JSON"

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(invalidJson)

        then:
        result == null
    }

    def "urlFromInvidividualVideoResponse selects high quality mp4 audio"() {
        given:
        def validResponse = '''{
            "adaptiveFormats":[
                {"url":"https://example.com/video.mp4","type":"video/mp4;codecs=avc1.4d401e","audioQuality":"MEDIUM"},
                {"url":"https://example.com/audio-high.mp4","type":"audio/mp4;codecs=mp4a.40.2","audioQuality":"HIGH"},
                {"url":"https://example.com/audio-low.webm","type":"audio/webm;codecs=opus","audioQuality":"LOW"}
            ]
        }'''

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(validResponse)

        then:
        result == "https://example.com/audio-high.mp4"
    }

    def "urlFromInvidividualVideoResponse prefers mp4 format"() {
        given:
        def validResponse = '''{
            "adaptiveFormats":[
                {"url":"https://example.com/video.mp4","type":"video/mp4;codecs=avc1.4d401e","audioQuality":"MEDIUM"},
                {"url":"https://example.com/audio-medium.mp4","type":"audio/mp4;codecs=mp4a.40.2","audioQuality":"MEDIUM"},
                {"url":"https://example.com/audio-high.webm","type":"audio/webm;codecs=opus","audioQuality":"HIGH"}
            ]
        }'''

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(validResponse)

        then:
        result == "https://example.com/audio-medium.mp4"
    }

    def "urlFromInvidividualVideoResponse returns null when no audio formats available"() {
        given:
        def validResponse = '''{
            "adaptiveFormats":[
                {"url":"https://example.com/video1.mp4","type":"video/mp4;codecs=avc1.4d401e","audioQuality":"MEDIUM"},
                {"url":"https://example.com/video2.mp4","type":"video/mp4;codecs=avc1.4d401e","audioQuality":"HIGH"}
            ]
        }'''

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(validResponse)

        then:
        result == null
    }

    def "urlFromInvidividualVideoResponse returns null for empty adaptiveFormats"() {
        given:
        def validResponse = '{"adaptiveFormats":[]}'

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(validResponse)

        then:
        result == null
    }

    def "urlFromInvidividualVideoResponse falls back to low quality when needed"() {
        given:
        def validResponse = '''{
            "adaptiveFormats":[
                {"url":"https://example.com/video.mp4","type":"video/mp4;codecs=avc1.4d401e","audioQuality":"MEDIUM"},
                {"url":"https://example.com/audio-low.mp4","type":"audio/mp4;codecs=mp4a.40.2","audioQuality":"LOW"}
            ]
        }'''

        when:
        def result = ResponseParser.urlFromInvidividualVideoResponse(validResponse)

        then:
        result == "https://example.com/audio-low.mp4"
    }
}
