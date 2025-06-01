package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.window.TerminalWindowManager;

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
    public void execute(TerminalWindowManager windowManager) {
        // For now, use existing window manager methods
        // This will be enhanced once PageRegistry is implemented
        
        // Navigate to the appropriate static page window
        navigateToStaticPage(windowManager);
    }
    
    private void navigateToStaticPage(TerminalWindowManager windowManager) {
        String className = targetPageClass.getSimpleName();
        
        switch (className) {
            case "MainPage":
                windowManager.showMainMenu();
                break;
            case "SearchPage":
                windowManager.transitionTo(windowManager.searchPageWindow);
                break;
            case "LikedMusicPage":
                windowManager.transitionTo(windowManager.likedMusicPageWindow);
                break;
            case "PlaylistPage":
                windowManager.transitionTo(windowManager.playlistPageWindow);
                break;
            case "RecentlyPlayedPage":
                windowManager.transitionTo(windowManager.recentlyPlayedPageWindow);
                break;
            case "DownloadedMusicPage":
                windowManager.transitionTo(windowManager.downloadedPageWindow);
                break;
            case "SettingsPage":
                windowManager.transitionTo(windowManager.settingsPageWindow);
                break;
            case "LanguagePage":
                windowManager.transitionTo(windowManager.languagePageWindow);
                break;
            case "HelpPage":
                windowManager.transitionTo(windowManager.helpPageWindow);
                break;
            default:
                // Fallback to main menu for unknown pages
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
