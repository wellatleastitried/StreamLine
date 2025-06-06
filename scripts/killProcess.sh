kill -9 "$(ps aux | grep streamline | awk '{print $2; exit;}')"
