package com.walit.streamline.Communicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VideoResult.class, name = "video"),
    @JsonSubTypes.Type(value = PlaylistResult.class, name = "playlist"),
    @JsonSubTypes.Type(value = ChannelResult.class, name = "channel")
})
public abstract class SearchResult {
    public String type;
}
