DELETE FROM RecentlyPlayed WHERE song_id IN (SELECT song_id FROM RecentlyPlayed ORDER BY last_listen ASC LIMIT (SELECT COUNT(*) FROM RecentlyPlayed) - 35);
