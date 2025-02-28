package com.walit.streamline.Communicate.JsonObjects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistSearchResult extends SearchResult {
    private String title;
    private String playlistId;
    private String playlistThumbnail;
    private String author;
    private String authorId;
    private String authorUrl;
    private boolean authorVerified;
    private int videoCount;
    private List<PlaylistVideo> videos;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistThumbnail() {
        return playlistThumbnail;
    }

    public void setPlaylistThumbnail(String playlistThumbnail) {
        this.playlistThumbnail = playlistThumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public boolean isAuthorVerified() {
        return authorVerified;
    }

    public void setAuthorVerified(boolean authorVerified) {
        this.authorVerified = authorVerified;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public List<PlaylistVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<PlaylistVideo> videos) {
        this.videos = videos;
    }

    public static class PlaylistVideo {
        private String title;
        private String videoId;
        private int lengthSeconds;
        private List<VideoThumbnail> videoThumbnails;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public int getLengthSeconds() {
            return lengthSeconds;
        }

        public void setLengthSeconds(int lengthSeconds) {
            this.lengthSeconds = lengthSeconds;
        }

        public List<VideoThumbnail> getVideoThumbnails() {
            return videoThumbnails;
        }

        public void setVideoThumbnails(List<VideoThumbnail> videoThumbnails) {
            this.videoThumbnails = videoThumbnails;
        }

        public static class VideoThumbnail {
            private String quality;
            private String url;
            private int width;
            private int height;

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }

}
