package com.streamline.frontend.terminal.navigation.commands;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.window.NewTerminalWindowManager;

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
    public void execute(NewTerminalWindowManager windowManager) {
        // For now, use existing window manager methods
        // This will be enhanced once PageRegistry is implemented
        
        // Navigate to the appropriate static page window
        navigateToStaticPage(windowManager);
    }
    
    private void navigateToStaticPage(NewTerminalWindowManager windowManager) {
        String className = targetPageClass.getSimpleName();
        Logger.debug("NavigateToPageCommand.navigateToStaticPage for {}", className);
        
        switch (className) {
            case "MainPage":
                Logger.debug("Navigating to MainPage");
                windowManager.showMainMenu();
                break;
            case "SearchPage":
                Logger.debug("Navigating to SearchPage");
                windowManager.transitionTo(windowManager.getSearchPageWindow());
                break;
            case "LikedMusicPage":
                windowManager.transitionTo(windowManager.getLikedMusicPageWindow());
                break;
            case "PlaylistPage":
                Logger.debug("Navigating to PlaylistPage");
                windowManager.transitionTo(windowManager.getPlaylistPageWindow());
                break;
            case "RecentlyPlayedPage":
                Logger.debug("Navigating to RecentlyPlayedPage");
                windowManager.transitionTo(windowManager.getRecentlyPlayedPageWindow());
                break;
            case "DownloadedMusicPage":
                Logger.debug("Navigating to DownloadedMusicPage");
                windowManager.transitionTo(windowManager.getDownloadedPageWindow());
                break;
            case "SettingsPage":
                Logger.debug("Navigating to SettingsPage");
                windowManager.transitionTo(windowManager.getSettingsPageWindow());
                break;
            case "LanguagePage":
                Logger.debug("Navigating to LanguagePage");
                windowManager.transitionTo(windowManager.getLanguagePageWindow());
                break;
            case "HelpPage":
                Logger.debug("Navigating to HelpPage");
                windowManager.transitionTo(windowManager.getHelpPageWindow());
                break;
            default:
                // Fallback to main menu for unknown pages
                Logger.debug("Unknown page class {}, falling back to MainPage", className);
                windowManager.showMainMenu();
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
