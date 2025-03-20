package com.streamline.backend;

import com.streamline.audio.Song;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;
import com.streamline.utilities.internal.StreamLineMessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.ArrayList;
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

    private List<Song> getFromCache(String term) {
        String cacheKey = term.toLowerCase().trim();
        Long cacheTime = cacheTimestamps.get(cacheKey);
        if (cacheTime != null && System.currentTimeMillis() - cacheTime < StreamLineConstants.YOUTUBE_CACHE_EXPIRY_MS) {
            List<Song> cachedResults = searchCache.get(cacheKey);
            if (cachedResults != null && !cachedResults.isEmpty()) {
                Logger.debug("[*] Cache hit for search term: {}", term);
                return new ArrayList<>(cachedResults);
            }
        }
        Logger.debug("[*] Cache miss for search term: {}", term);
        return null;
    }

    private List<Song> processCommandOutput(Process process) throws IOException, InterruptedException {
        List<Song> results = new ArrayList<>();
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
            Logger.warn("[!] yt-dlp search timed out after 10 seconds");
        }
        return results;
    }

    private void saveToCache(String term, List<Song> results) {
        String cacheKey = term.toLowerCase().trim();
        if (!results.isEmpty()) {
            searchCache.put(cacheKey, new ArrayList<>(results));
            cacheTimestamps.put(cacheKey, System.currentTimeMillis());
            Logger.debug("[*] Cached {} results for term: {}", results.size(), term);
        }
    }

    @Override
    public CompletableFuture<List<Song>> retrieveSearchResults(String term) {
        return CompletableFuture.supplyAsync(() -> {
            cleanupCache();
            List<Song> cachedResults = getFromCache(term);
            if (cachedResults != null) {
                return cachedResults;
            }

            List<Song> results = new ArrayList<>();
            String[] command = buildSearchCommand(term);
            Process process = null;
            try {
                process = CommandExecutor.runCommandExpectWait(command);
                if (process == null) {
                    Logger.warn("[!] Process was null when searching for: {}, term");
                    return results;
                }
                handleErrorStream(process);
                results = processCommandOutput(process);
                saveToCache(term, results);
            } catch (IOException | InterruptedException e) {
                Logger.warn(StreamLineMessages.UnableToPullSearchResultsFromYtDlp.getMessage());
                return null;
            } finally {
                cleanupProcess(process);
            }
            return results;
        });
    }

    private String[] buildSearchCommand(String term) {
        String sanitizedTerm = term.replace("'", "'\\''");
        return new String[] {
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
    }

    private void handleErrorStream(Process process) {
        final Process finalProcess = process;
        CompletableFuture.runAsync(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(finalProcess.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    Logger.debug("[*] yt-dlp error: {}", line);
                }
            } catch (IOException iE) {
                Logger.debug("[!] Error reading stderr: {}", iE.getMessage());
            }
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
            process = CommandExecutor.runCommandExpectWait(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            process.waitFor();
        } catch (InterruptedException | IOException iE) {
            return null;
        } finally {
            cleanupProcess(process);
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

    private static boolean createMissingDirectories(String stringPath) {
        File path = new File(stringPath);
        if (!path.getParentFile().mkdirs()) {
            System.out.println("[!] Could not create the necessary directories for binary executable's path.");
            return false;
        }
        System.out.println("[*] Path for binary has been set.");
        return true;
    }

    private static Process downloadBinaryUsingCurl(YtDlpBinaryInfo binaryInfo) throws IOException {
        String[] command = {"curl", "-fL", binaryInfo.url, "-o", binaryInfo.targetLocation};
        return CommandExecutor.runCommandExpectWait(command);
    }

    private static void printErrorStream(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static boolean makeBinaryExecutable(String targetLocation) {
        Path path = Paths.get(targetLocation);
        if (Files.exists(path)) {
            try {
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
                System.out.println("[*] Binary has been marked as executable.");
                return true;
            } catch (IOException iE) {
                Logger.error("[!] Failed to set executable permission on yt-dlp: {}", iE.getMessage());
                return false;
            }
        } else {
            System.out.println("[!] yt-dlp was not downloaded.");
            return false;
        }
    }

    private static boolean wasDownloadSuccessful(Process process) throws InterruptedException, IOException  {
        if (!process.waitFor(60, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            Logger.error("[!] Download of yt-dlp timed out after 60 seconds.");
            return false;
        }
        if (process.exitValue() != 0) {
            System.out.println("[!] Error: curl command failed with exit code " + process.exitValue());
            printErrorStream(process);
            return false;
        }
        return true;
    }

    private static boolean downloadYtDlp(Config config) {
        YtDlpBinaryInfo binaryInfo = getYtDlpBinaryInfo(config);
        if (!createMissingDirectories(binaryInfo.targetLocation)) {
            return false;
        }
        Process process = null;
        try {
            process = downloadBinaryUsingCurl(binaryInfo);
            if (!wasDownloadSuccessful(process)) {
                return false;
            }
            if (isUnixSystem(config.getOS())) {
                if (!makeBinaryExecutable(binaryInfo.targetLocation)) {
                    return false;
                }
            }
            return true;
        } catch (InterruptedException | IOException iE) {
            Logger.error(StreamLineMessages.YtDlpDownloadFailed.getMessage());
        } finally {
            cleanupProcess(process);
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

    // Remove yt-dlp binary
    public static void clean(Config config) {
    }

    private static boolean isUnixSystem(OS os) {
        return os == OS.LINUX || os == OS.MAC;
    }

    private static void cleanupProcess(Process process) {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
    }

    private static class YtDlpBinaryInfo {
        final String url;
        final String targetLocation;

        YtDlpBinaryInfo(String url, String targetLocation) {
            this.url = url;
            this.targetLocation = targetLocation;
        }
    }

    private static YtDlpBinaryInfo getYtDlpBinaryInfo(Config config) {
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
        return new YtDlpBinaryInfo(ytDlpUrl, ytDlpTargetLocation);
    }
}
