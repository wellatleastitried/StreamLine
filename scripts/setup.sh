#!/bin/bash
# Mark all scripts in this directory executable

for file in ./*
do
    chmod +x "$file"
done
