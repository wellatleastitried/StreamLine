package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.Pages.*;

import org.tinylog.Logger;

import java.util.Collection;

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

    public TerminalWindowManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend, TerminalComponentFactory componentFactory) {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;
        this.componentFactory = componentFactory;

        // Initialize all windows
        this.mainPage = new MainPage(this, backend, guiThread, componentFactory).createWindow();
        this.helpPage = new HelpPage(this, backend, guiThread, componentFactory).createWindow();
        this.settingsPage = new SettingsPage(this, backend, guiThread, componentFactory).createWindow();
        this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
        this.likedMusicPage = new LikedMusicPage(this, backend, guiThread, componentFactory).createWindow();
        this.playlistPage = new PlaylistPage(this, backend, guiThread, componentFactory).createWindow();
        this.recentlyPlayedPage = new RecentlyPlayedPage(this, backend, guiThread, componentFactory).createWindow();
        this.downloadedPage = new DownloadedMusicPage(this, backend, guiThread, componentFactory).createWindow();
        this.languagePage = new LanguagePage(this, backend, guiThread, componentFactory).createWindow();
    }

    public void rebuildWindows() {
        guiThread.invokeLater(() -> {
            this.mainPage = new MainPage(this, backend, guiThread, componentFactory).createWindow();
            this.helpPage = new HelpPage(this, backend, guiThread, componentFactory).createWindow();
            this.settingsPage = new SettingsPage(this, backend, guiThread, componentFactory).createWindow();
            this.searchPage = new SearchPage(this, backend, guiThread, componentFactory, textGUI).createWindow();
            this.likedMusicPage = new LikedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            this.playlistPage = new PlaylistPage(this, backend, guiThread, componentFactory).createWindow();
            this.recentlyPlayedPage = new RecentlyPlayedPage(this, backend, guiThread, componentFactory).createWindow();
            this.downloadedPage = new DownloadedMusicPage(this, backend, guiThread, componentFactory).createWindow();
            this.languagePage = new LanguagePage(this, backend, guiThread, componentFactory).createWindow();
        });
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
                textGUI.addWindowAndWait(window);
            }
        });
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
