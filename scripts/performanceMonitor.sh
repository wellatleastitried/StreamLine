#!/bin/bash

APP="streamline-$(./scripts/getVersion.sh).jar"
INTERVAL=5

rm logs/system_usage.log

echo "Monitoring system resources used by $APP"

while true; do
    pid=$(pgrep -f "$APP")
    if [ -n "$pid" ]; then
        echo "$(date) - CPU: $(ps -p $pid -o %cpu=)%, MEM: $(ps -p $pid -o %mem=)%" >> logs/system_usage.log
    else
        echo "$(date) - $APP not running" >> logs/system_usage.log
    fi
    sleep $INTERVAL
done
