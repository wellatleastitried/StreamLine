-- DO NOT MODIFY THIS FILE --
SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM LikedSongs ORDER BY date_liked DESC);
