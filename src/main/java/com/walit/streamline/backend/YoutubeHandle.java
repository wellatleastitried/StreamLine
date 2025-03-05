package com.walit.streamline.backend;

import com.walit.streamline.audio.Song;
import com.walit.streamline.backend.Core;
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
import java.util.logging.Logger;
import java.util.logging.Level;

public final class YoutubeHandle implements ConnectionHandle {

    public static YoutubeHandle instance;

    private final Config config;
    private final Logger logger;

    public YoutubeHandle(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public static YoutubeHandle getInstance(Config config, Logger logger) {
        if (instance == null) {
            instance = new YoutubeHandle(config, logger);
        }
        return instance;
    }

    @Override
    public CompletableFuture<List<Song>> retrieveSearchResults(String term) {
        return CompletableFuture.supplyAsync(() -> {
            List<Song> results = new ArrayList<>();
            String[] command = {
                config.getBinaryPath(),
                "ytsearch3:'" + term + "'",
                "--print",
                "%(title)s | %(uploader)s | %(duration>%M:%S)s | %(id)s"
            };
            try {
                Process process = Core.runCommandExpectWait(command);
                if (process == null) {
                    System.out.println("process was null");
                    return results;
                }
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
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                logger.log(Level.WARNING, StreamLineMessages.UnableToPullSearchResultsFromYtDlp.getMessage());
                return null;
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
            "-f",
            "ba",
            "--get-url",
            "https://www.youtube.com/watch?v=" + id
        };
        try {
            Process process = Core.runCommandExpectWait(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            process.waitFor();
        } catch (InterruptedException | IOException iE) {
            return null;
        }
        return stringBuilder.toString().trim();
    }

    public static boolean setupYoutubeInterop(Config config) {
        if (!isYtDlpDownloaded(config)) {
            return downloadYtDlp(config);
        }
        return true;
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
        String[] command = {"curl", "-fL", ytDlpUrl, "-o", ytDlpTargetLocation};
        try {
            Process process = Core.runCommandExpectWait(command);
            process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println("Error: curl command failed with exit code " + process.exitValue());
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
                    System.out.println("Binary has been marked as executable.");
                } else {
                    System.out.println("yt-dlp was not downloaded.");
                    return false;
                }
            }
            return true;
        } catch (InterruptedException | IOException iE) {
            iE.printStackTrace();
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
}
