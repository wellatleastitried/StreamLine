#!/bin/bash

# This is for adding new languages for use with StreamLine.
# To generate a template language file, run this script with a
# two character language code (e.g. "en", "es", "ru", etc.) and
# the template will be created. You will need to then fill in the
# appropriate text in the newly created file.

if [ -z "$1" ]; then
    echo "Usage: $0 <language_code>"
    exit 1
fi

TARGET_DIR="src/main/resources/i18n"
LANGUAGE_CODE="$1"
FILE_NAME="messages_${LANGUAGE_CODE}.properties"
FILE_PATH="${TARGET_DIR}/${FILE_NAME}"

mkdir -p "$TARGET_DIR"

if [ -f "$FILE_PATH" ]; then
    echo "Error: File $FILE_NAME already exists!"
    exit 2
fi

cat <<EOL > "$FILE_PATH"
# Language: $LANGUAGE_CODE
# Auto-generated template for translations
app.title=StreamLine Music Player
app.goodbye=

# CLI Menu


# Back button to be used in most screens
button.back=<- Back

# Song options
button.addToPlaylist=
button.likeSong=
button.play=
button.pause=

# Main Menu
button.searchForSong=
button.viewLikedSong=
button.playlists=
button.recentlyPlayed=
button.downloadedMusic=
button.help=
button.settings=
button.quit=

# Help Menu
window.helpTitle=
label.searchHelpTitle=
label.searchHelpBody=
label.likedMusicTitle=
label.likedMusicBody=

# Settings Menu
window.settingsTitle=
button.clearCache=
button.chooseLanguage=
EOL

echo "Template created: $FILE_PATH"

