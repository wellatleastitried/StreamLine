package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.LoggerUtils;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import org.tinylog.Logger;

/**
 * Window for displaying recently played music.
 */
public class RecentlyPlayedPage extends AbstractDynamicPage {

    private final TextGUI textGUI;
    private final BasicWindow window;
    private final Panel resultPanel;

    private final Button backButton;

    private final int RESULT_PANEL_WIDTH;
    private final int RESULT_PANEL_HEIGHT;
    private final int SONG_BUTTON_WIDTH;
    private final int SONG_BUTTON_HEIGHT;
    private final int SONGS_PER_PAGE = 20;

    private List<Song> recentlyPlayedSongs;
    private List<Button> songButtons;

    private int currentPage = 0;
    private int totalPages = 0;

    public RecentlyPlayedPage(Dispatcher backend, TextGUIThread guiThread, TextGUI textGUI) {
        super(backend, guiThread);
        this.textGUI = textGUI;
        this.window = createStandardWindow(get("window.downloadedMusicTitle"));
        this.RESULT_PANEL_WIDTH = componentFactory.getTerminalSize().getColumns();
        this.RESULT_PANEL_HEIGHT = componentFactory.getTerminalSize().getRows() - mainPanel.getSize().getRows() - 15;
        this.SONG_BUTTON_WIDTH = RESULT_PANEL_WIDTH;
        this.SONG_BUTTON_HEIGHT = componentFactory.getButtonHeight();

        this.resultPanel = new Panel();
        this.resultPanel.setLayoutManager(new GridLayout(1));
        this.resultPanel.setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
        this.resultPanel.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        this.backButton = createBackButton();

        this.songButtons = new ArrayList<>();
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

    private void refreshWindow() {
        try {
            textGUI.getScreen().refresh();
            Logger.debug("Screen refreshed successfully.");
        } catch (IOException iE) {
            Logger.error("[!] Error while redrawing screen, please restart the app.");
        }
    }

    private void buildPage() {
        mainPanel.removeAllComponents();
        resultPanel.removeAllComponents();
        songButtons.clear();
        addSpace();
        mainPanel.addComponent(createLabel(get("label.downloadedMusicTitle")));
        addSpace();
        RetrievedStorage retrievedSongs = getRecentlyPlayedSongs();
        loadSongs(retrievedSongs);
        totalPages = (int) Math.ceil((double) recentlyPlayedSongs.size() / SONGS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        displayCurrentPage();
        mainPanel.addComponent(resultPanel);
        addSpace();
        mainPanel.addComponent(backButton);
        window.setComponent(mainPanel);
    }

    private void loadSongs(RetrievedStorage songs) {
        recentlyPlayedSongs = new LinkedList<>();

        if (songs == null || songs.size() < 1) {
            Logger.debug("No downloaded songs found.");
            return;
        }

        for (Song song : songs.drain()) {
            if (song == null) {
                Logger.debug("Song is null.");
                continue;
            }
            recentlyPlayedSongs.add(song);
        }

        Logger.debug("Loaded {} downloaded songs.", recentlyPlayedSongs.size());
    }

    private void displayCurrentPage() {
        int startIndex = currentPage * SONGS_PER_PAGE;
        int endIndex = Math.min(startIndex + SONGS_PER_PAGE, recentlyPlayedSongs.size());

        for (int i = startIndex; i < endIndex; i++) {
            Song song = recentlyPlayedSongs.get(i);
            String formattedText = componentFactory.getFormattedTextForSongButton(
                    RESULT_PANEL_WIDTH - 6, /* Maybe God himself knows why I am having to subtract 6 on this page but not SearchPage */
                    i + 1,
                    song.getSongName(),
                    song.getSongArtist(),
                    song.getDuration());
            Button songButton = createButton(formattedText, () -> handleSongSelection(song), SONG_BUTTON_WIDTH, SONG_BUTTON_HEIGHT);
            songButtons.add(songButton);
            resultPanel.addComponent(songButton);
        }
    }

    private Button createBackButton() {
        return createButton(
                get("button.back"), 
                () -> windowManager.returnToMainMenu(window),
                componentFactory.getButtonWidth() / 3, 
                componentFactory.getButtonHeight() / 2
                );
    }

    private RetrievedStorage getRecentlyPlayedSongs() {
        return backend.getRecentlyPlayedSongs();
    }

    private void handleSongSelection(Song song) {
        windowManager.buildSongOptionPage(song, this);
        windowManager.transitionTo(windowManager.songOptionPageWindow);
    }
}
