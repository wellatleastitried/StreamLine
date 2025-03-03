package com.walit.streamline.communicate.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSearchResult extends SearchResult {
    private String title;
    private String videoId;
    private String author;
    private String authorUrl;
    private List<VideoThumbnail> videoThumbnails;
    private String description;
    private String descriptionHtml;
    private long viewCount;
    private long published;
    private String publishedText;
    private int lengthSeconds;
    private boolean liveNow;
    private boolean paid;
    private boolean premium;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public List<VideoThumbnail> getVideoThumbnails() {
        return videoThumbnails;
    }

    public void setVideoThumbnails(List<VideoThumbnail> videoThumbnails) {
        this.videoThumbnails = videoThumbnails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getPublished() {
        return published;
    }

    public void setPublished(long published) {
        this.published = published;
    }

    public String getPublishedText() {
        return publishedText;
    }

    public void setPublishedText(String publishedText) {
        this.publishedText = publishedText;
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(int lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public boolean isLiveNow() {
        return liveNow;
    }

    public void setLiveNow(boolean liveNow) {
        this.liveNow = liveNow;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
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
