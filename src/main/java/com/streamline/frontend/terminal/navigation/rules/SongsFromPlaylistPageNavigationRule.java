package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.SongsFromPlaylistPage;

public class SongsFromPlaylistPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof SongsFromPlaylistPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        // SongsFromPlaylistPage goes back to playlist listing
        context.setContextData("requiresRebuild", true);
        return NavigationDestination.PLAYLISTS;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
