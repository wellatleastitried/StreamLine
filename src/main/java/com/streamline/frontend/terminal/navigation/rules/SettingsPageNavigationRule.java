package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.SettingsPage;

import org.tinylog.Logger;

public class SettingsPageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof SettingsPage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        NavigationDestination desiredPage = context.get("desiredPage", NavigationDestination.class);
        Logger.debug("SettingsPageNavigationRule: Redirecting to desired page: " + desiredPage);
        if (desiredPage != null) {
            return desiredPage;
        }

        /* Default to Main Menu if no desired page is set */
        return NavigationDestination.MAIN_MENU;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
