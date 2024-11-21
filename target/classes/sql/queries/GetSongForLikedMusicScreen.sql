SELECT * FROM Songs WHERE id IN (SELECT song_id FROM LikedSongs ORDER BY date_liked DESC);
