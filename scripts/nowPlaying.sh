#!/bin/bash

while true; do
    # This can be changed to an alias later
    SONG=$(java -jar "dist/streamline-$(./scripts/getVersion.sh).jar" --now-playing)
    echo "$SONG" > /tmp/StreamLine/nowPlaying.txt
    sleep 5
done
