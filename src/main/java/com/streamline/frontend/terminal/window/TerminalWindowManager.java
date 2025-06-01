package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;

import com.streamline.audio.Playlist;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.TerminalKeybinds;
import com.streamline.frontend.terminal.page.pages.*;

import java.io.IOException;

import org.tinylog.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Manages all windows and transitions between them. Responsible for creating, showing, and closing windows.
 * @author wellatleastitried
 */
public class TerminalWindowManager {

    private static TerminalWindowManager instance;

    private final WindowBasedTextGUI textGUI;
    private final TextGUIThread guiThread;
    private final Dispatcher backend;

    private final MainPage mainPage;
    private final HelpPage helpPage;
    private final SettingsPage settingsPage;
    private final LanguagePage languagePage;
    private final SearchPage searchPage;
    private final LikedMusicPage likedMusicPage;
    private final PlaylistPage playlistPage;
    private final RecentlyPlayedPage recentlyPlayedPage;
    private final DownloadedMusicPage downloadedPage;

    private static final Map<String, AbstractBasePage> pages;

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

    /**
     * Flag to indicate if the window should be rebuilt after navigating off of the page.
     */
    public boolean rebuildSearchPageWhenDone = false;
    public boolean rebuildPlaylistPageWhenDone = false;

    static {
        pages = new java.util.HashMap<>();
    }

    private TerminalWindowManager(TerminalScreen screen, WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;

        TerminalKeybinds.applyTo(textGUI);

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
    }

    public void buildWindows() {
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
            Logger.error("[!] Error while creating windows, please restart the app.");
            System.exit(1);
        }
    }

    public static TerminalWindowManager getInstance() {
        if (instance == null) {
            Logger.debug("[!] TerminalWindowManager has not been initialized yet.");
            throw new IllegalStateException("TerminalWindowManager has not been initialized yet.");
        }
        return instance;
    }

    public static TerminalWindowManager createInstance(TerminalScreen screen, WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        if (instance == null) {
            instance = new TerminalWindowManager(screen, textGUI, guiThread, backend);
            setWindowManagerForWindows();
        }

        return instance;
    }

    private static void setWindowManagerForWindows() {
        for (AbstractBasePage page : pages.values()) {
            page.setWindowManager(instance);
        }
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow);
        songOptionPage.setWindowManager(this);
        this.songOptionPageWindow = songOptionPage.createWindow();
    }

    public <T extends AbstractBasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        SongOptionPage songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow, previousSearchResults);
        songOptionPage.setWindowManager(this);
        this.songOptionPageWindow = songOptionPage.createWindow();
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow);
        playlistChoicePage.setWindowManager(this);
        this.playlistChoicePageWindow = playlistChoicePage.createWindow();
    }

    public <T extends AbstractBasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        PlaylistChoicePage playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow, previousSearchResults);
        playlistChoicePage.setWindowManager(this);
        this.playlistChoicePageWindow = playlistChoicePage.createWindow();
    }

    public <T extends AbstractBasePage> void buildCreatePlaylistPage(T previousWindow) {
        CreatePlaylistPage createPlaylistPage = new CreatePlaylistPage(previousWindow, backend, guiThread);
        createPlaylistPage.setWindowManager(this);
        this.createPlaylistPageWindow = createPlaylistPage.createWindow();
    }

    public <T extends AbstractBasePage> void buildSongsFromPlaylistPage(Playlist playlist, T previousWindow) {
        SongsFromPlaylistPage songsFromPlaylistPage = new SongsFromPlaylistPage(backend, guiThread, playlist.getId(), playlist.getName());
        songsFromPlaylistPage.setWindowManager(this);
        this.songsFromPlaylistPageWindow = songsFromPlaylistPage.createWindow();
    }

    public void rebuildDynamicWindows() {
        guiThread.invokeLater(() -> {
            this.likedMusicPageWindow = likedMusicPage.updateWindow();
            this.playlistPageWindow = playlistPage.updateWindow();
            this.recentlyPlayedPageWindow = recentlyPlayedPage.updateWindow();
            this.downloadedPageWindow = downloadedPage.updateWindow();
        });
    }

    public void rebuildAllWindows() {
        guiThread.invokeLater(() -> {
            this.mainPageWindow = mainPage.createWindow();
            this.helpPageWindow = helpPage.createWindow();
            this.settingsPageWindow = settingsPage.createWindow();
            this.searchPageWindow = searchPage.createWindow();
            this.languagePageWindow = languagePage.createWindow();
            rebuildDynamicWindows();
        });
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        guiThread.invokeLater(() -> {
            if (this.rebuildSearchPageWhenDone) {
                this.rebuildSearchPageWhenDone = false;
                SearchPage searchPage = new SearchPage(backend, guiThread);
                searchPage.setWindowManager(this);
                this.searchPageWindow = searchPage.createWindow();
            } else {
                SearchPage searchPage = new SearchPage(backend, guiThread, searchResults);
                searchPage.setWindowManager(this);
                this.searchPageWindow = searchPage.createWindow();
            }
            assert searchPage != null;
        });
    }

    public void refresh() {
        guiThread.invokeLater(() -> {
            try {
                textGUI.getScreen().refresh();
            } catch (IOException iE) {
                Logger.error("[!] Error while redrawing screen, please restart the app.");
            }
        });
    }

    public void showMainMenu() {
        mainPageWindow.setVisible(true);
        Collection<Window> openWindows = textGUI.getWindows();
        for (Window window : openWindows) {
            if (window != mainPage) {
                textGUI.removeWindow(window);
            }
        }
        if (!openWindows.contains(mainPageWindow)) {
            textGUI.addWindowAndWait(mainPageWindow);
        }
    }

    public void transitionToCachedSearchPage() {
        this.rebuildSearchPageWhenDone = true;
        transitionTo(searchPageWindow);
    }

    public <T extends AbstractBasePage> void transitionToPlaylistChoicePage(T previousPage, Song song, Map<Integer, Button> previousSearchResults) {
        if (previousSearchResults != null) {
            buildPlaylistChoicePage(song, previousPage, previousSearchResults);
        } else {
            buildPlaylistChoicePage(song, previousPage);
        }
        transitionTo(playlistChoicePageWindow);
    }

    public void transitionTo(BasicWindow window) {
        guiThread.invokeLater(() -> {
            Collection<Window> openWindows = textGUI.getWindows();
            for (Window openWindow: openWindows) {
                if (openWindow != window) {
                    textGUI.removeWindow(openWindow);
                }
            }
            if (!openWindows.contains(window)) {
                if (isDynamicWindow(window)) {
                    rebuildDynamicWindow(window);
                }
                textGUI.addWindowAndWait(window);
            }
        });
    }

    private boolean isDynamicWindow(BasicWindow window) {
        return window.equals(likedMusicPageWindow) || window.equals(downloadedPageWindow) || window.equals(likedMusicPageWindow) || window.equals(playlistPageWindow);
    }

    private void rebuildDynamicWindow(BasicWindow window) {
        if (window.equals(likedMusicPageWindow)) {
            this.likedMusicPageWindow = likedMusicPage.updateWindow();
        } else if (window.equals(downloadedPageWindow)) {
            this.downloadedPageWindow = downloadedPage.updateWindow();
        } else if (window.equals(playlistPageWindow)) {
            this.playlistPageWindow = playlistPage.updateWindow();
        } else if (window.equals(recentlyPlayedPageWindow)) {
            this.recentlyPlayedPageWindow = recentlyPlayedPage.updateWindow();
        } else {
            Logger.debug("[!] No dynamic window to rebuild.");
        }
    }

    public void returnToMainMenu(BasicWindow currentWindow) {
        guiThread.invokeLater(() -> {
            textGUI.removeWindow(currentWindow);
            showMainMenu();
        });
    }

    private boolean verifyWindows() {
        try {
            if (mainPage == null || settingsPage == null || helpPage == null || searchPage == null || likedMusicPage == null || playlistPage == null || recentlyPlayedPage == null || downloadedPage == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void closeAllWindows() {
        guiThread.invokeLater(() -> {
            try {
                for (Window window : textGUI.getWindows()) {
                    textGUI.removeWindow(window);
                }
            } catch (IllegalStateException iE) {
                Logger.warn("[!] There was an exception while cleaning up the terminal interface:\n" + iE.getMessage());
            }
        });
    }
}
