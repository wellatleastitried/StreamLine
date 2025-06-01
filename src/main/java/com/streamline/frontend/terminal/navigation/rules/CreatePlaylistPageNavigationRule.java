package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.audio.Song;
import com.streamline.frontend.terminal.navigation.*;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.page.pages.CreatePlaylistPage;
import com.streamline.frontend.terminal.page.pages.PlaylistChoicePage;
import com.streamline.frontend.terminal.page.pages.PlaylistPage;

public class CreatePlaylistPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof CreatePlaylistPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        AbstractBasePage previousPage = context.getPreviousPage();

        if (previousPage instanceof PlaylistChoicePage) {
            Song cachedSong = context.getContextData("cachedSong", Song.class);
            AbstractBasePage cachedPage = context.getContextData("cachedPage", AbstractBasePage.class);

            if (cachedSong != null && cachedPage != null) {
                context.setContextData("requiresRebuild", true);
                context.setContextData("rebuildArgs", new Object[]{cachedSong, cachedPage});
            }
            return NavigationDestination.PLAYLIST_CHOICE;
        } else if (previousPage instanceof PlaylistPage) {
            context.setContextData("requiresRebuild", true);
            return NavigationDestination.PLAYLISTS;
        } else {
            return NavigationDestination.MAIN_MENU;
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
