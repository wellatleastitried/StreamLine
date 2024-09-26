CREATE TABLE IF NOT EXISTS Songs (
  song_id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  artist TEXT NOT NULL,
  url TEXT NOT NULL,
);
CREATE TABLE IF NOT EXISTS Playlists (
  playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS PlaylistSongs (
  playlist_id INTEGER NOT NULL,
  song_id INTEGER NOT NULL,
  date_added_to_playlist DATETIME DEFAULT CURRENT_TIMESTAMP
  PRIMARY KEY (playlist_id, song_id),
  FOREIGN KEY (playlist_id) REFERENCES Playlists(playlist_id) ON DELETE CASCADE,
  FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE,
);
CREATE TABLE IF NOT EXISTS RecentlyPlayed (
  song_id INTEGER NOT NULL,
  last_listen DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS LikedSongs (
  song_id INTEGER NOT NULL,
  date_liked DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS DownloadedSongs (
  song_id INTEGER NOT NULL,
  date_downloaded DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE
);
