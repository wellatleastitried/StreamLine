package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.window.TerminalWindowManager;

import org.tinylog.Logger;

public class NavigateToPageCommand<T extends AbstractBasePage> implements NavigationCommand {
    
    private final Class<T> targetPageClass;
    // TODO: constructorArgs will be used when PageRegistry is implemented
    @SuppressWarnings("unused")
    private final Object[] constructorArgs;
    
    public NavigateToPageCommand(Class<T> targetPageClass) {
        this(targetPageClass, new Object[0]);
    }
    
    public NavigateToPageCommand(Class<T> targetPageClass, Object... constructorArgs) {
        this.targetPageClass = targetPageClass;
        this.constructorArgs = constructorArgs;
    }
    
    @Override
    public void execute(TerminalWindowManager wm) {
        navigateToPage(wm);
    }
    
    private void navigateToPage(TerminalWindowManager wm) {
        String className = targetPageClass.getSimpleName();
        Logger.debug("NavigateToPageCommand.navigateToStaticPage for {}", className);
        
        switch (className) {
            case "MainPage":
                Logger.debug("Navigating to MainPage");
                wm.showMainMenu();
                break;
            case "SearchPage":
                Logger.debug("Navigating to SearchPage");
                wm.transitionTo(wm.getSearchPageWindow());
                break;
            case "LikedMusicPage":
                wm.transitionTo(wm.getLikedMusicPageWindow());
                break;
            case "PlaylistPage":
                Logger.debug("Navigating to PlaylistPage");
                wm.transitionTo(wm.getPlaylistPageWindow());
                break;
            case "RecentlyPlayedPage":
                Logger.debug("Navigating to RecentlyPlayedPage");
                wm.transitionTo(wm.getRecentlyPlayedPageWindow());
                break;
            case "DownloadedMusicPage":
                Logger.debug("Navigating to DownloadedMusicPage");
                wm.transitionTo(wm.getDownloadedPageWindow());
                break;
            case "SettingsPage":
                Logger.debug("Navigating to SettingsPage");
                wm.transitionTo(wm.getSettingsPageWindow());
                break;
            case "LanguagePage":
                Logger.debug("Navigating to LanguagePage");
                wm.transitionTo(wm.getLanguagePageWindow());
                break;
            case "HelpPage":
                Logger.debug("Navigating to HelpPage");
                wm.transitionTo(wm.getHelpPageWindow());
                break;
            default:
                // Fallback to main menu for unknown pages
                Logger.debug("Unknown page class {}, falling back to MainPage", className);
                wm.showMainMenu();
                break;
        }
    }
    
    @Override
    public String getDescription() {
        return "Navigate to " + targetPageClass.getSimpleName();
    }
    
    @Override
    public boolean canExecute() {
        return targetPageClass != null;
    }
}
