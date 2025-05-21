package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.TextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;

import org.tinylog.Logger;

public class CreatePlaylistPage extends AbstractDynamicPage {

    private BasicWindow window;

    private final TextGUI textGUI;
    private final AbstractBasePage previousPage;

    private String playlistName;
    private boolean enableConfirmationButtons = false;

    private Song cachedSong = null;
    private AbstractBasePage cachedPage = null;

    public <T extends AbstractBasePage> CreatePlaylistPage(T previousPage, Dispatcher backend, TextGUIThread guiThread, WindowBasedTextGUI textGUI) {
        super(backend, guiThread);
        this.previousPage = previousPage;
        this.textGUI = textGUI;
    }

    public <T extends AbstractBasePage> CreatePlaylistPage(T previousPage, Dispatcher backend, TextGUIThread guiThread, WindowBasedTextGUI textGUI, Song songFromPreviousPage, T cachedPage) {
        super(backend, guiThread);
        this.previousPage = previousPage;
        this.textGUI = textGUI;
        this.cachedSong = songFromPreviousPage;
        this.cachedPage = cachedPage;
    }

    @Override
    public BasicWindow createWindow() {
        return buildWindow();
    }

    @Override
    public BasicWindow updateWindow() {
        return buildWindow();
    }

    private BasicWindow buildWindow() {
        window = createStandardWindow(get("window.createPlaylistTitle"));

        addSpace(3);
        mainPanel.addComponent(createPlaylistNameEntryBox());
        addSpace();

        if (enableConfirmationButtons) {
            mainPanel.addComponent(createButton(
                        get("button.createPlaylistConfirm"),
                        () -> {
                            enableConfirmationButtons = false;
                            backend.createPlaylist(playlistName);
                            handlePageTransition();
                        },
                        componentFactory.getButtonWidth() / 2,
                        componentFactory.getButtonHeight() / 2
                        ));
            mainPanel.addComponent(createButton(
                        get("button.createPlaylistCancel"),
                        () -> {
                            enableConfirmationButtons = false;
                            handlePageTransition();
                        },
                        componentFactory.getButtonWidth() / 2,
                        componentFactory.getButtonHeight() / 2
                        ));
        }

        addSpace();

        mainPanel.addComponent(componentFactory.createButton(
            get("button.back"),
            () -> windowManager.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        return window;
    }

    private TextBox createPlaylistNameEntryBox() {
        return new TextBox(new TerminalSize(componentFactory.getTerminalSize().getColumns() / 2, 5)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    playlistName = this.getText();
                    if (verifyPlaylistName(playlistName)) {
                        enableConfirmationButtons = true;
                        guiThread.invokeLater(() -> {
                            window = buildWindow();
                            window.invalidate();
                            try {
                                textGUI.getScreen().refresh();
                            } catch (Exception e) {
                                Logger.error("[!] Error refreshing screen: " + e.getMessage());
                            }
                        });
                    }
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
    }

    private boolean verifyPlaylistName(String name) {
        if (name == null || name.isEmpty()) {
            Logger.debug("[*] Playlist name is empty.");
            return false;
        }
        if (name.length() > 20) {
            Logger.warn("[!] Playlist name is too long: it must be less than 20 characters.");
            return false;
        }
        return true;
    }

    private void handlePageTransition() {
        switch (previousPage.getClass().getSimpleName()) {
            case "PlaylistChoicePage":
                windowManager.buildPlaylistChoicePage(cachedSong, cachedPage);
                windowManager.transitionTo(windowManager.playlistChoicePageWindow);
                break;
            case "PlaylistPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.playlistPageWindow);
                break;
            default:
                windowManager.rebuildAllWindows();
                windowManager.returnToMainMenu(window);
                break;
        }
    }
}

