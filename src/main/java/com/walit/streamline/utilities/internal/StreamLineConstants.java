package com.walit.streamline.utilities.internal;


public class StreamLineConstants {

    public static final String  GET_TOKENS_FOR_YOUTUBE_VALIDATOR        = "docker run quay.io/invidious/youtube-trusted-session-generator";
    public static final String  INVIDIOUS_INSTANCE_ADDRESS              = "http://localhost:3000/";
    public static final String  INVIDIOUS_GITHUB_REPO_ADDRESS           = "https://github.com/iv-org/invidious.git";
    public static final String  INVIDIOUS_LOCAL_LINUX_REPO_ADDRESS      = System.getProperty("user.home") + "/.local/share/StreamLine/invidious";
    public static final String  INVIDIOUS_LOCAL_WINDOWS_REPO_ADDRESS    = System.getenv("APPDATA") + "\\Local\\StreamLine\\invidious";
    public static final String  INVIDIOUS_LOCAL_MAC_REPO_ADDRESS        = System.getProperty("user.home") + "/Library/Application Support/StreamLine/invidious";
    public static final int     INVIDIOUS_PORT                          = 3000;

    public static final int DOCKER_CONTAINER_KILLED         = 0;
    public static final int DOCKER_CONTAINER_DOES_NOT_EXIST = 1;
    public static final int DOCKER_CONTAINER_NOT_KILLED     = 2;

    public static final boolean REQUEST_INSTANCE_START      = true;

    public static final String  YOUTUBE_HOST                = ""; // Add this later if needed

    public static final String  YT_DLP_BIN_LOCATION_WINDOWS = System.getenv("APPDATA") + "\\Local\\StreamLine\\bin\\";
    public static final String  YT_DLP_BIN_LOCATION_LINUX   = System.getProperty("user.home") + "/.local/share/StreamLine/bin/";
    public static final String  YT_DLP_BIN_LOCATION_MAC     = System.getProperty("user.home") + "/Library/Application Support/StreamLine/bin/";

    public static final String  WINDOWS_CACHE_ADDRESS       = System.getenv("APPDATA") + "\\Local\\StreamLine\\Cache\\";
    public static final String  LINUX_CACHE_ADDRESS         = System.getProperty("user.home") + "/.local/share/StreamLine/cache/";
    public static final String  MAC_CACHE_ADDRESS           = System.getProperty("user.home") + "/Library/Caches/com.streamline/";

    public static final String  WINDOWS_TEMP_DIR_PATH       = System.getenv("TEMP") + "\\Streamline\\";
    public static final String  OTHER_OS_TEMP_DIR_PATH      = "/tmp/StreamLine/";

    public static final String  WINDOWS_LOG_CONFIG_DIR_PATH = System.getenv("APPDATA") + "\\Local\\StreamLine\\config\\";
    public static final String  LINUX_LOG_CONFIG_DIR_PATH   = System.getProperty("user.home") + "/.local/share/StreamLine/config/";
    public static final String  MAC_LOG_CONFIG_DIR_PATH     = System.getProperty("user.home") + "/Library/Application Support/StreamLine/config/";

    public static final String  WINDOWS_DB_ADDRESS          = System.getenv("APPDATA") + "\\Local\\StreamLine\\storage\\streamline.db";
    public static final String  LINUX_DB_ADDRESS            = System.getProperty("user.home") + "/.local/share/StreamLine/storage/streamline.db";
    public static final String  MAC_DB_ADDRESS              = System.getProperty("user.home") + "/Library/Application Support/StreamLine/storage/streamline.db";
    public static final String  LINUX_TESTING_DB_ADDRESS    = "/tmp/StreamLine/TEST.db";
    public static final String  WINDOWS_TESTING_DB_ADDRESS  = System.getenv("TEMP") + "\\StreamLine\\TEST.db";

    public static final char[]  SPINNER_SYMBOLS             = {'-', '\\', '|', '/'};

    public static final String  BUILD_INVIDIOUS_IMAGE       = "Building Invidious image";
    public static final String  RETRIEVING_TOKENS_MESSAGE   = "Retrieving tokens from Youtube validator";
    public static final String  CLONING_REPO_MESSAGE        = "Cloning Invidious repository";
    public static final String  LOADING_COMPLETE_SYMBOL     = "[✔] ";
    public static final String  LOADING_ERROR_MESSAGE       = "[!] An error has occurred:                                                      \n";

    public static final String  HOST_RESOURCE_PATH          = "/ApiInstanceList.txt";

    public static final String  WINDOWS_LOG_CONFIG_CONTENTS = "writer = file\r\nwriter.file = " + WINDOWS_TEMP_DIR_PATH + "streamline.log\r\nlevel = info\r\nformat = {class_name}#{method:-unknown}(): {message}\r\nwritingmode = overwrite";
    public static final String  UNIX_LOG_CONFIG_CONTENTS    = "writer = file\nwriter.file = " + OTHER_OS_TEMP_DIR_PATH + "streamline.log\nlevel = info\nformat = {class_name}#{method:-unknown}(): {message}\nwritingmode = overwrite";

    // Set cache expiration at 5 minutes
    public static final long YOUTUBE_CACHE_EXPIRY_MS = 5 * 60 * 1000;
}
