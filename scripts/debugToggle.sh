#!/bin/bash

JAVA_FILE_WITH_CONFIG_LINE=src/main/java/com/walit/streamline/utilities/internal/StreamLineConstants.java

if grep -q "level = DEBUG" "$JAVA_FILE_WITH_CONFIG_LINE"; then
    sed -i 's/level = DEBUG/level = INFO/' "$JAVA_FILE_WITH_CONFIG_LINE"
    echo "Debug mode disabled for StreamLine log."
else
    sed -i 's/level = INFO/level = DEBUG/' "$JAVA_FILE_WITH_CONFIG_LINE"
    echo "Debug mode enabled for StreamLine log."
fi
