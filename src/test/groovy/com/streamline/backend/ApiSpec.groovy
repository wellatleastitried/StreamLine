package com.streamline.backend

import com.streamline.utilities.internal.Config
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.IgnoreIf
import spock.lang.Ignore

class ApiSpec extends Specification {
    @Shared
    InvidiousHandle handle
    
    @Shared
    Config config
    
    def setupSpec() {
        config = new Config()
        config.setHost(InvidiousHandle.getWorkingHostnameFromApiOrDocker())
        handle = InvidiousHandle.getInstance(config)
    }
    
    def "check handle is singleton"() {
        when:
        def testHandle = InvidiousHandle.getInstance(config)
        
        then:
        handle == testHandle
        handle.is(testHandle)
    }
    
    @IgnoreIf({ config.host == null })
    def "check stats from API"() {
        when:
        def response = handle.retrieveStats()
        
        then:
        response != null
        !response.isEmpty()
        
        and:
        println "API is reachable!"
        println "\nInvidious Stats: ${handle.retrieveStats().replace('},', '},\n')}\n"
    }
    
    def "check URL encode"() {
        expect:
        handle.urlEncodeString("Test String") == "Test+String"
        handle.urlEncodeString("") == ""
        handle.urlEncodeString(null) == null
    }
    
    @IgnoreIf({ config.host == null })
    def "check can get video ID"() {
        given:
        def searchTerm = "Give Cold"
        
        when:
        def searchResults = handle.retrieveSearchResults(searchTerm).get()
        
        then:
        searchResults != null
        !searchResults.isEmpty()
        
        and:
        println "\nRESULTING VIDEO IDs ARE:"
        println "\nNumber of results from searching \"$searchTerm\": ${searchResults.size()}"
        searchResults.each { result -> 
            println result.songVideoId
            assert result.songVideoId != null
            assert !result.songVideoId.isEmpty()
        }
        println "\n"
    }
    
    @IgnoreIf({ config.host == null })
    def "attempt video download and audio strip"() {
        given:
        def videoId = "dQw4w9WgXcQ" // Example video ID
        
        when:
        def result = handle.downloadVideo(videoId)
        
        then:
        result != null
        // Add more specific assertions based on your requirements
    }
    
    @Ignore("This test requires a running Docker instance")
    def "test docker container management"() {
        given:
        def dockerManager = new DockerManager()
        
        when:
        def isAlive = dockerManager.containerIsAlive()
        def canConnect = dockerManager.canConnectToContainer()
        
        then:
        isAlive != null
        canConnect != null
    }
    
    def cleanupSpec() {
        try {
            if (DockerManager.containerIsAlive() && DockerManager.canConnectToContainer()) {
                DockerManager.stopContainer()
            }
        } catch (InterruptedException e) {
            System.err.println("InterruptedException occurred while trying to stop docker instance.")
            e.printStackTrace()
        }
    }
} 
