SELECT s.* FROM Songs s JOIN CachedSongs cs ON s.id = cs.song_id WHERE cs.last_time_played <= DATE('now', '-30 days');
