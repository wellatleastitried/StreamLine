#!/bin/bash

if ! command -v java &> /dev/null; then
    echo "Java is not installed!"
    exit
else
    echo "Java version: $(java -version 2>&1 | head -n 1)"
fi

if ! command -v yt-dlp &> /dev/null; then
    echo "yt-dlp is not installed!"
else
    echo "yt-dlp version: $(yt-dlp --version)"
fi

