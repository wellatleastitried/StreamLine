package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;

import java.util.Map;

/**
 * New window manager that integrates with the navigation command system.
 * This bridges the gap between the old TerminalWindowManager and the new navigation system.
 */
public class NewTerminalWindowManager {

    private final TerminalWindowManager legacyWindowManager;
    private final TerminalWindowStateManager stateManager;
    private final TerminalWindowNavigationManager navigationManager;

    public NewTerminalWindowManager(TerminalWindowManager legacyWindowManager) {
        this.legacyWindowManager = legacyWindowManager;
        this.stateManager = new TerminalWindowStateManager();
        this.navigationManager = new TerminalWindowNavigationManager(legacyWindowManager);
    }

    public void navigateTo(NavigationDestination destination) {
        // Use the navigation manager for destination-based navigation
        NavigationContext context = new NavigationContext(null, null);
        context.setContextData("directDestination", destination);
        navigationManager.navigateToDestination(destination, context);
    }

    public void navigateTo(NavigationDestination destination, NavigationContext context) {
        navigationManager.navigateToDestination(destination, context);
    }

    public void navigateBack(AbstractBasePage currentPage) {
        // Use the navigation manager for back navigation
        navigationManager.navigateBack();
    }

    // Navigation convenience methods
    public void navigateToPage(Class<?> pageClass) {
        navigationManager.navigateTo(pageClass);
    }

    public void returnToMainMenu() {
        navigationManager.returnToMainMenu();
    }

    public boolean canNavigateBack() {
        return navigationManager.canNavigateBack();
    }

    // Window building methods delegated to legacy manager
    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow) {
        legacyWindowManager.buildSongOptionPage(song, previousWindow);
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        legacyWindowManager.buildSongOptionPage(song, previousWindow, previousSearchResults);
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        legacyWindowManager.buildPlaylistChoicePage(song, previousWindow);
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        legacyWindowManager.buildPlaylistChoicePage(song, previousWindow, previousSearchResults);
    }

    public <T extends AbstractBasePage> void buildCreatePlaylistPage(T previousWindow) {
        legacyWindowManager.buildCreatePlaylistPage(previousWindow);
    }

    public <T extends AbstractBasePage> void buildSongsFromPlaylistPage(Playlist playlist, T previousWindow) {
        legacyWindowManager.buildSongsFromPlaylistPage(playlist, previousWindow);
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        legacyWindowManager.rebuildSearchPage(searchResults);
    }

    public void showMainMenu() {
        legacyWindowManager.showMainMenu();
    }

    public void transitionToCachedSearchPage() {
        legacyWindowManager.transitionToCachedSearchPage();
    }

    public void rebuildDynamicWindows() {
        stateManager.rebuildDirtyWindows();
        legacyWindowManager.rebuildDynamicWindows();
    }

    public void markWindowDirty(Class<?> pageClass) {
        stateManager.markWindowDirty(pageClass);
    }

    // Delegate common operations to legacy manager
    public void transitionTo(BasicWindow window) {
        legacyWindowManager.transitionTo(window);
    }

    public void returnToMainMenu(BasicWindow currentWindow) {
        legacyWindowManager.returnToMainMenu(currentWindow);
    }

    public void refresh() {
        legacyWindowManager.refresh();
    }

    // Provide access to legacy manager for gradual migration
    public TerminalWindowManager getLegacyManager() {
        return legacyWindowManager;
    }
}
