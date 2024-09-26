package com.walit.streamline.Communicate;

public enum HelpMessages {

    SearchInformation("The search menu allows you to search for any song or artist you are interested in."),
    LikedMusicInformation("The \"Liked music\" option allows you to view the songs you have \"liked\" over time.");

    private final String message;

    HelpMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
