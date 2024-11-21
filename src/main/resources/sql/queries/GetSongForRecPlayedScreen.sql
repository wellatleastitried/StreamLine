SELECT * FROM Songs WHERE id IN (SELECT song_id FROM RecentlyPlayed ORDER BY last_listen DESC);
