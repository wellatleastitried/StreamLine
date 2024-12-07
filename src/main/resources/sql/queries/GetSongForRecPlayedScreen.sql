SELECT Songs.* FROM Songs JOIN RecentlyPlayed ON Songs.id = RecentlyPlayed.song_id ORDER BY RecentlyPlayed.last_listen DESC;
