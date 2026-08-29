package com.streamline.backend.jobs;

import com.streamline.audio.Song;
import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.StreamLineConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.tinylog.Logger;

public class DownloadJob extends AbstractStreamLineJob {

    private final Song song;
    private final DatabaseRunner dbRunner;
    private final int action;

    private boolean resultIsReady = false;
    private boolean returnStatus;

    public DownloadJob(Config config, Song song, DatabaseRunner dbRunner, int action) {
        super(config);
        this.song = song;
        this.dbRunner = dbRunner;
        this.action = action;
    }

    @Override
    public void execute() {
        if (action == StreamLineConstants.DOWNLOAD_SONG_ACTION) {
            downloadSong();
        } else if (action == StreamLineConstants.CANCEL_DOWNLOAD_ACTION) {
            cancelDownload();
        } else if (action == StreamLineConstants.REMOVE_DOWNLOADED_SONG_ACTION) {
            removeDownloadedSong();
        } else {
            Logger.warn("[!] Invalid action for download job.");
        }
    }

    private void downloadSong() {
        Logger.info("[*] Downloading song: " + song.getSongName());
        song.setSongLink(config.getHandle().getAudioUrlFromVideoId(String.valueOf(song.getSongId())));
        try {
            config.getHandle().downloadSong(song).thenAccept(downloadedSong -> {
                if (downloadedSong.getDownloadPath() != null) {
                    downloadedSong.setFileHash(generateHashFromFile(downloadedSong.getDownloadPath()));
                    returnStatus = true;
                    dbRunner.downloadSong(song);
                    Logger.info("[*] Download completed for song: " + song.getSongName());
                } else {
                    returnStatus = false;
                    Logger.warn("[!] Download failed for song: " + song.getSongName());
                }
            }).join();
            Logger.debug("[*] Download job completed for song: " + song.getSongName());
        } finally {
            resultIsReady = true;
        }
    }

    private void cancelDownload() {
        Logger.info("[*] Cancelling download for song: " + song.getSongName());
        config.getHandle().cancelSongDownload(song);
    }

    private void removeDownloadedSong() {
        Logger.info("[*] Removing downloaded song: " + song.getSongName());
        String downloadPath = dbRunner.getSongFromDownloads(song).getDownloadPath();
        if (downloadPath != null) {
            Logger.info("[*] Deleting file: " + downloadPath);
            File downloadedSong = new File(downloadPath);
            downloadedSong.delete();
            dbRunner.removeDownloadedSong(song);
            Logger.info("[*] Downloaded song removed: " + song.getSongName());
        } else {
            Logger.warn("[!] No file path found for downloaded song.");
        }
    }

    private String generateHashFromFile(String path) {
        try (FileInputStream fS = new FileInputStream(new File(path))) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] byteArray = new byte[1024];
            int byteCount;
            while ((byteCount = fS.read(byteArray)) != -1) {
                digest.update(byteArray, 0, byteCount);
            }
            final byte[] bytes = digest.digest();
            final StringBuilder hexStringOfHash = new StringBuilder(new BigInteger(1, bytes).toString(16));
            while (hexStringOfHash.length() < 64) {
                hexStringOfHash.insert(0, '0');
            }
            return hexStringOfHash.toString();
        } catch (NoSuchAlgorithmException nA) {
            Logger.error("There is a typo in the name of the hashing algorithm being used or Java no longer supports the used algorithm. Either way, it needs to be changed.");
        } catch (IOException iE) {
            Logger.error("[!] There has been an error reading the bytes from the configuration file, please try reloading the app.");
        }
        return null;
    }

    public synchronized boolean getResults() {
        finish();
        return returnStatus;
    }

    public boolean resultsAreReady() {
        return resultIsReady;
    }
}
