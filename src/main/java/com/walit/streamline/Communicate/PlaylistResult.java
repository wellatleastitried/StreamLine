package com.walit.streamline.Communicate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistResult extends SearchResult {
    public String title;
    public String playlistId;
    public String playlistThumbnail;
    public String author;
    public String authorId;
    public String authorUrl;
    public boolean authorVerified;
    public int videoCount;
    public List<PlaylistVideo> videos;

    public static class PlaylistVideo {
        public String title;
        public String videoId;
        public int lengthSeconds;
        public List<VideoThumbnail> videoThumbnails;

        public static class VideoThumbnail {
            public String quality;
            public String url;
            public int width;
            public int height;
        }
    }

}
