package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.LikedMusicPage;

public class LikedMusicPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof LikedMusicPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        // LikedMusicPage always goes back to main menu
        return NavigationDestination.MAIN_MENU;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
