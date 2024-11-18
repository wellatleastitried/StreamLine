package com.walit.streamline.Communicate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoResult extends SearchResult {
    public String title;
    public String videoId;
    public String author;
    public String authorUrl;
    public List<VideoThumbnail> videoThumbnails;
    public String description;
    public String descriptionHtml;
    public long viewCount;
    public long published;
    public String publishedText;
    public int lengthSeconds;
    public boolean liveNow;
    public boolean paid;
    public boolean premium;

    public static class VideoThumbnail {
        public String quality;
        public String url;
        public int width;
        public int height;
    }
}
