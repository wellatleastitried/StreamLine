package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.pages.*;
import com.streamline.frontend.terminal.page.PageState;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import org.tinylog.Logger;

import java.util.Map;
import java.util.HashMap;

public class TerminalWindowManager {

    private final TextGUIThread guiThread;
    private final Dispatcher backend;

    private final TerminalWindowStateManager stateManager;
    private final TerminalWindowLifecycleManager lifecycleManager;
    private final TerminalWindowNavigationManager navigationManager;

    private final MainPage mainPage;
    private final HelpPage helpPage;
    private final SettingsPage settingsPage;
    private final LanguagePage languagePage;
    private final SearchPage searchPage;
    private final LikedMusicPage likedMusicPage;
    private final PlaylistPage playlistPage;
    private final RecentlyPlayedPage recentlyPlayedPage;
    private final DownloadedMusicPage downloadedPage;

    private static final Map<String, AbstractBasePage> pages = new HashMap<>();
    
    private final Map<Class<? extends AbstractBasePage>, PageState> pageStates = new HashMap<>();

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

    public TerminalWindowManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        this.guiThread = guiThread;
        this.backend = backend;

        this.stateManager = new TerminalWindowStateManager();
        this.lifecycleManager = new TerminalWindowLifecycleManager(textGUI, guiThread, backend);
        this.navigationManager = new TerminalWindowNavigationManager(lifecycleManager);

        this.mainPage = new MainPage(backend, guiThread);
        pages.put("mainPage", mainPage);
        this.helpPage = new HelpPage(backend, guiThread);
        pages.put("helpPage", helpPage);
        this.settingsPage = new SettingsPage(backend, guiThread);
        pages.put("settingsPage", settingsPage);
        this.searchPage = new SearchPage(backend, guiThread);
        pages.put("searchPage", searchPage);
        this.likedMusicPage = new LikedMusicPage(backend, guiThread);
        pages.put("likedMusicPage", likedMusicPage);
        this.playlistPage = new PlaylistPage(backend, guiThread);
        pages.put("playlistPage", playlistPage);
        this.recentlyPlayedPage = new RecentlyPlayedPage(backend, guiThread);
        pages.put("recentlyPlayedPage", recentlyPlayedPage);
        this.downloadedPage = new DownloadedMusicPage(backend, guiThread);
        pages.put("downloadedPage", downloadedPage);
        this.languagePage = new LanguagePage(backend, guiThread);
        pages.put("languagePage", languagePage);

        initializePageStates();

        Logger.debug("Initialized NewTerminalWindowManager with enhanced navigation and state management");
    }

    /**
     * Initialize page states for all managed pages.
     */
    private void initializePageStates() {
        pageStates.put(MainPage.class, new PageState());
        pageStates.put(HelpPage.class, new PageState());
        pageStates.put(SettingsPage.class, new PageState());
        pageStates.put(LanguagePage.class, new PageState());
        pageStates.put(SearchPage.class, new PageState());
        pageStates.put(LikedMusicPage.class, new PageState());
        pageStates.put(PlaylistPage.class, new PageState());
        pageStates.put(RecentlyPlayedPage.class, new PageState());
        pageStates.put(DownloadedMusicPage.class, new PageState());
        
        Logger.debug("Initialized page states for all managed pages");
    }

    /**
     * Build all static windows using the lifecycle manager.
     */
    public void buildWindows() {
        setWindowManagerForWindows();

        this.mainPageWindow = lifecycleManager.createWindow(MainPage.class, mainPage);
        this.helpPageWindow = lifecycleManager.createWindow(HelpPage.class, helpPage);
        this.settingsPageWindow = lifecycleManager.createWindow(SettingsPage.class, settingsPage);
        this.searchPageWindow = lifecycleManager.createWindow(SearchPage.class, searchPage);
        this.likedMusicPageWindow = lifecycleManager.createWindow(LikedMusicPage.class, likedMusicPage);
        this.playlistPageWindow = lifecycleManager.createWindow(PlaylistPage.class, playlistPage);
        this.recentlyPlayedPageWindow = lifecycleManager.createWindow(RecentlyPlayedPage.class, recentlyPlayedPage);
        this.downloadedPageWindow = lifecycleManager.createWindow(DownloadedMusicPage.class, downloadedPage);
        this.languagePageWindow = lifecycleManager.createWindow(LanguagePage.class, languagePage);

        if (!verifyWindows()) {
            Logger.error("Error while creating windows, please restart the app.");
            System.exit(1);
        }
        Logger.debug("Successfully built all windows");
    }

    private void setWindowManagerForWindows() {
        for (AbstractBasePage page : pages.values()) {
            page.setWindowManager(this);
        }
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

    public void navigateTo(NavigationContext context) {
        Logger.debug("Navigation to destination: {} with context", context.getDestination());
        navigationManager.navigateToDestination(context.getDestination(), context);
    }

    public void navigateBack(AbstractBasePage currentPage) {
        navigationManager.navigateBack();
    }

    public void navigateToPage(Class<?> pageClass) {
        navigationManager.navigateTo(pageClass);
    }

    public void returnToMainMenu() {
        navigationManager.returnToMainMenu();
    }

    public boolean canNavigateBack() {
        return navigationManager.canNavigateBack();
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        stateManager.markWindowDirty(SearchPage.class);
        lifecycleManager.rebuildWindow(SearchPage.class);
        Logger.debug("Rebuilt search page with {} search results", searchResults.size());
    }

    public void showMainMenu() {
        lifecycleManager.showMainMenu();
    }

    public void transitionToCachedSearchPage() {
        lifecycleManager.transitionTo(SearchPage.class);
    }

    public void rebuildDynamicWindows() {
        Logger.debug("rebuildDynamicWindows called - rebuilding all dynamic windows");
        stateManager.rebuildDirtyWindows();
        lifecycleManager.rebuildDynamicWindow(SearchPage.class);
        lifecycleManager.rebuildDynamicWindow(PlaylistPage.class);
        
        // lifecycleManager.rebuildDynamicWindow(LikedMusicPage.class);
        Logger.debug("Rebuilding LikedMusicPage");
        BasicWindow likedMusicWindow = lifecycleManager.rebuildDynamicWindow(LikedMusicPage.class);
        if (likedMusicWindow != null) this.likedMusicPageWindow = likedMusicWindow;

        lifecycleManager.rebuildDynamicWindow(RecentlyPlayedPage.class);
        lifecycleManager.rebuildDynamicWindow(DownloadedMusicPage.class);
        Logger.debug("Rebuilt all dynamic windows");
    }

    public void rebuildAllWindows() {
        this.mainPageWindow = lifecycleManager.createWindow(MainPage.class, mainPage);
        this.helpPageWindow = lifecycleManager.createWindow(HelpPage.class, helpPage);
        this.settingsPageWindow = lifecycleManager.createWindow(SettingsPage.class, settingsPage);
        this.searchPageWindow = lifecycleManager.createWindow(SearchPage.class, searchPage);
        this.languagePageWindow = lifecycleManager.createWindow(LanguagePage.class, languagePage);
        
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

    public void transitionTo(BasicWindow window) {
        lifecycleManager.transitionTo(window);
    }

    public void returnToMainMenu(BasicWindow currentWindow) {
        Logger.debug("returnToMainMenu called with currentWindow: {}", currentWindow != null ? currentWindow.getClass().getSimpleName() : "null");
        BasicWindow mainWindow = this.mainPageWindow;
        if (mainWindow == null) {
            Logger.debug("mainPageWindow is null, getting from lifecycleManager");
            mainWindow = lifecycleManager.getWindow(MainPage.class);
        }
        
        if (mainWindow != null) {
            Logger.debug("Calling lifecycleManager.returnToMainMenu with currentWindow and mainWindow");
            lifecycleManager.returnToMainMenu(currentWindow, mainWindow);
        } else {
            Logger.warn("Main window not available for returnToMainMenu, attempting fallback");
            guiThread.invokeLater(() -> {
                if (this.mainPageWindow != null) {
                    Logger.debug("Fallback: transitioning to mainPageWindow");
                    lifecycleManager.transitionTo(this.mainPageWindow);
                } else {
                    Logger.error("No main window available for transition");
                    lifecycleManager.showMainMenu();
                }
            });
        }
    }

    public void refresh() {
        lifecycleManager.refresh();
    }

    public void closeAllWindows() {
        lifecycleManager.closeAllWindows();
        
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
        
        Logger.debug("Closed all windows");
    }

    public static TerminalWindowManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NewTerminalWindowManager has not been initialized yet.");
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
