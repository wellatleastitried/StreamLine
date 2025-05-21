package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.TextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Panel;


import com.streamline.audio.Playlist;
import com.streamline.backend.Dispatcher;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.tinylog.Logger;

/**
 * Window for displaying and managing playlists.
 */
public class PlaylistPage extends AbstractDynamicPage {

    private final TextGUI textGUI;

    private final BasicWindow window;
    private final Panel resultPanel;
    private final int RESULT_PANEL_WIDTH;
    private final int RESULT_PANEL_HEIGHT;
    private final int PLAYLIST_BUTTON_WIDTH;
    private final int PLAYLIST_BUTTON_HEIGHT;
    private final int PLAYLISTS_PER_PAGE = 20;

    private List<Playlist> playlists = null;
    private List<Button> displayedPlaylists = new LinkedList<>();

    private int currentPage = 0;
    
    public PlaylistPage(Dispatcher backend, TextGUIThread guiThread, TextGUI textGUI) {
        super(backend, guiThread);
        this.textGUI = textGUI;
        this.window = createStandardWindow(get("window.playlistsTitle"));

        this.RESULT_PANEL_WIDTH = componentFactory.getTerminalSize().getColumns();
        this.RESULT_PANEL_HEIGHT = componentFactory.getTerminalSize().getRows() - mainPanel.getSize().getRows() - 15;
        this.PLAYLIST_BUTTON_WIDTH = RESULT_PANEL_WIDTH / 2;
        this.PLAYLIST_BUTTON_HEIGHT = componentFactory.getButtonHeight();

        this.resultPanel = new Panel();
        this.resultPanel.setLayoutManager(new GridLayout(1));
        this.resultPanel.setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
        this.resultPanel.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);
    }

    @Override
    public BasicWindow createWindow() {
        currentPage = 0;
        buildPage();
        return window;
    }

    @Override
    public BasicWindow updateWindow() {
        buildPage();
        window.invalidate();
        refreshWindow();
        return window;
    }

    private void buildPage() {
        mainPanel.removeAllComponents();
        resultPanel.removeAllComponents();

        addSpace();
        mainPanel.addComponent(componentFactory.createLabel(get("label.playlistsFeature")));
        addSpace();
        mainPanel.addComponent(createButton(
                    get("button.createPlaylist"),
                    () -> {
                        handleCreatePlaylist();
                    },
                    componentFactory.getButtonWidth() / 2,
                    componentFactory.getButtonHeight() / 2
                    ));
        
        addSpace();
        displayCurrentPage();

        mainPanel.addComponent(componentFactory.createButton(
            get("button.back"),
            () -> windowManager.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
    }

    private void displayCurrentPage() {
        if (playlists == null) {
            return;
        }

        int startIndex = currentPage * PLAYLISTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PLAYLISTS_PER_PAGE, playlists.size());
        for (int i = startIndex; i < endIndex; i++) {
            Playlist playlist = playlists.get(i);
            String formattedText = componentFactory.getFormattedTextForPlaylistButton(
                    PLAYLIST_BUTTON_WIDTH,
                    playlist.getName()
                    );
            Button playlistButton = createButton(formattedText, () -> handlePlaylistSelection(playlist), PLAYLIST_BUTTON_WIDTH, PLAYLIST_BUTTON_HEIGHT);
            displayedPlaylists.add(playlistButton);
            resultPanel.addComponent(playlistButton);
        }
    }

    private void refreshWindow() {
        try {
            textGUI.getScreen().refresh();
            Logger.debug("Screen refreshed successfully.");
        } catch (IOException iE) {
            Logger.error("[!] Error while redrawing screen, please restart the app.");
        }
    }

    private void handleCreatePlaylist() {
        windowManager.buildCreatePlaylistPage(this);
        windowManager.transitionTo(windowManager.createPlaylistPageWindow);
    }

    private void handlePlaylistSelection(Playlist playlist) {
        windowManager.buildSongsFromPlaylistPage(playlist, this);
        windowManager.transitionTo(windowManager.songsFromPlaylistPageWindow);
    }
}

