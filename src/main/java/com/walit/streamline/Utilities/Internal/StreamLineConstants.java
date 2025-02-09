package com.walit.streamline.Utilities.Internal;


public class StreamLineConstants {

    public static final String  GET_TOKENS_FOR_YOUTUBE_VALIDATOR    = "docker run quay.io/invidious/youtube-trusted-session-generator";
    public static final String  DOCKER_COMPOSE_PATH                 = "./invidious/docker-compose.yml";
    public static final String  INVIDIOUS_INSTANCE_ADDRESS          = "https://localhost:3000/";

    public static final int     INVIDIOUS_PORT      = 3000;
    public static final String  INVIDIOUS_IMAGE     = "quay.io/invidious/invidious:latest";
    public static final String  CONTAINER_NAME      = "invidious_instance";

    public static final int DOCKER_CONTAINER_KILLED          = 0;
    public static final int DOCKER_CONTAINER_DOES_NOT_EXIST  = 1;
    public static final int DOCKER_CONTAINER_NOT_KILLED      = 2;

    public static final boolean REQUEST_INSTANCE_START   = true;

    public static final String WINDOWS_CACHE_ADDRESS    = "%LOCALAPPDATA\\StreamLine\\Cache\\";
    public static final String LINUX_CACHE_ADDRESS      = String.format("%s/.cache/StreamLine/", System.getProperty("user.home"));
    public static final String MAC_CACHE_ADDRESS        = String.format("%s/Library/Caches/com.streamline/", System.getProperty("user.home"));

}
