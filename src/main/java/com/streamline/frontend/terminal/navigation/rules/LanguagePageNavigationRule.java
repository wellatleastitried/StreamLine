package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.LanguagePage;

public class LanguagePageNavigationRule implements NavigationRule {

    @Override
    public boolean appliesTo(NavigationContext context) {
        return context.getCurrentPage() instanceof LanguagePage;
    }

    @Override
    public NavigationDestination getDestination(NavigationContext context) {
        // LanguagePage goes back to settings menu
        return NavigationDestination.SETTINGS;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
