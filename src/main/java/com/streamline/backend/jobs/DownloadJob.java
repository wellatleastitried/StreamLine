package com.streamline.backend.jobs;

import com.streamline.audio.Song;
import com.streamline.database.DatabaseRunner;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.StreamLineConstants;

import java.io.File;

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
        boolean returnStatus = false;
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
        String url = config.getHandle().getAudioUrlFromVideoId(String.valueOf(song.getSongId()));
        try {
            config.getHandle().downloadSong(url).thenAccept(downloadedSong -> {
                if (downloadedSong) {
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
        Logger.debug("[*] Cancelled download for song: " + song.getSongName());
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

    public synchronized boolean getResults() {
        finish();
        return returnStatus;
    }

    public boolean resultsAreReady() {
        return resultIsReady;
    }
}
