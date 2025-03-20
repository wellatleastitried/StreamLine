#!/bin/bash


# PREPARE BASH SCRIPTS
cd scripts/ || exit
echo "Marking all bash scripts as executable..."

for file in ./*
do
    if [[ "$file" =~ \.sh$ ]]; then
        chmod +x "$file"
        echo "$file is now executable."
    else
        echo "$file has been skipped."
    fi
done

echo "All bash scripts have been marked as executable!"


# PREPARE CONFIGURATION FILE
APP_DIR=~/.local/share/StreamLine/config
if [ ! -f "$APP_DIR"/config.properties ]; then
    echo 'language=en' > "$APP_DIR"/config.properties
    echo "config.properties has been created and the language for the app has been set to English."
fi

if [ ! -f "$APP_DIR"/tinylog.properties ]; then
    printf "writer = file\nwriter.file = /tmp/StreamLine/streamline.log\nlevel = debug\nformat = {class_name}#{method:-unknown}(): {message}\nwritingmode = overwrite" > "$APP_DIR"/tinylog.properties
    echo "tinylog.properties has been created."
fi
