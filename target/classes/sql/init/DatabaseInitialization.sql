DROP TABLE IF EXISTS Songs;
CREATE TABLE IF NOT EXISTS Songs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    videoId VARCHAR(255) NOT NULL
);
DROP TABLE IF EXISTS Playlists;
CREATE TABLE IF NOT EXISTS Playlists (
    playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    date_created DATETIME DEFAULT CURRENT_TIMESTAMP
);
DROP TABLE IF EXISTS PlaylistSongs;
CREATE TABLE IF NOT EXISTS PlaylistSongs (
    playlist_id INTEGER NOT NULL,
    song_id INTEGER NOT NULL,
    date_added_to_playlist DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES Playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES Songs(id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS RecentlyPlayed;
CREATE TABLE IF NOT EXISTS RecentlyPlayed (
    song_id INTEGER NOT NULL,
    last_listen DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (song_id) REFERENCES Songs(id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS LikedSongs;
CREATE TABLE IF NOT EXISTS LikedSongs (
    song_id INTEGER NOT NULL,
    date_liked DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (song_id) REFERENCES Songs(id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS DownloadedSongs;
CREATE TABLE IF NOT EXISTS DownloadedSongs (
    song_id INTEGER NOT NULL,
    date_downloaded DATETIME DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(255) NOT NULL,
    file_hash VARCHAR(64) NOT NULL,
    FOREIGN KEY (song_id) REFERENCES Songs(id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS CachedSongs;
CREATE TABLE IF NOT EXISTS CachedSongs (
    song_id INTEGER NOT NULL,
    date_cached DATETIME DEFAULT CURRENT_TIMESTAMP,
    file_path VARCHAR(255) NOT NULL,
    file_hash VARCHAR(64) NOT NULL,
    last_time_played DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (song_id) REFERENCES Songs(id) ON DELETE CASCADE
);
