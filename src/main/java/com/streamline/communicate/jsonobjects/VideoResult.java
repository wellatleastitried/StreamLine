package com.streamline.communicate.jsonobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoResult {
    private String type;
    private String title;
    private String videoId;

    @JsonProperty("videoThumbnails")
    private List<Thumbnail> videoThumbnails;

    private List<Storyboard> storyboards;
    private String description;
    private String descriptionHtml;
    private long published;
    private String publishedText;
    private List<String> keywords;
    private long viewCount;
    private int likeCount;
    private int dislikeCount;

    private boolean paid;
    private boolean premium;
    private boolean isFamilyFriendly;
    private List<String> allowedRegions;
    private String genre;
    private String genreUrl;

    private String author;
    private String authorId;
    private String authorUrl;

    @JsonProperty("authorThumbnails")
    private List<Thumbnail> authorThumbnails;

    private String subCountText;
    private int lengthSeconds;
    private boolean allowRatings;
    private float rating;
    private boolean isListed;
    private boolean liveNow;
    private boolean isPostLiveDvr;
    private boolean isUpcoming;
    private String dashUrl;
    private Long premiereTimestamp; // Nullable field
    private String hlsUrl;          // Nullable field

    @JsonProperty("adaptiveFormats")
    private List<AdaptiveFormat> adaptiveFormats;

    @JsonProperty("formatStreams")
    private List<FormatStream> formatStreams;

    private List<Caption> captions;

    @JsonProperty("recommendedVideos")
    private List<RecommendedVideo> recommendedVideos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public List<Thumbnail> getVideoThumbnails() {
        return videoThumbnails;
    }

    public void setVideoThumbnails(List<Thumbnail> videoThumbnails) {
        this.videoThumbnails = videoThumbnails;
    }

    public List<Storyboard> getStoryboards() {
        return storyboards;
    }

    public void setStoryboards(List<Storyboard> storyboards) {
        this.storyboards = storyboards;
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

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
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

    public boolean isFamilyFriendly() {
        return isFamilyFriendly;
    }

    public void setFamilyFriendly(boolean familyFriendly) {
        isFamilyFriendly = familyFriendly;
    }

    public List<String> getAllowedRegions() {
        return allowedRegions;
    }

    public void setAllowedRegions(List<String> allowedRegions) {
        this.allowedRegions = allowedRegions;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenreUrl() {
        return genreUrl;
    }

    public void setGenreUrl(String genreUrl) {
        this.genreUrl = genreUrl;
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

    public List<Thumbnail> getAuthorThumbnails() {
        return authorThumbnails;
    }

    public void setAuthorThumbnails(List<Thumbnail> authorThumbnails) {
        this.authorThumbnails = authorThumbnails;
    }

    public String getSubCountText() {
        return subCountText;
    }

    public void setSubCountText(String subCountText) {
        this.subCountText = subCountText;
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(int lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public boolean isAllowRatings() {
        return allowRatings;
    }

    public void setAllowRatings(boolean allowRatings) {
        this.allowRatings = allowRatings;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isListed() {
        return isListed;
    }

    public void setListed(boolean listed) {
        isListed = listed;
    }

    public boolean isLiveNow() {
        return liveNow;
    }

    public void setLiveNow(boolean liveNow) {
        this.liveNow = liveNow;
    }

    public boolean isPostLiveDvr() {
        return isPostLiveDvr;
    }

    public void setPostLiveDvr(boolean postLiveDvr) {
        isPostLiveDvr = postLiveDvr;
    }

    public boolean isUpcoming() {
        return isUpcoming;
    }

    public void setUpcoming(boolean upcoming) {
        isUpcoming = upcoming;
    }

    public String getDashUrl() {
        return dashUrl;
    }

    public void setDashUrl(String dashUrl) {
        this.dashUrl = dashUrl;
    }

    public Long getPremiereTimestamp() {
        return premiereTimestamp;
    }

    public void setPremiereTimestamp(Long premiereTimestamp) {
        this.premiereTimestamp = premiereTimestamp;
    }

    public String getHlsUrl() {
        return hlsUrl;
    }

    public void setHlsUrl(String hlsUrl) {
        this.hlsUrl = hlsUrl;
    }

    public List<AdaptiveFormat> getAdaptiveFormats() {
        return adaptiveFormats;
    }

    public void setAdaptiveFormats(List<AdaptiveFormat> adaptiveFormats) {
        this.adaptiveFormats = adaptiveFormats;
    }

    public List<FormatStream> getFormatStreams() {
        return formatStreams;
    }

    public void setFormatStreams(List<FormatStream> formatStreams) {
        this.formatStreams = formatStreams;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public void setCaptions(List<Caption> captions) {
        this.captions = captions;
    }

    public List<RecommendedVideo> getRecommendedVideos() {
        return recommendedVideos;
    }

    public void setRecommendedVideos(List<RecommendedVideo> recommendedVideos) {
        this.recommendedVideos = recommendedVideos;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        private String quality;
        private String url;
        private int width;
        private int height;

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getQuality() {
            return quality;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Storyboard {
        private String url;
        private String templateUrl;
        private int width;
        private int height;
        private int count;
        private int interval;
        private int storyboardWidth;
        private int storyboardHeight;
        private int storyboardCount;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStoryboardWidth() {
            return storyboardWidth;
        }

        public void setStoryboardWidth(int storyboardWidth) {
            this.storyboardWidth = storyboardWidth;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }

        public void setTemplateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getStoryboardCount() {
            return storyboardCount;
        }

        public void setStoryboardId(int storyboardCount) {
            this.storyboardCount = storyboardCount;
        }

        public int getStoryboardHeight() {
            return storyboardHeight;
        }

        public void setStoryboardHeight(int storyboardHeight) {
            this.storyboardHeight = storyboardHeight;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdaptiveFormat {
        private String index;
        private String bitrate;
        private String init;
        private String url;
        private String itag;
        private String type;
        private String clen;
        private String lmt;
        private String projectionType;
        private String container;
        private String encoding;
        private String audioQuality;
        private String audioSampleRate;
        private String audioChannels;

        public String getInit() {
            return init;
        }

        public void setInit(String init) {
            this.init = init;
        }

        public String getClen() {
            return clen;
        }

        public void setClen(String clen) {
            this.clen = clen;
        }

        public String getLmt() {
            return lmt;
        }

        public void setLmt(String lmt) {
            this.lmt = lmt;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getItag() {
            return itag;
        }

        public void setItag(String itag) {
            this.itag = itag;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getAudioQuality() {
            return audioQuality;
        }

        public void setAudioQuality(String audioQuality) {
            this.audioQuality = audioQuality;
        }

        public String getContainer() {
            return container;
        }

        public void setContainer(String container) {
            this.container = container;
        }

        public String getBitrate() {
            return bitrate;
        }

        public void setBitrate(String bitrate) {
            this.bitrate = bitrate;
        }

        public String getAudioSampleRate() {
            return audioSampleRate;
        }

        public void setAudioSampleRate(String audioSampleRate) {
            this.audioSampleRate = audioSampleRate;
        }

        public String getProjectionType() {
            return projectionType;
        }

        public void setProjectionType(String projectionType) {
            this.projectionType = projectionType;
        }

        public String getAudioChannels() {
            return audioChannels;
        }

        public void setAudioChannels(String audioChannels) {
            this.audioChannels = audioChannels;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormatStream {
        private String url;
        private String itag;
        private String type;
        private String quality;
        private String container;
        private String encoding;
        private String qualityLabel;
        private String resolution;
        private String size;

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getQualityLabel() {
            return qualityLabel;
        }

        public void setQualityLabel(String qualityLabel) {
            this.qualityLabel = qualityLabel;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getItag() {
            return itag;
        }

        public void setItag(String itag) {
            this.itag = itag;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContainer() {
            return container;
        }

        public void setContainer(String container) {
            this.container = container;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Caption {
        private String label;
        private String languageCode;
        private String url;

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecommendedVideo {
        private String videoId;
        private String title;

        @JsonProperty("videoThumbnails")
        private List<Thumbnail> videoThumbnails;

        private String author;
        private int lengthSeconds;
        private String viewCountText;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public List<Thumbnail> getVideoThumbnails() {
            return videoThumbnails;
        }

        public void setThumbnails(List<Thumbnail> videoThumbnails) {
            this.videoThumbnails = videoThumbnails;
        }

        public String getViewCountText() {
            return viewCountText;
        }

        public void setViewCountText(String viewCountText) {
            this.viewCountText = viewCountText;
        }

        public int getLengthSeconds() {
            return lengthSeconds;
        }

        public void setLengthSeconds(int lengthSeconds) {
            this.lengthSeconds = lengthSeconds;
        }
    }
}
