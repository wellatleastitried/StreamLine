-- DO NOT MODIFY THIS FILE --
DELETE FROM Songs WHERE song_id NOT IN (SELECT song_id FROM PlaylistSongs, RecentlyPlayed, LikedSongs, DownloadedSongs);
