package com.walit.streamline.backend;

import com.walit.streamline.audio.Song;
import com.walit.streamline.utilities.internal.Config;
import com.walit.streamline.utilities.internal.OS;
import com.walit.streamline.utilities.internal.StreamLineConstants;
import com.walit.streamline.utilities.internal.StreamLineMessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

public final class YoutubeHandle implements ConnectionHandle {

    public static YoutubeHandle instance;

    private final Config config;

    private final Map<String, List<Song>> searchCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();

    public YoutubeHandle(Config config) {
        this.config = config;
    }

    public static YoutubeHandle getInstance(Config config) {
        if (instance == null) {
            instance = new YoutubeHandle(config);
        }
        return instance;
    }

    @Override
    public CompletableFuture<List<Song>> retrieveSearchResults(String term) {
        cleanupCache();
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = term.toLowerCase().trim();
            Long cacheTime = cacheTimestamps.get(cacheKey);
            if (cacheTime != null && System.currentTimeMillis() - cacheTime < StreamLineConstants.YOUTUBE_CACHE_EXPIRY_MS) {
                List<Song> cachedResults = searchCache.get(cacheKey);
                if (cachedResults != null && !cachedResults.isEmpty()) {
                    Logger.debug("Cache hit for search term: {}", term);
                    return new ArrayList<>(cachedResults);
                }
            }
            Logger.debug("Cache miss for search term: {}", term);

            List<Song> results = new ArrayList<>();

            String sanitizedTerm = term.replace("'", "'\\''");
            String[] command = {
                config.getBinaryPath(),
                "--no-warnings",
                "--ignore-errors",
                "--no-playlist",
                "--flat-playlist",
                "--socket-timeout", "5", // Remove if necessary
                "ytsearch10:'" + sanitizedTerm + "'",
                "--print",
                "%(title)s | %(uploader)s | %(duration>%M:%S)s | %(id)s"
            };

            Process process = null;
            try {
                process = Dispatcher.runCommandExpectWait(command);

                if (process == null) {
                    Logger.warn("Process was null when searching for: {}, term");
                    return results;
                }

                final Process finalProcess = process;
                CompletableFuture.runAsync(() -> {
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(finalProcess.getErrorStream()))) {
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            Logger.debug("yt-dlp error: {}", line);
                        }
                    } catch (IOException iE) {
                        Logger.debug("Error reading stderr: {}", iE.getMessage());
                    }
                });

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(" \\| ");
                    if (fields.length == 4) {
                        String title = fields[0];
                        String author = fields[1];
                        String duration = fields[2];
                        String videoId = fields[3];
                        if (acceptableDuration(duration)) {
                            results.add(new Song(-1, title, author, null, duration, videoId));
                        }
                    }
                }

                if (!process.waitFor(10, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    Logger.warn("yt-dlp search timed out after 10 seconds for term: {}", term);
                }

                if (!results.isEmpty()) {
                    searchCache.put(cacheKey, new ArrayList<>(results));
                    cacheTimestamps.put(cacheKey, System.currentTimeMillis());
                    Logger.debug("Cached {} results for term: {}", results.size(), term);
                }
            } catch (IOException | InterruptedException e) {
                Logger.warn(StreamLineMessages.UnableToPullSearchResultsFromYtDlp.getMessage());
                return null;
            } finally {
                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }
            }
            return results;
        });
    }

    private boolean acceptableDuration(String duration) { // Passed as M:S
        if (!duration.contains(":")) {
            return false;
        }
        String[] partsOfTime = duration.split(":");
        if (!(Integer.parseInt(partsOfTime[0]) < 15)) {
            return false;
        }
        return true;
    }

    @Override
    public String getAudioUrlFromVideoId(String id) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] command = {
            config.getBinaryPath(),
            "--geo-bypass",
            "--no-warnings",
            "--ignore-errors",
            "-f",
            "ba",
            "--get-url",
            "https://www.youtube.com/watch?v=" + id
        };
        Process process = null;
        try {
            process = Dispatcher.runCommandExpectWait(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            process.waitFor();
        } catch (InterruptedException | IOException iE) {
            return null;
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        return stringBuilder.toString().trim();
    }

    public void cleanupCache() {
        long currentTime = System.currentTimeMillis();
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Long> entry : cacheTimestamps.entrySet()) {
            if (currentTime - entry.getValue() > StreamLineConstants.YOUTUBE_CACHE_EXPIRY_MS) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (String key : keysToRemove) {
            searchCache.remove(key);
            cacheTimestamps.remove(key);
        }
        Logger.debug("[!] Cache cleanup remove {} expired entries", keysToRemove.size());
    }

    public static boolean setupYoutubeInterop(Config config) {
        if (!isYtDlpDownloaded(config)) {
            return downloadYtDlp(config);
        }
        return true;
    }

    private static void createMissingDirectories(String stringPath) {
        File path = new File(stringPath);
        if (!path.getParentFile().mkdirs()) {
            System.out.println("[!] Could not create the necessary directories for binary executable's path.");
            return;
        }
        System.out.println("[*] Path for binary has been set.");
    }

    private static boolean downloadYtDlp(Config config) {
        String ytDlpUrl;
        String ytDlpTargetLocation;
        if (config.getOS() == OS.MAC) {
            ytDlpUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp";
            ytDlpTargetLocation = StreamLineConstants.YT_DLP_BIN_LOCATION_MAC + "yt-dlp";
        } else if (config.getOS() == OS.WINDOWS) {
            ytDlpUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";
            ytDlpTargetLocation = StreamLineConstants.YT_DLP_BIN_LOCATION_WINDOWS + "yt-dlp.exe";
        } else {
            ytDlpUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp";
            ytDlpTargetLocation = StreamLineConstants.YT_DLP_BIN_LOCATION_LINUX + "yt-dlp";
        }
        createMissingDirectories(ytDlpTargetLocation);
        String[] command = {"curl", "-fL", ytDlpUrl, "-o", ytDlpTargetLocation};
        Process process = null;
        try {
            process = Dispatcher.runCommandExpectWait(command);
            if (!process.waitFor(60, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                Logger.error("[!] Download of yt-dlp timed out after 60 seconds.");
                return false;
            }
            if (process.exitValue() != 0) {
                System.out.println("[!] Error: curl command failed with exit code " + process.exitValue());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                return false;
            }
            if (config.getOS() == OS.LINUX || config.getOS() == OS.MAC) {
                Path path = Paths.get(ytDlpTargetLocation);
                if (Files.exists(path)) {
                    Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
                    System.out.println("[*] Binary has been marked as executable.");
                } else {
                    System.out.println("[!] yt-dlp was not downloaded.");
                    return false;
                }
            }
            return true;
        } catch (InterruptedException | IOException iE) {
            Logger.error(StreamLineMessages.YtDlpDownloadFailed.getMessage());
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        return false;
    }

    private static boolean isYtDlpDownloaded(Config config) {
        File binary;
        if (config.getOS() == OS.MAC) {
            binary = new File(StreamLineConstants.YT_DLP_BIN_LOCATION_MAC + "yt-dlp");
        } else if (config.getOS() == OS.WINDOWS) {
            binary = new File(StreamLineConstants.YT_DLP_BIN_LOCATION_WINDOWS + "yt-dlp.exe");
        } else {
            binary = new File(StreamLineConstants.YT_DLP_BIN_LOCATION_LINUX + "yt-dlp");
        }
        
        return binary.exists();
    }

    public static void clean(Config config) {
    }
}
