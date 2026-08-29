SELECT s.* FROM Songs s INNER JOIN DownloadedSongs ds ON s.id = ds.song_id ORDER BY ds.date_downloaded DESC;
