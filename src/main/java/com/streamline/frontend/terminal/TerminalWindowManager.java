package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.Pages.*;

import java.io.IOException;

import org.tinylog.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Manages all windows and transitions between them. Responsible for creating, showing, and closing windows.
 * @author wellatleastitried
 */
public class TerminalWindowManager {

    private final WindowBasedTextGUI textGUI;
    private final TextGUIThread guiThread;
    private final Dispatcher backend;
    private final TerminalComponentFactory componentFactory;

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

    private final TerminalKeybinds vimKeyBindings;

    /**
     * Flag to indicate if the window should be rebuilt after navigating off of the page.
     */
    public boolean rebuildSearchPageWhenDone = false;
    public boolean rebuildPlaylistPageWhenDone = false;

    public TerminalWindowManager(TerminalScreen screen, WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend, TerminalComponentFactory componentFactory) throws Exception {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;
        this.componentFactory = componentFactory;

        this.vimKeyBindings = new TerminalKeybinds(textGUI);

        // Initialize all windows
        this.mainPage = new MainPage(this, backend, guiThread, componentFactory).createWindow();
        assert mainPage != null;
        this.helpPage = new HelpPage(this, backend, guiThread, componentFactory).createWindow();
        assert helpPage != null;
        this.settingsPage = new SettingsPage(this, backend, guiThread, componentFactory).createWindow();
        assert settingsPage != null;
        this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
        assert searchPage != null;
        this.likedMusicPage = new LikedMusicPage(this, backend, guiThread, componentFactory).createWindow();
        assert likedMusicPage != null;
        this.playlistPage = new PlaylistPage(this, backend, guiThread, componentFactory).createWindow();
        assert playlistPage != null;
        this.recentlyPlayedPage = new RecentlyPlayedPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
        assert recentlyPlayedPage != null;
        this.downloadedPage = new DownloadedMusicPage(this, backend, guiThread, componentFactory).createWindow();
        assert downloadedPage != null;
        this.languagePage = new LanguagePage(this, backend, guiThread, componentFactory).createWindow();
        assert languagePage != null;
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow) {
        this.songOptionPage = new SongOptionPage(this, backend, guiThread, componentFactory, song, previousWindow).createWindow();
        assert songOptionPage != null;
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        this.songOptionPage = new SongOptionPage(this, backend, guiThread, componentFactory, song, previousWindow, previousSearchResults).createWindow();
        assert songOptionPage != null;
    }

    public <T extends BasePage> void buildPlaylistChoicePage(Song song, T previousWindow) {
        this.playlistChoicePage = new PlaylistChoicePage(this, backend, guiThread, componentFactory, song, previousWindow).createWindow();
        assert playlistChoicePage != null;
    }

    public <T extends BasePage> void buildPlaylistChoicePage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        this.playlistChoicePage = new PlaylistChoicePage(this, backend, guiThread, componentFactory, song, previousWindow, previousSearchResults).createWindow();
        assert playlistChoicePage != null;
    }

    public void rebuildDynamicWindows() {
        guiThread.invokeLater(() -> {
            this.likedMusicPage = new LikedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            assert likedMusicPage != null;
            this.playlistPage = new PlaylistPage(this, backend, guiThread, componentFactory).createWindow();
            assert playlistPage != null;
            this.recentlyPlayedPage = new RecentlyPlayedPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
            assert recentlyPlayedPage != null;
            this.downloadedPage = new DownloadedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            assert downloadedPage != null;
        });
    }

    public void rebuildAllWindows() {
        guiThread.invokeLater(() -> {
            this.mainPage = new MainPage(this, backend, guiThread, componentFactory).createWindow();
            assert mainPage != null;
            this.helpPage = new HelpPage(this, backend, guiThread, componentFactory).createWindow();
            assert helpPage != null;
            this.settingsPage = new SettingsPage(this, backend, guiThread, componentFactory).createWindow();
            assert settingsPage != null;
            this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
            assert searchPage != null;
            this.likedMusicPage = new LikedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            assert likedMusicPage != null;
            this.playlistPage = new PlaylistPage(this, backend, guiThread, componentFactory).createWindow();
            assert playlistPage != null;
            this.recentlyPlayedPage = new RecentlyPlayedPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
            assert recentlyPlayedPage != null;
            this.downloadedPage = new DownloadedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            assert downloadedPage != null;
            this.languagePage = new LanguagePage(this, backend, guiThread, componentFactory).createWindow();
            assert languagePage != null;
        });
    }

    public void rebuildSearchPage(Map<Integer, Button> searchResults) {
        guiThread.invokeLater(() -> {
            if (this.rebuildSearchPageWhenDone) {
                this.rebuildSearchPageWhenDone = false;
                this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
            } else {
                this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI, searchResults).createWindow();
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
