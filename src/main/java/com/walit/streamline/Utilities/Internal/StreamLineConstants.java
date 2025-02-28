package com.walit.streamline.Utilities.Internal;


public class StreamLineConstants {

    public static final String  GET_TOKENS_FOR_YOUTUBE_VALIDATOR        = "docker run quay.io/invidious/youtube-trusted-session-generator";
    public static final String  INVIDIOUS_INSTANCE_ADDRESS              = "http://localhost:3000/";
    public static final String  INVIDIOUS_GITHUB_REPO_ADDRESS           = "https://github.com/iv-org/invidious.git";
    public static final String  INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS      = System.getProperty("user.home") + "/.local/share/streamline/invidious";
    public static final String  INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS    = System.getProperty("APPDATA") + "\\StreamLine\\invidious";
    public static final String  INVIDIOUS_LOCAL_MAC_REPO_ADDRESS        = System.getProperty("user.home") + "/Library/Application Support/StreamLine/invidious";
    public static final int     INVIDIOUS_PORT                          = 3000;

    public static final int DOCKER_CONTAINER_KILLED         = 0;
    public static final int DOCKER_CONTAINER_DOES_NOT_EXIST = 1;
    public static final int DOCKER_CONTAINER_NOT_KILLED     = 2;

    public static final boolean REQUEST_INSTANCE_START      = true;

    public static final String  YOUTUBE_HOST                = "";

    public static final String  WINDOWS_CACHE_ADDRESS       = "%LOCALAPPDATA%\\StreamLine\\Cache\\";
    public static final String  LINUX_CACHE_ADDRESS         = System.getProperty("user.home") + "/.cache/StreamLine/";
    public static final String  MAC_CACHE_ADDRESS           = System.getProperty("user.home") + "/Library/Caches/com.streamline/";

    public static final String  WINDOWS_TEMP_DIR_PATH       = System.getProperty("TEMP") + "\\Streamline\\";
    public static final String  OTHER_OS_TEMP_DIR_PATH      = "/tmp/streamline/";

    public static final String  WINDOWS_DB_ADDRESS          = System.getProperty("APPDATA") + "\\StreamLine\\streamline.db";
    public static final String  LINUX_DB_ADDRESS            = System.getProperty("user.home") + "/.config/StreamLine/storage/streamline.db";
    public static final String  MAC_DB_ADDRESS              = System.getProperty("user.home") + "/Library/Application Support/StreamLine/streamline.db";
    public static final String  TESTING_DB_ADDRESS          = "/tmp/StreamLine/TEST.db";

    public static final char[]  SPINNER_SYMBOLS             = {'-', '\\', '|', '/'};

    public static final String  BUILD_INVIDIOUS_IMAGE       = "Building Invidious image";
    public static final String  RETRIEVING_TOKENS_MESSAGE   = "Retrieving tokens from Youtube validator";
    public static final String  CLONING_REPO_MESSAGE        = "Cloning Invidious repository";
    public static final String  LOADING_COMPLETE_SYMBOL     = "[✔] ";
    public static final String  LOADING_ERROR_MESSAGE       = "[!] An error has occurred:                                                      \n";

    public static final String  HOST_RESOURCE_PATH          = "/ApiInstanceList.txt";
}
