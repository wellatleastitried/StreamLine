SELECT * FROM Songs WHERE song_id IN (SELECT song_id FROM RecentlyPlayed ORDER BY last_listen DESC);
