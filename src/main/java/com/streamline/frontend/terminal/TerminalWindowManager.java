package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.pages.*;

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

    public BasicWindow mainPage;
    public BasicWindow settingsPage;
    public BasicWindow helpPage;
    public BasicWindow searchPage;
    public BasicWindow recentlyPlayedPage;
    public BasicWindow downloadedPage;
    public BasicWindow playlistPage;
    public BasicWindow likedMusicPage;
    public BasicWindow languagePage;
    public BasicWindow songOptionPage;
    public BasicWindow playlistChoicePage;

    /**
     * Flag to indicate if the window should be rebuilt after navigating off of the page.
     */
    public boolean rebuildSearchPageWhenDone = false;
    public boolean rebuildPlaylistPageWhenDone = false;

    private TerminalWindowManager(TerminalScreen screen, WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) throws Exception {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;

        TerminalKeybinds.applyTo(textGUI);
    }

    public void buildWindows() {
        this.mainPage = new MainPage(backend, guiThread).createWindow();
        this.helpPage = new HelpPage(backend, guiThread).createWindow();
        this.settingsPage = new SettingsPage(backend, guiThread).createWindow();
        this.searchPage = new SearchPage(backend, guiThread, textGUI).createWindow();
        this.likedMusicPage = new LikedMusicPage(backend, guiThread, textGUI).createWindow();
        this.playlistPage = new PlaylistPage(backend, guiThread).createWindow();
        this.recentlyPlayedPage = new RecentlyPlayedPage(backend, guiThread, textGUI).createWindow();
        this.downloadedPage = new DownloadedMusicPage(backend, guiThread, textGUI).createWindow();
        this.languagePage = new LanguagePage(backend, guiThread).createWindow();
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
        }

        return instance;
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow) {
        this.songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow).createWindow();
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        this.songOptionPage = new SongOptionPage(backend, guiThread, song, previousWindow, previousSearchResults).createWindow();
    }

    public <T extends BasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        this.playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow).createWindow();
    }

    public <T extends BasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        this.playlistChoicePage = new PlaylistChoicePage(backend, guiThread, song, previousWindow, previousSearchResults).createWindow();
    }

    public void rebuildDynamicWindows() {
        guiThread.invokeLater(() -> {
            this.likedMusicPage = new LikedMusicPage(backend, guiThread, textGUI).createWindow();
            this.playlistPage = new PlaylistPage(backend, guiThread).createWindow();
            this.recentlyPlayedPage = new RecentlyPlayedPage(backend, guiThread, textGUI).createWindow();
            this.downloadedPage = new DownloadedMusicPage(backend, guiThread, textGUI).createWindow();
        });
    }

    public void rebuildAllWindows() {
        guiThread.invokeLater(() -> {
            this.mainPage = new MainPage(backend, guiThread).createWindow();
            this.helpPage = new HelpPage(backend, guiThread).createWindow();
            this.settingsPage = new SettingsPage(backend, guiThread).createWindow();
            this.searchPage = new SearchPage(backend, guiThread, textGUI).createWindow();
            this.likedMusicPage = new LikedMusicPage(backend, guiThread, textGUI).createWindow();
            this.playlistPage = new PlaylistPage(backend, guiThread).createWindow();
            this.recentlyPlayedPage = new RecentlyPlayedPage(backend, guiThread, textGUI).createWindow();
            this.downloadedPage = new DownloadedMusicPage(backend, guiThread, textGUI).createWindow();
            this.languagePage = new LanguagePage(backend, guiThread).createWindow();
        });
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        guiThread.invokeLater(() -> {
            if (this.rebuildSearchPageWhenDone) {
                this.rebuildSearchPageWhenDone = false;
                this.searchPage = new SearchPage(backend, guiThread, textGUI).createWindow();
            } else {
                this.searchPage = new SearchPage(backend, guiThread, textGUI, searchResults).createWindow();
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
        mainPage.setVisible(true);
        Collection<Window> openWindows = textGUI.getWindows();
        for (Window window : openWindows) {
            if (window != mainPage) {
                textGUI.removeWindow(window);
            }
        }
        if (!openWindows.contains(mainPage)) {
            textGUI.addWindowAndWait(mainPage);
        }
    }

    public void transitionToCachedSearchPage() {
        this.rebuildSearchPageWhenDone = true;
        transitionTo(searchPage);
    }

    public <T extends BasePage> void transitionToPlaylistChoicePage(T previousPage, Song song, Map<Integer, Button> previousSearchResults) {
        if (previousSearchResults != null) {
            buildPlaylistChoicePage(song, previousPage, previousSearchResults);
        } else {
            buildPlaylistChoicePage(song, previousPage);
        }
        transitionTo(playlistChoicePage);
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
                    rebuildDynamicWindows();
                }
                textGUI.addWindowAndWait(window);
            }
        });
    }

    private boolean isDynamicWindow(BasicWindow window) {
        return window.equals(likedMusicPage) || window.equals(downloadedPage) || window.equals(likedMusicPage) || window.equals(playlistPage);
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
