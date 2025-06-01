package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.navigation.NavigationContext;

import org.tinylog.Logger;

public class CreatePlaylistPage extends AbstractDynamicPage {

    private final AbstractBasePage previousPage;

    private String playlistName;
    private boolean enableConfirmationButtons = false;

    private Song cachedSong = null;
    private AbstractBasePage cachedPage = null;

    public <T extends AbstractBasePage> CreatePlaylistPage(T previousPage, Dispatcher backend,
            TextGUIThread guiThread) {
        super(backend, guiThread);
        this.previousPage = previousPage;
        setWindowTitle(getText("window.createPlaylistTitle"));
    }

    public <T extends AbstractBasePage> CreatePlaylistPage(T previousPage, Dispatcher backend, TextGUIThread guiThread,
            Song songFromPreviousPage, T cachedPage) {
        super(backend, guiThread);
        setWindowTitle(getText("window.createPlaylistTitle"));
        this.previousPage = previousPage;
        this.cachedSong = songFromPreviousPage;
        this.cachedPage = cachedPage;
    }

    @Override
    public BasicWindow createWindow() {
        buildWindowContent("");
        return window;
    }

    @Override
    public void rebuildContent() {
        window.invalidate();
        buildWindowContent(playlistName != null ? playlistName : "");
        windowManager.refresh();
    }

    private void buildWindowContent(String textInTextBox) {
        mainPanel.removeAllComponents();

        addSpace(3);
        mainPanel.addComponent(createLabel(getText("label.createPlaylist")));
        mainPanel.addComponent(createPlaylistNameEntryBox(textInTextBox));
        addSpace();

        if (enableConfirmationButtons) {
            addConfirmationButtons();
        }

        addSpace();

        mainPanel.addComponent(componentFactory.createButton(
                getText("button.back"),
                this::navigateBack,
                componentFactory.getButtonWidth() / 3,
                componentFactory.getButtonHeight() / 2));

        window.setComponent(mainPanel);
    }

    private TextBox createPlaylistNameEntryBox(String text) {
        TextBox textBox = new TextBox(new TerminalSize(componentFactory.getTerminalSize().getColumns() / 2, 5)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    playlistName = this.getText();
                    if (verifyPlaylistName(playlistName)) {
                        enableConfirmationButtons = true;
                        guiThread.invokeLater(() -> {
                            updateWindow();
                        });
                    }
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
        textBox.setText(text);
        return textBox;
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

    @Override
    protected AbstractBasePage getPreviousPage() {
        return previousPage;
    }

    @Override
    protected NavigationContext createNavigationContext() {
        NavigationContext context = super.createNavigationContext();
        
        // Add cached data for navigation rules
        if (cachedSong != null) {
            context.setContextData("cachedSong", cachedSong);
        }
        if (cachedPage != null) {
            context.setContextData("cachedPage", cachedPage);
        }
        
        return context;
    }

    private void addConfirmationButtons() {
        mainPanel.addComponent(createButton(
                    getText("button.createPlaylistConfirm"),
                    this::handleConfirmation,
                    componentFactory.getButtonWidth() / 2,
                    componentFactory.getButtonHeight() / 2));
        mainPanel.addComponent(createButton(
                    getText("button.createPlaylistCancel"),
                    this::handleCancellation,
                    componentFactory.getButtonWidth() / 2,
                    componentFactory.getButtonHeight() / 2));
    }

    private void handleConfirmation() {
        enableConfirmationButtons = false;
        backend.createPlaylist(playlistName);
        navigateBack();
    }

    private void handleCancellation() {
        enableConfirmationButtons = false;
        navigateBack();
    }
}
