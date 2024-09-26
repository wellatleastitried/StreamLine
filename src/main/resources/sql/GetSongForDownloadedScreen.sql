-- DO NOT MODIFY THIS FILE --
SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM DownloadedSongs ORDER BY date_downloaded DESC);
