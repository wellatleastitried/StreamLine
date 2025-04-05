import com.streamline.audio.Song;
import com.streamline.communicate.ResponseParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonProcessingDemo {

    private static final String SEARCH_RESPONSE_PATH = "sample-search-response.json";
    private static final String VIDEO_RESPONSE_PATH = "sample-video-response.json";
    
    public static void main(String[] args) {
        System.out.println("Starting JSON Processing Demo");
        System.out.println("This example demonstrates how the Streamline application processes JSON from the Invidious API");
        System.out.println();
        
        try {
            // Load JSON content from files
            String searchResponseJson = loadJsonFromFile(SEARCH_RESPONSE_PATH);
            String videoResponseJson = loadJsonFromFile(VIDEO_RESPONSE_PATH);
            
            // Demo the search response processing
            demoSearchResponseProcessing(searchResponseJson);
            
            // Demo the individual video URL extraction
            demoVideoUrlExtraction(videoResponseJson);
            
            // Demo the complete flow with both processing steps
            demoCompleteFlow(searchResponseJson, videoResponseJson);
            
        } catch (IOException e) {
            System.err.println("Error reading JSON files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads JSON content from a file.
     * 
     * @param filePath Path to the JSON file
     * @return String containing the JSON content
     * @throws IOException If there's an error reading the file
     */
    private static String loadJsonFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    /**
     * Demonstrates processing a search response from the Invidious API.
     * 
     * @param searchResponseJson JSON string containing search results
     */
    private static void demoSearchResponseProcessing(String searchResponseJson) {
        System.out.println("====== DEMO: SEARCH RESPONSE PROCESSING ======");
        
        // Validate JSON
        boolean isValid = ResponseParser.isValidJson(searchResponseJson);
        System.out.println("Is search JSON valid? " + isValid);
        
        if (isValid) {
            // Process JSON into List<Song>
            List<Song> songs = ResponseParser.listFromInvidiousSearchResponse(searchResponseJson);
            
            // Display results
            System.out.println("Found " + songs.size() + " songs:");
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                System.out.println((i + 1) + ". Title: " + song.getSongName());
                System.out.println("   Artist: " + song.getSongArtist());
                System.out.println("   Video ID: " + song.getSongVideoId());
                System.out.println();
            }
        }
        
        System.out.println("============================================");
    }
    
    /**
     * Demonstrates extracting the best audio URL from an individual video response.
     * 
     * @param videoResponseJson JSON string containing video details
     */
    private static void demoVideoUrlExtraction(String videoResponseJson) {
        System.out.println("====== DEMO: VIDEO URL EXTRACTION ======");
        
        // Validate JSON
        boolean isValid = ResponseParser.isValidJson(videoResponseJson);
        System.out.println("Is video JSON valid? " + isValid);
        
        if (isValid) {
            // Extract best URL
            String bestUrl = ResponseParser.urlFromInvidividualVideoResponse(videoResponseJson);
            System.out.println("Best audio URL: " + bestUrl);
        }
        
        System.out.println("========================================");
    }
    
    /**
     * Demonstrates the complete flow of processing a search response and then
     * updating each Song with its audio URL.
     * 
     * @param searchResponseJson JSON string containing search results
     * @param videoResponseJson JSON string containing video details
     */
    private static void demoCompleteFlow(String searchResponseJson, String videoResponseJson) {
        System.out.println("====== DEMO: COMPLETE FLOW ======");
        
        // First get list of songs from search
        List<Song> songs = ResponseParser.listFromInvidiousSearchResponse(searchResponseJson);
        
        if (songs != null && !songs.isEmpty()) {
            System.out.println("Processing " + songs.size() + " songs to retrieve audio URLs...");
            
            // In a real application, you would make an API call for each video ID
            // Here we'll simulate it with our sample video response
            for (Song song : songs) {
                // In real code, this would be a new API call using the video ID
                String url = ResponseParser.urlFromInvidividualVideoResponse(videoResponseJson);
                song.setSongLink(url);
                
                System.out.println("Updated song: " + song.getSongName());
                System.out.println("  Audio URL: " + song.getSongLink());
                System.out.println();
            }
        }
        
        System.out.println("================================");
    }
}
