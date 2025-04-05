package com.streamline.communicate.jsonobjects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VideoSearchResult.class, name = "video"),
    @JsonSubTypes.Type(value = PlaylistSearchResult.class, name = "playlist"),
    @JsonSubTypes.Type(value = ChannelSearchResult.class, name = "channel")
})
public abstract class SearchResult {
    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
