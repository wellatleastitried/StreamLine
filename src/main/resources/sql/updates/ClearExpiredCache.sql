DELETE FROM Songs WHERE id IN (SELECT song_id FROM CachedSongs WHERE last_time_played <= DATE('now', '-30 days'));
