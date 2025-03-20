#!/bin/bash

LOG_DIR=/tmp/StreamLine/
SL_DIR=~/.local/share/StreamLine/

echo "Removing the current log file directory..."
rm -rf "$LOG_DIR"
echo "The log file directory has been removed."
echo "Removing the application's resources from the system..."
rm -rf "$SL_DIR"
echo "The application's resources have been removed."
