#!/bin/bash

if [ -f ~/.local/share/StreamLine/storage/streamline.db ]; then
    rm ~/.local/share/StreamLine/storage/streamline.db
fi

if [ -f /tmp/StreamLine/streamline.log ]; then
    rm /tmp/StreamLine/streamline.log
fi

mvn clean install
java -jar target/streamline.jar
