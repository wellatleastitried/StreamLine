#!/bin/bash

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
