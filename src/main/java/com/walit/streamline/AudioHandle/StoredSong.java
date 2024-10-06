package com.walit.streamline.AudioHandle;

public class StoredSong extends Song {

    private final String fileLocation;
    private final String fileHash;

    public StoredSong(int songId, String title, String artist, String url, String fileLocation, String fileHash) {
        super(songId, title, artist, url);
        this.fileLocation = fileLocation;
        this.fileHash = fileHash;
    }

    public StoredSong(int songId, String title, String artist, String url, boolean isLiked, boolean isDownloaded, boolean isRecentlyPlayed, String fileLocation, String fileHash) {
        super(songId, title, artist, url, isLiked, isDownloaded, isRecentlyPlayed);
        this.fileLocation = fileLocation;
        this.fileHash = fileHash;
    }
    
    public String getFileLocation() {
        return fileLocation;
    }

    public String getFileHash() {
        return fileHash;
    }
}
