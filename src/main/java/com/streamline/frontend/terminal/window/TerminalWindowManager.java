package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;
import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.pages.*;
import org.tinylog.Logger;

import java.util.Map;
import java.util.Collection;
import java.util.Stack;

public class TerminalWindowManager {

    private final TextGUIThread guiThread;
    private final Dispatcher backend;
    private WindowBasedTextGUI textGUI;

    private final Stack<Class<?>> navigationHistory = new Stack<>();

    public final MainPage mainPage;
    public final HelpPage helpPage;
    public final SettingsPage settingsPage;
    public final LanguagePage languagePage;
    public final SearchPage searchPage;
    public final LikedMusicPage likedMusicPage;
    public final PlaylistPage playlistPage;
    public final RecentlyPlayedPage recentlyPlayedPage;
    public final DownloadedMusicPage downloadedPage;

    public BasicWindow mainPageWindow;
    public BasicWindow settingsPageWindow;
    public BasicWindow helpPageWindow;
    public BasicWindow languagePageWindow;
    public BasicWindow searchPageWindow;
    public BasicWindow recentlyPlayedPageWindow;
    public BasicWindow downloadedPageWindow;
    public BasicWindow likedMusicPageWindow;
    public BasicWindow playlistPageWindow;

    public BasicWindow songOptionPageWindow;
    public BasicWindow playlistChoicePageWindow;
    public BasicWindow createPlaylistPageWindow;
    public BasicWindow songsFromPlaylistPageWindow;

    public boolean rebuildSearchPageWhenDone = false;
    public boolean rebuildPlaylistPageWhenDone = false;

    private static TerminalWindowManager instance;

    public TerminalWindowManager(TextGUIThread guiThread, Dispatcher backend) throws Exception {
        this.guiThread = guiThread;
        this.backend = backend;

        this.mainPage = new MainPage(backend, guiThread);
        this.helpPage = new HelpPage(backend, guiThread);
        this.settingsPage = new SettingsPage(backend, guiThread);
        this.searchPage = new SearchPage(backend, guiThread);
        this.likedMusicPage = new LikedMusicPage(backend, guiThread);
        this.playlistPage = new PlaylistPage(backend, guiThread);
        this.recentlyPlayedPage = new RecentlyPlayedPage(backend, guiThread);
        this.downloadedPage = new DownloadedMusicPage(backend, guiThread);
        this.languagePage = new LanguagePage(backend, guiThread);

        Logger.debug("Initialized TerminalWindowManager");
    }

    /**
     * Build all static windows.
     */
    public void buildWindows() {
        setWindowManagerForWindows();

        this.mainPageWindow = mainPage.createWindow();
        this.helpPageWindow = helpPage.createWindow();
        this.settingsPageWindow = settingsPage.createWindow();
        this.searchPageWindow = searchPage.createWindow();
        this.likedMusicPageWindow = likedMusicPage.createWindow();
        this.playlistPageWindow = playlistPage.createWindow();
        this.recentlyPlayedPageWindow = recentlyPlayedPage.createWindow();
        this.downloadedPageWindow = downloadedPage.createWindow();
        this.languagePageWindow = languagePage.createWindow();

        if (!verifyWindows()) {
            Logger.error("Error while creating windows, please restart the app.");
            System.exit(1);
        }
        Logger.debug("Successfully built all windows");
    }

    private void setWindowManagerForWindows() {
        mainPage.setWindowManager(this);
        helpPage.setWindowManager(this);
        settingsPage.setWindowManager(this);
        searchPage.setWindowManager(this);
        likedMusicPage.setWindowManager(this);
        playlistPage.setWindowManager(this);
        recentlyPlayedPage.setWindowManager(this);
        downloadedPage.setWindowManager(this);
        languagePage.setWindowManager(this);
    }

    private boolean verifyWindows() {
        try {
            return mainPageWindow != null && settingsPageWindow != null && 
                   helpPageWindow != null && searchPageWindow != null && 
                   likedMusicPageWindow != null && playlistPageWindow != null && 
                   recentlyPlayedPageWindow != null && downloadedPageWindow != null &&
                   languagePageWindow != null;
        } catch (Exception e) {
            Logger.error("Window verification failed: {}", e.getMessage());
            return false;
        }
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow);
        songOptionPage.setWindowManager(this);
        this.songOptionPageWindow = songOptionPage.createWindow();
        Logger.debug("Built SongOptionPage for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow, previousSearchResults);
        songOptionPage.setWindowManager(this);
        this.songOptionPageWindow = songOptionPage.createWindow();
        Logger.debug("Built SongOptionPage with search results for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow);
        playlistChoicePage.setWindowManager(this);
        this.playlistChoicePageWindow = playlistChoicePage.createWindow();
        Logger.debug("Built PlaylistChoicePage for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow, previousSearchResults);
        playlistChoicePage.setWindowManager(this);
        this.playlistChoicePageWindow = playlistChoicePage.createWindow();
        Logger.debug("Built PlaylistChoicePage with search results for song: {}", song.getSongName());
    }

    public <T extends AbstractBasePage> void buildCreatePlaylistPage(T previousWindow) {
        CreatePlaylistPage createPlaylistPage = new CreatePlaylistPage(previousWindow, backend, guiThread);
        createPlaylistPage.setWindowManager(this);
        this.createPlaylistPageWindow = createPlaylistPage.createWindow();
        Logger.debug("Built CreatePlaylistPage");
    }

    public <T extends AbstractBasePage> void buildSongsFromPlaylistPage(Playlist playlist, T previousWindow) {
        SongsFromPlaylistPage songsFromPlaylistPage = new SongsFromPlaylistPage(backend, guiThread, playlist.getId(), playlist.getName());
        songsFromPlaylistPage.setWindowManager(this);
        this.songsFromPlaylistPageWindow = songsFromPlaylistPage.createWindow();
        Logger.debug("Built SongsFromPlaylistPage for playlist: {}", playlist.getName());
    }

    public void navigateBack(AbstractBasePage currentPage) {
        // TODO: Might need to handle navigating to/from dynamic pages
        navigateBack();
    }

    public void navigateBack() {
        Logger.debug("Navigating back");
        
        if (!navigationHistory.isEmpty()) {
            navigationHistory.pop(); // Remove current page
        }
        
        if (!navigationHistory.isEmpty()) {
            Class<?> previousPage = navigationHistory.peek();
            Logger.debug("Going back to {}", previousPage.getSimpleName());
            transitionToPage(previousPage);
        } else {
            Logger.debug("No navigation history, returning to main menu");
            showMainMenu();
        }
    }

    public void navigateToPage(Class<?> pageClass) {
        Logger.debug("Navigating to {}", pageClass.getSimpleName());
        navigationHistory.push(pageClass);
        transitionToPage(pageClass);
    }

    public void returnToMainMenu() {
        Logger.debug("Returning to main menu");
        navigationHistory.clear();
        showMainMenu();
    }

    public void returnToMainMenu(BasicWindow currentWindow) {
        returnToMainMenu();
    }

    public boolean canNavigateBack() {
        return navigationHistory.size() > 1;
    }

    public void transitionToPage(Class<?> pageClass) {
        BasicWindow targetWindow = getWindowForPageClass(pageClass);
        if (targetWindow != null) {
            transitionTo(targetWindow);
        } else {
            Logger.warn("No window found for page class: {}", pageClass.getSimpleName());
        }
    }

    private BasicWindow getWindowForPageClass(Class<?> pageClass) {
        if (pageClass == MainPage.class) return mainPageWindow;
        if (pageClass == HelpPage.class) return helpPageWindow;
        if (pageClass == SettingsPage.class) return settingsPageWindow;
        if (pageClass == LanguagePage.class) return languagePageWindow;
        if (pageClass == SearchPage.class) return searchPageWindow;
        if (pageClass == LikedMusicPage.class) return likedMusicPageWindow;
        if (pageClass == PlaylistPage.class) return playlistPageWindow;
        if (pageClass == RecentlyPlayedPage.class) return recentlyPlayedPageWindow;
        if (pageClass == DownloadedMusicPage.class) return downloadedPageWindow;
        return null;
    }

    public void transitionTo(BasicWindow targetWindow) {
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
    }

    public void showMainMenu() {
        transitionTo(mainPageWindow);
    }

    public void transitionToCachedSearchPage() {
        transitionTo(searchPageWindow);
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        this.searchPageWindow = searchPage.createWindow();
        Logger.debug("Rebuilt search page with {} search results", searchResults.size());
    }

    public void rebuildDynamicWindows() {
        Logger.debug("rebuildDynamicWindows called - rebuilding all dynamic windows");
        this.searchPageWindow = searchPage.createWindow();
        this.playlistPageWindow = playlistPage.createWindow();
        this.likedMusicPageWindow = likedMusicPage.createWindow();
        this.recentlyPlayedPageWindow = recentlyPlayedPage.createWindow();
        this.downloadedPageWindow = downloadedPage.createWindow();
        Logger.debug("Rebuilt all dynamic windows");
    }

    public void rebuildDynamicPages() {
        rebuildDynamicWindows();
    }

    public void rebuildDirtyWindows() {
        rebuildDynamicWindows();
    }

    public void rebuildAllWindows() {
        this.mainPageWindow = mainPage.createWindow();
        this.helpPageWindow = helpPage.createWindow();
        this.settingsPageWindow = settingsPage.createWindow();
        this.searchPageWindow = searchPage.createWindow();
        this.languagePageWindow = languagePage.createWindow();
        
        rebuildDynamicWindows();
        
        Logger.debug("Rebuilt all windows");
    }

    public <T extends AbstractBasePage> void transitionToPlaylistChoicePage(T previousPage, Song song, Map<Integer, Button> previousSearchResults) {
        if (previousSearchResults != null) {
            buildPlaylistChoicePage(song, previousPage, previousSearchResults);
        } else {
            buildPlaylistChoicePage(song, previousPage);
        }
        transitionTo(playlistChoicePageWindow);
    }

    public BasicWindow getMainPageWindow() { return mainPageWindow; }
    public BasicWindow getSettingsPageWindow() { return settingsPageWindow; }
    public BasicWindow getHelpPageWindow() { return helpPageWindow; }
    public BasicWindow getLanguagePageWindow() { return languagePageWindow; }
    public BasicWindow getSearchPageWindow() { return searchPageWindow; }
    public BasicWindow getRecentlyPlayedPageWindow() { return recentlyPlayedPageWindow; }
    public BasicWindow getDownloadedPageWindow() { return downloadedPageWindow; }
    public BasicWindow getLikedMusicPageWindow() { return likedMusicPageWindow; }
    public BasicWindow getPlaylistPageWindow() { return playlistPageWindow; }
    public BasicWindow getSongOptionPageWindow() { return songOptionPageWindow; }
    public BasicWindow getPlaylistChoicePageWindow() { return playlistChoicePageWindow; }
    public BasicWindow getCreatePlaylistPageWindow() { return createPlaylistPageWindow; }
    public BasicWindow getSongsFromPlaylistPageWindow() { return songsFromPlaylistPageWindow; }

    public void markWindowAsDirty(Class<?> pageClass, AbstractDynamicPage instance) {
        Logger.debug("Marked window dirty: {}", pageClass.getSimpleName());
        // TODO: the window needs rebuilding
    }

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
        
        /* Clear all window references */
        this.mainPageWindow = null;
        this.helpPageWindow = null;
        this.settingsPageWindow = null;
        this.searchPageWindow = null;
        this.likedMusicPageWindow = null;
        this.playlistPageWindow = null;
        this.recentlyPlayedPageWindow = null;
        this.downloadedPageWindow = null;
        this.languagePageWindow = null;
        this.songOptionPageWindow = null;
        this.playlistChoicePageWindow = null;
        this.createPlaylistPageWindow = null;
        this.songsFromPlaylistPageWindow = null;
    }

    public static TerminalWindowManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TerminalWindowManager has not been initialized yet.");
        }
        return instance;
    }

    public static TerminalWindowManager createInstance(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        if (instance == null) {
            instance = new TerminalWindowManager(guiThread, backend);
        }
        return instance;
    }
}
