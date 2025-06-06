package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.DownloadedMusicPage;

public class DownloadedMusicPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof DownloadedMusicPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        // DownloadedMusicPage always goes back to main menu
        return NavigationDestination.MAIN_MENU;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
