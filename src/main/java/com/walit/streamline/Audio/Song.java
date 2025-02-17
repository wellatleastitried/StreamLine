package com.walit.streamline.Audio;

public class Song {

    private int songId;
    private String title;
    private String artist;
    private String url;
    private String videoId;
    private boolean isLiked;
    private boolean isDownloaded;
    private boolean isRecentlyPlayed;
    private String downloadPath;
    private String fileHash;

    /**
     * Constructor for songs that are being fetched quickly.
     */
    public Song(int songId, String title, String artist, String url, String videoId) {
        if (songId == -1) {
            // TODO: handle this
        }
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.videoId = videoId;
        this.isLiked = false;
        this.isDownloaded = false;
        this.isRecentlyPlayed = false;
        this.downloadPath = null;
        this.fileHash = null;
    }

    /**
     * Constructor for songs that need more context, such as their respective file path and hash.
     */
    public Song(int songId, String title, String artist, String url, String videoId, boolean isLiked, boolean isDownloaded, boolean isRecentlyPlayed, String downloadPath, String fileHash) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.isLiked = isLiked;
        this.isDownloaded = isDownloaded;
        this.isRecentlyPlayed = isRecentlyPlayed;
        this.downloadPath = downloadPath;
        this.fileHash = fileHash;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public void setSongName(String title) {
        this.title = title;
    }

    public void setSongArtist(String artist) {
        this.artist = artist;
    }

    public void setSongLink(String url) {
        this.url = url;
    }

    public void setSongVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setSongLikeStatus(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public void setSongDownloadStatus(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public void setSongRecentlyPlayedStatus(boolean isRecentlyPlayed) {
        this.isRecentlyPlayed = isRecentlyPlayed;
    }

    public int getSongId() {
        return songId;
    }

    public String getSongName() {
        return title;
    }

    public String getSongArtist() {
        return artist;
    }

    public String getSongLink() {
        return url;
    }

    public String getSongVideoId() {
        return videoId;
    }

    public boolean isSongLiked() {
        return isLiked;
    }

    public boolean isSongDownloaded() {
        return isDownloaded;
    }

    public boolean isSongRecentlyPlayed() {
        return isRecentlyPlayed;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public String getFileHash() {
        return fileHash;
    }
}
