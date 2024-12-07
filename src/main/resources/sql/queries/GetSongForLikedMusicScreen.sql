SELECT Songs.* FROM Songs JOIN LikedSongs ON Songs.id = LikedSongs.song_id ORDER BY LikedSongs.date_liked DESC;
