set shell := ["bash", "-cu"]

build:
    if [ -f ~/.local/share/Streamline/storage/streamline.db ]; then \
        rm ~/.local/share/Streamline/storage/streamline.db; \
    fi
    if [ -f /tmp/Streamline/streamline.log ]; then \
        rm /tmp/Streamline/streamline.log; \
    fi
    mvn clean install
    java -jar target/streamline.jar

test:
    mkdir logs || true
    mvn test > logs/mvnTest.log

setup:
    APP_DIR=~/.local/share/Streamline/config
    if [ ! -f "$APP_DIR"/config.properties ]; then \
        echo "language=en" > "$APP_DIR"/config.properties; \
        echo "theme=default" >> "$APP_DIR"/config.properties; \
        echo "config.properties has been created and the language for the app has been set to English."; \
    fi
    if [ ! -f "$APP_DIR"/tinylog.properties ]; then \
        printf "writer = file\nwriter.file = /tmp/Streamline/streamline.log\nlevel = debug\nformat = {class_name}#{method:-unknown}(): {message}\nwritingmode = overwrite" > "$APP_DIR"/tinylog.properties; \
        echo "tinylog.properties has been created."; \
    fi

run:
    java -jar target/streamline.jar

access-db:
    sqlite3 ~/.local/share/Streamline/storage/streamline.db

killProcess:
    kill -9 "$(ps aux | grep streamline | awk '{print $2; exit;}')"
