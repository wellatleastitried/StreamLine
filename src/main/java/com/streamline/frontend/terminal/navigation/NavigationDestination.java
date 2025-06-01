package com.streamline.frontend.terminal.navigation;

import com.streamline.frontend.terminal.page.pages.*;

import java.util.Arrays;

public enum NavigationDestination {

    MAIN_MENU(MainPage.class, false),
    SEARCH(SearchPage.class, false),
    LIKED_MUSIC(LikedMusicPage.class, true),
    PLAYLISTS(PlaylistPage.class, true),
    RECENTLY_PLAYED(RecentlyPlayedPage.class, true),
    DOWNLOADS(DownloadedMusicPage.class, true),
    SETTINGS(SettingsPage.class, false),
    LANGUAGE(LanguagePage.class, false),
    HELP(HelpPage.class, false),

    SONG_OPTIONS(SongOptionPage.class, false, true),
    PLAYLIST_CHOICE(PlaylistChoicePage.class, false, true),
    CREATE_PLAYLIST(CreatePlaylistPage.class, false, true),
    SONGS_IN_PLAYLIST(SongsFromPlaylistPage.class, true, true);

    private final Class<? extends AbstractBasePage> pageClass;
    private final boolean requiresRebuild;
    private final boolean requiresParameters;

    NavigationDestination(Class<? extends AbstractBasePage> pageClass, boolean requiresRebuild) {
        this(pageClass, requiresRebuild, false);
    }

    NavigationDestination(Class<? extends AbstractBasePage> pageClass, boolean requiresRebuild, boolean requiresParameters) {
        this.pageClass = pageClass;
        this.requiresRebuild = requiresRebuild;
        this.requiresParameters = requiresParameters;
    }

    public Class<? extends AbstractBasePage> getPageClass() {
        return pageClass;
    }

    public boolean requiresRebuild() {
        return requiresRebuild;
    }

    public boolean requiresParameters() {
        return requiresParameters;
    }

    public static NavigationDestination fromPageClass(Class<?> pageClass) {
        return Arrays.stream(values())
            .filter(dest -> dest.pageClass.equals(pageClass))
            .findFirst()
            .orElse(MAIN_MENU);
    }
}
