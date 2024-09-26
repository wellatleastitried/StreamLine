-- DO NOT MODIFY THIS FILE --
SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM RecentlyPlayed ORDER BY last_listen DESC);
