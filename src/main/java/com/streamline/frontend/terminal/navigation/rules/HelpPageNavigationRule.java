package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.HelpPage;

public class HelpPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof HelpPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        // HelpPage always goes back to main menu
        return NavigationDestination.MAIN_MENU;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
