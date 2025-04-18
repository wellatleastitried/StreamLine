package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.Pages.*;

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

    // Windows
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

    public TerminalWindowManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend, TerminalComponentFactory componentFactory) {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;
        this.componentFactory = componentFactory;

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
        this.playlistChoicePage = new PlaylistChoicePage(this, backend, guiThread, componentFactory).createWindow();
        assert playlistChoicePage != null;
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow) {
        this.songOptionPage = new SongOptionPage(this, backend, guiThread, componentFactory, song, previousWindow).createWindow();
        assert songOptionPage != null;
    }

    public <T extends BasePage> void buildSongOptionPage(Song song, T previousWindow, Map<Integer, Button> previousSearchResults) {
        this.songOptionPage = new SongOptionPage(this, backend, guiThread, componentFactory, song, previousWindow, previousSearchResults).createWindow();
        assert songOptionPage != null;
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
        this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI, searchResults).createWindow();
        assert searchPage != null;
    }

    public void showMainMenu() {
        mainPage.setVisible(true);
        Collection<Window> openWindows = textGUI.getWindows();
        if (!openWindows.contains(mainPage)) {
            textGUI.addWindowAndWait(mainPage);
        }
    }

    public void transitionTo(BasicWindow window) {
        guiThread.invokeLater(() -> {
            Collection<Window> openWindows = textGUI.getWindows();
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
