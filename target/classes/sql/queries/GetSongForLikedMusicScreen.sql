SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM LikedSongs ORDER BY date_liked DESC);
