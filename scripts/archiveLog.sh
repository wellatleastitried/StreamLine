#!/bin/bash

LOG_FILE=/tmp/StreamLine/streamline.log

echo "Checking if log file exists..."

if [ -f "$LOG_FILE" ]; then
    cp "$LOG_FILE" logs/archive/archived_streamline.log
    echo "Log file has been successfully archived in logs/archive"
else
    echo "No log file exists at the moment."
fi
