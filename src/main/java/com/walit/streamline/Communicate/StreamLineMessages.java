package com.walit.streamline.Communicate;

public enum StreamLineMessages {

    FatalError("[!] A fatal error has occured while starting StreamLine, please try reloading the app."),
    Farewell("[*] Thank you for using StreamLine!"),
    SearchInformation("The search menu allows you to search for any song or artist you are interested in."),
    LikedMusicInformation("The \"Liked music\" option allows you to view the songs you have \"liked\" over time.");

    private final String message;

    StreamLineMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
