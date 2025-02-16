SELECT s.* FROM Songs s JOIN RecentlyPlayed rp ON s.id = rp.song_id ORDER BY rp.last_listen DESC;
