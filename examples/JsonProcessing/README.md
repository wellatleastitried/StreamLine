# JSON Processing Example

This example demonstrates how the Streamline application processes JSON responses from the Invidious API into Song objects.

## Prerequisites

- JDK 8 or higher
- The packaged streamline.jar in the dist/ directory
- JSON files in the same directory as the Java class file

## Compiling the Example

To compile the example while referencing the main application classes, you'll use the packaged JAR file.

### On Linux/macOS:

```bash
# Navigate to the example directory
cd examples/JsonProcessingDemo

# Compile the example using the JAR file from the main project's dist/
javac -cp ../../dist/streamline.jar JsonProcessingDemo.java
```

### On Windows:

```batch
REM Navigate to the example directory
cd examples\JsonProcessingDemo

REM Compile the example using the JAR file from the main project's dist/
javac -cp ..\..\dist\streamline.jar JsonProcessingDemo.java
```

## Running the Example

After compilation, run the example with the JAR file in the classpath:

### On Linux/macOS:

```bash
java -cp .:../../dist/streamline.jar JsonProcessingDemo
```

### On Windows:

```batch
java -cp .:../../dist/streamline.jar JsonProcessingDemo
```

## Example Output

When you run the example, you should see output similar to:

```
Starting JSON Processing Demo
This example demonstrates how the Streamline application processes JSON from the Invidious API

====== DEMO: SEARCH RESPONSE PROCESSING ======
Is search JSON valid? true
Found 3 songs:
1. Title: Bohemian Rhapsody
   Artist: Queen
   Video ID: fJ9rUzIMcZQ

2. Title: Hotel California
   Artist: Eagles
   Video ID: EqPtz5qN7HM

3. Title: Stairway to Heaven
   Artist: Led Zeppelin
   Video ID: QkF3oxziUI4

============================================
====== DEMO: VIDEO URL EXTRACTION ======
Is video JSON valid? true
Best audio URL: https://example.com/audio/bohemian-rhapsody-high.mp4
========================================
====== DEMO: COMPLETE FLOW ======
Processing 3 songs to retrieve audio URLs...
Updated song: Bohemian Rhapsody
  Audio URL: https://example.com/audio/bohemian-rhapsody-high.mp4

Updated song: Hotel California
  Audio URL: https://example.com/audio/bohemian-rhapsody-high.mp4

Updated song: Stairway to Heaven
  Audio URL: https://example.com/audio/bohemian-rhapsody-high.mp4

================================
```

## How It Works

1. The example loads JSON responses from files in the same directory
2. It uses the `ResponseParser` class from the main application to:
   - Validate the JSON
   - Parse the search results into a list of `Song` objects
   - Extract the best audio URL from the video details
3. The conversion process maps:
   - `VideoSearchResult.title` → `Song.title`
   - `VideoSearchResult.author` → `Song.artist`
   - `VideoSearchResult.videoId` → `Song.videoId`

## Troubleshooting

If you encounter a `FileNotFoundException` or issues loading the JSON files:
1. Make sure the JSON files are in the same directory where you run the application
2. Check that the file names match exactly: `sample-search-response.json` and `sample-video-response.json`

If you encounter a `ClassNotFoundException` or `NoClassDefFoundError`, check that:
1. The streamline.jar file exists in the ../../dist/ directory
2. The classpath includes both the build directory and the JAR file
3. The JAR file contains all the required classes (you can check using `jar tf ../../dist/streamline.jar`)
