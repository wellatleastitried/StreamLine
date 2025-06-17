package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.*;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.page.pages.SongOptionPage;
import com.streamline.frontend.terminal.page.pages.SearchPage;
import com.streamline.frontend.terminal.page.pages.LikedMusicPage;
import com.streamline.frontend.terminal.page.pages.DownloadedMusicPage;
import com.streamline.frontend.terminal.page.pages.PlaylistPage;
import com.streamline.frontend.terminal.page.pages.SongsFromPlaylistPage;

public class SongOptionPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof SongOptionPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        AbstractBasePage previousPage = context.getPreviousPage();

        if (previousPage instanceof SearchPage) {
            // Special handling for SearchPage - needs to rebuild with search results
            context.setContextData("requiresRebuild", true);
            context.setContextData("rebuildType", "cachedSearch");
            return NavigationDestination.SEARCH;
        } else if (previousPage instanceof LikedMusicPage) {
            context.setContextData("requiresRebuild", true);
            return NavigationDestination.LIKED_MUSIC;
        } else if (previousPage instanceof DownloadedMusicPage) {
            context.setContextData("requiresRebuild", true);
            return NavigationDestination.DOWNLOADS;
        } else if (previousPage instanceof PlaylistPage) {
            context.setContextData("requiresRebuild", true);
            return NavigationDestination.PLAYLISTS;
        } else if (previousPage instanceof SongsFromPlaylistPage) {
            context.setContextData("requiresRebuild", true);
            return NavigationDestination.SONGS_IN_PLAYLIST;
        } else {
            // Default fallback - go to main menu
            return NavigationDestination.MAIN_MENU;
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
