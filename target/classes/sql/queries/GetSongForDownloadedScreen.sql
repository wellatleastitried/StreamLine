SELECT * FROM Songs WHERE id IN (SELECT song_id FROM DownloadedSongs ORDER BY date_downloaded DESC);
