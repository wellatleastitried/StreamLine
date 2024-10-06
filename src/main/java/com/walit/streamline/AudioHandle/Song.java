package com.walit.streamline.AudioHandle;

public class Song {

    private int songId;
    private String title;
    private String artist;
    private String url;
    private boolean isLiked;
    private boolean isDownloaded;
    private boolean isRecentlyPlayed;

    public Song(int songId, String title, String artist, String url) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.isLiked = false;
        this.isDownloaded = false;
        this.isRecentlyPlayed = false;
    }

    public Song(int songId, String title, String artist, String url, boolean isLiked, boolean isDownloaded, boolean isRecentlyPlayed) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.isLiked = isLiked;
        this.isDownloaded = isDownloaded;
        this.isRecentlyPlayed = isRecentlyPlayed;
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

    public boolean isSongLiked() {
        return isLiked;
    }

    public boolean isSongDownloaded() {
        return isDownloaded;
    }

    public boolean isSongRecentlyPlayed() {
        return isRecentlyPlayed;
    }
}
