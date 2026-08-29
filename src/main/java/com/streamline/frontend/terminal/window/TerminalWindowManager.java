package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.Page;
import com.streamline.frontend.terminal.page.pages.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.tinylog.Logger;


public class TerminalWindowManager {

    private final TextGUIThread guiThread;
    private final Dispatcher backend;
    private final WindowBasedTextGUI textGUI;

    private final Stack<String> navigationHistory = new Stack<>();

    private final Map<String, AbstractBasePage> pages;

    private final Map<AbstractBasePage, BasicWindow> windows;

    public BasicWindow songOptionPageWindow;
    public BasicWindow playlistChoicePageWindow;
    public BasicWindow createPlaylistPageWindow;
    public BasicWindow songsFromPlaylistPageWindow;

    public boolean rebuildSearchPageWhenDone = false;
    public boolean rebuildPlaylistPageWhenDone = false;

    private static TerminalWindowManager instance;

    public TerminalWindowManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;

        this.pages = new HashMap<>();
        this.pages.putAll(buildMapOfPage());

        this.windows = new HashMap<>();
        Logger.debug("Initialized TerminalWindowManager");
    }

    private Map<String, AbstractBasePage> buildMapOfPage() {
        return Map.of(
            Page.MAIN_MENU, new MainPage(backend, guiThread),
            Page.HELP, new HelpPage(backend, guiThread),
            Page.SETTINGS, new SettingsPage(backend, guiThread),
            Page.LANGUAGE, new LanguagePage(backend, guiThread),
            Page.SEARCH, new SearchPage(backend, guiThread),
            Page.LIKED_MUSIC, new LikedMusicPage(backend, guiThread),
            Page.PLAYLISTS, new PlaylistPage(backend, guiThread),
            Page.RECENTLY_PLAYED, new RecentlyPlayedPage(backend, guiThread),
            Page.DOWNLOADED_MUSIC, new DownloadedMusicPage(backend, guiThread)
        );
    }

    /**
     * Build all static windows.
     */
    public void buildWindows() {
        for (AbstractBasePage page : pages.values()) {
            page.setWindowManager(this);
            windows.put(page, page.createWindow());
        }

        if (!verifyWindows()) {
            Logger.error("Error while creating windows, please restart the app.");
            System.exit(1);
        }
        Logger.debug("Successfully built all windows");
    }

    private boolean verifyWindows() {
        for (AbstractBasePage page : pages.values()) {
            if (!page.hasWindowManager()) {
                Logger.error("Window manager is not set for page: {}", page.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

    public BasicWindow getMainMenuWindow() {
        return windows.get(pages.get(Page.MAIN_MENU));
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow);
        songOptionPage.setWindowManager(this);
        songOptionPageWindow = songOptionPage.createWindow();
        Logger.debug("Built SongOptionPage for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow, previousSearchResults);
        songOptionPage.setWindowManager(this);
        songOptionPageWindow = songOptionPage.createWindow();
        Logger.debug("Built SongOptionPage with search results for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow);
        playlistChoicePage.setWindowManager(this);
        playlistChoicePageWindow = playlistChoicePage.createWindow();
        Logger.debug("Built PlaylistChoicePage for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow, previousSearchResults);
        playlistChoicePage.setWindowManager(this);
        playlistChoicePageWindow = playlistChoicePage.createWindow();
        Logger.debug("Built PlaylistChoicePage with search results for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildCreatePlaylistPage(T previousWindow) {
        CreatePlaylistPage createPlaylistPage = new CreatePlaylistPage(previousWindow, backend, guiThread);
        createPlaylistPage.setWindowManager(this);
        createPlaylistPageWindow = createPlaylistPage.createWindow();
        Logger.debug("Built CreatePlaylistPage");
    }

    public <T extends AbstractBasePage> void buildSongsFromPlaylistPage(Playlist playlist, T previousWindow) {
        SongsFromPlaylistPage songsFromPlaylistPage = new SongsFromPlaylistPage(backend, guiThread, playlist.getId(), playlist.getName());
        songsFromPlaylistPage.setWindowManager(this);
        songsFromPlaylistPageWindow = songsFromPlaylistPage.createWindow();
        Logger.debug("Built SongsFromPlaylistPage for playlist: {}", playlist.getName());
    }

    public void triggerPageRebuild() {
        Logger.debug("Page rebuild triggered.");
        pages.clear();
        pages.putAll(buildMapOfPage());
        windows.clear();
        buildWindows();
    }

    public void navigateBack(AbstractBasePage currentPage) {
        if (currentPage instanceof AbstractDynamicPage) {
            rebuildDynamicWindows();
        }
        navigateBack();
    }

    public void navigateBack() {
        if (!navigationHistory.isEmpty()) {
            String previousPageName = navigationHistory.pop();
            Logger.debug("Going back to {}", previousPageName);
            transitionToPage(previousPageName);
        } else {
            Logger.debug("No navigation history, returning to main menu");
            showMainMenu();
        }
    }

    public void navigateToPage(String currentPageName, String targetPageName) {
        Logger.debug("Navigating to {}", targetPageName);
        navigationHistory.push(currentPageName);
        transitionToPage(targetPageName);
    }

    public void returnToMainMenu() {
        Logger.debug("Returning to main menu");
        navigationHistory.clear();
        showMainMenu();
    }

    public boolean canNavigateBack() {
        return navigationHistory.size() > 1;
    }

    public void transitionToPage(String pageName) {
        BasicWindow targetWindow = getWindowForPageName(pageName);
        if (targetWindow != null) {
            transitionTo(targetWindow);
        } else {
            Logger.warn("No window found for page name: {}", pageName);
        }
    }

    private BasicWindow getWindowForPageName(String pageName) {
        if (pages.get(pageName) instanceof AbstractDynamicPage) {
            rebuildDynamicWindows();
        }
        return windows.get(pages.get(pageName));
    }

    public void transitionTo(String pageName) {
        Logger.debug("Transitioning to page: {}", pageName);
        BasicWindow targetWindow = getWindowForPageName(pageName);
        if (targetWindow != null) {
            transitionTo(targetWindow);
        } else {
            Logger.warn("No window found for page name: {}", pageName);
        }
    }

    public void transitionTo(BasicWindow targetWindow) {
        try {
            guiThread.invokeLater(() -> {
                Collection<Window> openWindows = textGUI.getWindows();
                for (Window openWindow : openWindows) {
                    if (openWindow != targetWindow) {
                        textGUI.removeWindow(openWindow);
                    }
                }
                if (!openWindows.contains(targetWindow)) {
                    textGUI.addWindowAndWait(targetWindow);
                }
            });
        } catch (Exception e) {
            Logger.debug("Error transitioning to window: {}", e.getMessage());
        }
    }

    public void showMainMenu() {
        transitionTo(windows.get(pages.get(Page.MAIN_MENU)));
    }

    public void transitionToCachedSearchPage() {
        transitionTo(windows.get(pages.get(Page.SEARCH)));
    }

    public void rebuildPage(String pageName) {
        Logger.debug("Rebuilding page: {}", pageName);
        AbstractBasePage page = pages.get(pageName);
        if (page != null) {
            windows.put(page, page.createWindow());
            Logger.debug("Rebuilt page: {}", pageName);
        } else {
            Logger.warn("No page found with name: {}", pageName);
        }
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        windows.put(pages.get(Page.SEARCH), pages.get(Page.SEARCH).createWindow());
        Logger.debug("Rebuilt search page with {} search results", searchResults.size());
    }

    public void rebuildDynamicWindows() {
        Logger.debug("rebuildDynamicWindows called - rebuilding all dynamic windows");
        for (AbstractBasePage page : pages.values()) {
            if (page instanceof AbstractDynamicPage) {
                windows.put(page, page.createWindow());
            }
        }

        Logger.debug("Rebuilt all dynamic windows");
    }

    public <T extends AbstractBasePage> void transitionToPlaylistChoicePage(T previousPage, Song song, Map<Integer, Button> previousSearchResults) {
        if (previousSearchResults != null) {
            buildPlaylistChoicePage(song, previousPage, previousSearchResults);
        } else {
            buildPlaylistChoicePage(song, previousPage);
        }
        transitionTo(playlistChoicePageWindow);
    }

    public BasicWindow getSongOptionPageWindow() { return songOptionPageWindow; }
    public BasicWindow getPlaylistChoicePageWindow() { return playlistChoicePageWindow; }
    public BasicWindow getCreatePlaylistPageWindow() { return createPlaylistPageWindow; }
    public BasicWindow getSongsFromPlaylistPageWindow() { return songsFromPlaylistPageWindow; }

    public void refresh() {
        guiThread.invokeLater(() -> {
            try {
                textGUI.getScreen().refresh();
            } catch (Exception e) {
                Logger.error("Error while refreshing screen: {}", e.getMessage());
            }
        });
    }

    public void closeAllWindows() {
        guiThread.invokeLater(() -> {
            try {
                Collection<Window> openWindows = textGUI.getWindows();
                for (Window window : openWindows) {
                    textGUI.removeWindow(window);
                }
                Logger.debug("Closed all windows");
            } catch (Exception e) {
                Logger.warn("Exception while closing windows: {}", e.getMessage());
            }
        });
    }

    public static TerminalWindowManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TerminalWindowManager has not been initialized yet.");
        }
        return instance;
    }

    public static TerminalWindowManager createInstance(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        if (instance == null) {
            instance = new TerminalWindowManager(textGUI, guiThread, backend);
        }
        return instance;
    }
}
