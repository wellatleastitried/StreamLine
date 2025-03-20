#!/bin/bash

LOG_FILE=/tmp/StreamLine/streamline.log
ERROR_COUNT=0

while [ "$ERROR_COUNT" -lt 1 ]; do
    ERROR_COUNT+="$(tail -n 10 "$LOG_FILE" | grep -ci "error" "$LOG_FILE")"
    sleep 1
done

echo "Encountered an error!"
cat /tmp/StreamLine/streamline.log
