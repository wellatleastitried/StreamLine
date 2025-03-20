package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.internal.StreamLineMessages;
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
    protected final BasicWindow mainMenu;
    protected final BasicWindow settingsMenu;
    protected final BasicWindow helpMenu;
    protected final BasicWindow searchPage;
    protected final BasicWindow recentlyPlayedPage;
    protected final BasicWindow downloadedPage;
    protected final BasicWindow playlistPage;
    protected final BasicWindow likedMusicPage;

    public TerminalWindowManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend, TerminalComponentFactory componentFactory) {
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;
        this.componentFactory = componentFactory;

        // Initialize all windows
        this.mainMenu = new MainMenuWindow(this, backend, guiThread, componentFactory).createWindow();
        this.helpMenu = new HelpMenuWindow(this, backend, guiThread, componentFactory).createWindow();
        this.settingsMenu = new SettingsMenuWindow(this, backend, guiThread, componentFactory).createWindow();
        this.searchPage = new SearchPageWindow(this, backend, guiThread, componentFactory, textGUI).createWindow();
        this.likedMusicPage = new LikedMusicWindow(this, backend, guiThread, componentFactory).createWindow();
        this.playlistPage = new PlaylistWindow(this, backend, guiThread, componentFactory).createWindow();
        this.recentlyPlayedPage = new RecentlyPlayedWindow(this, backend, guiThread, componentFactory).createWindow();
        this.downloadedPage = new DownloadedMusicWindow(this, backend, guiThread, componentFactory).createWindow();
    }

    public void showMainMenu() {
        mainMenu.setVisible(true);
        Collection<Window> openWindows = textGUI.getWindows();
        if (!openWindows.contains(mainMenu)) {
            textGUI.addWindowAndWait(mainMenu);
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
                Logger.warn(StreamLineMessages.IllegalStateExceptionInShutdown.getMessage() + iE.getMessage());
            }
        });
    }
}
