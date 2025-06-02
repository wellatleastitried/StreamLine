package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Panel;

import com.streamline.audio.Playlist;
import com.streamline.backend.Dispatcher;

import java.util.LinkedList;
import java.util.List;

/**
 * Window for displaying and managing playlists.
 */
public class PlaylistPage extends AbstractDynamicPage {

    private final Panel resultPanel;
    private final int RESULT_PANEL_WIDTH;
    private final int RESULT_PANEL_HEIGHT;

    private final Button createPlaylistButton;

    private final int PLAYLIST_BUTTON_WIDTH;
    private final int PLAYLIST_BUTTON_HEIGHT;
    private final int PLAYLISTS_PER_PAGE = 20;

    private List<Playlist> playlists = null;
    private List<Button> displayedPlaylists = new LinkedList<>();

    private int currentPage = 0;
    
    public PlaylistPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
        setWindowTitle(getText("window.playlistsTitle"));

        this.RESULT_PANEL_WIDTH = componentFactory.getTerminalSize().getColumns();
        this.RESULT_PANEL_HEIGHT = componentFactory.getTerminalSize().getRows() - mainPanel.getSize().getRows() - 15;
        this.PLAYLIST_BUTTON_WIDTH = RESULT_PANEL_WIDTH / 2;
        this.PLAYLIST_BUTTON_HEIGHT = componentFactory.getButtonHeight();

        this.resultPanel = new Panel();
        this.resultPanel.setLayoutManager(new GridLayout(1));
        this.resultPanel.setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
        this.resultPanel.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        this.createPlaylistButton = buildCreatePlaylistButton();
    }

    @Override
    public BasicWindow createWindow() {
        currentPage = 0;
        playlists = null;
        rebuildContent();
        return window;
    }

    @Override
    protected void rebuildContent() {
        mainPanel.removeAllComponents();
        resultPanel.removeAllComponents();
        
        if (playlists == null) {
            playlists = backend.getPlaylists();
        }

        addSpace();
        mainPanel.addComponent(componentFactory.createLabel(getText("label.playlistsFeature")));
        addSpace();
        mainPanel.addComponent(createPlaylistButton);
        
        addSpace();
        displayCurrentPage();

        mainPanel.addComponent(componentFactory.createButton(
            getText("button.back"),
            this::navigateBack,
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        if (mainPanel.containsComponent(createPlaylistButton)) {
            createPlaylistButton.takeFocus();
        }
    }

    /**
     * Refreshes the playlists by reloading them from the backend.
     * This should be called when playlists might have been updated.
     */
    public void refreshPlaylists() {
        playlists = null;
        rebuildContent();
    }
    
    @Override
    protected void preUpdateHook() {
        // Always refresh playlists before updating the page
        playlists = null;
    }
    
    private void displayCurrentPage() {
        if (playlists == null || playlists.isEmpty()) {
            // Add a message when no playlists are found
            resultPanel.addComponent(componentFactory.createLabel(getText("label.noPlaylistsFound")));
            mainPanel.addComponent(resultPanel);
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
        mainPanel.addComponent(resultPanel);
    }

    private Button buildCreatePlaylistButton() {
        return createButton(
            getText("button.createPlaylist"),
            this::handleCreatePlaylist,
            componentFactory.getButtonWidth() / 2,
            componentFactory.getButtonHeight() / 2
        );
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

