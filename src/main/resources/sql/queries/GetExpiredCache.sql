SELECT Songs.* FROM Songs JOIN CachedSongs ON Songs.id = CachedSongs.song_id WHERE CachedSongs.last_time_played <= DATEADD(day, -30, GETDATE());
