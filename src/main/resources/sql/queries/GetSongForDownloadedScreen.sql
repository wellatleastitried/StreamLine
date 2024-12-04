SELECT Songs.* FROM Songs JOIN DownloadedSongs ON Songs.id = DownloadedSongs.song_id ORDER BY DownloadedSongs.date_downloaded DESC;
