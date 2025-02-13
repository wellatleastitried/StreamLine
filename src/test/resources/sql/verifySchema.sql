SELECT 'Songs' AS table_name FROM sqlite_master WHERE type='table' AND name='Songs';
SELECT 'Playlists' AS table_name FROM sqlite_master WHERE type='table' AND name='Playlists';
SELECT 'PlaylistSongs' AS table_name FROM sqlite_master WHERE type='table' AND name='PlaylistSongs';
SELECT 'RecentlyPlayed' AS table_name FROM sqlite_master WHERE type='table' AND name='RecentlyPlayed';
SELECT 'LikedSongs' AS table_name FROM sqlite_master WHERE type='table' AND name='LikedSongs';
SELECT 'DownloadedSongs' AS table_name FROM sqlite_master WHERE type='table' AND name='DownloadedSongs';
SELECT 'CachedSongs' AS table_name FROM sqlite_master WHERE type='table' AND name='CachedSongs';

PRAGMA table_info(Songs);
PRAGMA table_info(Playlists);
PRAGMA table_info(PlaylistSongs);
PRAGMA table_info(RecentlyPlayed);
PRAGMA table_info(LikedSongs);
PRAGMA table_info(DownloadedSongs);
PRAGMA table_info(CachedSongs);
