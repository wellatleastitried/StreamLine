SELECT s.* FROM Songs s JOIN LikedSongs ls ON s.id = ls.song_id ORDER BY ls.date_liked DESC;
