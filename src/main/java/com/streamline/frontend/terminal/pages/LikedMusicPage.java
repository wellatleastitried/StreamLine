package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.LanguagePeer;

import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.tinylog.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Window for displaying liked music.
 */
public class LikedMusicPage extends BasePage {

    private final TextGUI textGUI;

    private final Panel panel;

    private Panel resultsBox;
    private final int RESULT_PANEL_WIDTH;
    private final int RESULT_PANEL_HEIGHT;

    private final int SONG_BUTTON_WIDTH;
    private final int SONG_BUTTON_HEIGHT;

    private Map<Integer, Button> likedMusicButtons;

    public LikedMusicPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;

        this.panel = componentFactory.createStandardPanel();
        this.RESULT_PANEL_WIDTH = componentFactory.getTerminalSize().getColumns();
        this.RESULT_PANEL_HEIGHT = componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15;
        this.SONG_BUTTON_WIDTH = RESULT_PANEL_WIDTH;
        this.SONG_BUTTON_HEIGHT = componentFactory.getButtonHeight();
    }

    @Override
    public BasicWindow createWindow() {
        likedMusicButtons = new HashMap<Integer, Button>();

        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.likedMusicTitle"));

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicTitle")));

        resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(1));
        resultsBox.setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(componentFactory.createEmptySpace());
        handleSongRendering(currentButtons);
        panel.addComponent(resultsBox);

        /* Back button */
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> windowManager.returnToMainMenu(window),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));

        window.setComponent(panel);
        return window;
    }

    private void handleSongRendering(Set<Button> currentButtons) {
        RetrievedStorage results = backend.getLikedSongs();
        if (results == null || results.size() < 1) {
            Logger.debug("No liked songs found.");
        }

        resultsToButtons(results);
        updateResultsDisplay(currentButtons);
        Logger.debug("Display has been updated with {} liked songs.", results.size());
    }

    private void resultsToButtons(RetrievedStorage results) {
        if (likedMusicButtons == null) {
            likedMusicButtons = new HashMap<>();
        } else {
            likedMusicButtons.clear();
        }
        Logger.debug("SIZE: {}", results.size());
        for (int i = 0; i < results.size(); i++) {
            int displayIndex = i + 1;
            try {
                Song song = results.getSongFromIndex(displayIndex);
                if (song == null) {
                    Logger.debug("Song is null at index {}", displayIndex);
                    continue;
                }

                String formattedText = componentFactory.getFormattedTextForSongButton(
                        RESULT_PANEL_WIDTH - 6, /* Maybe God himself knows why I am having to subtract 6 on this page but not SearchPage */
                        displayIndex,
                        song.getSongName(),
                        song.getSongArtist(),
                        song.getDuration());
                Logger.debug("Like : {}", formattedText);
                likedMusicButtons.put(i, componentFactory.createButton(
                            formattedText, 
                            () -> handleSongSelection(song),
                            SONG_BUTTON_WIDTH,
                            SONG_BUTTON_HEIGHT));
            } catch (Exception e) {
                Writer buffer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(buffer);
                e.printStackTrace(printWriter);
                Logger.error("Error generating song button: {}", buffer.toString());
                continue;
            }
        }
    }

    private void updateResultsDisplay(Set<Button> currentButtons) {
        guiThread.invokeLater(() -> {
            for (Button button : currentButtons) {
                resultsBox.removeComponent(button);
            }
            currentButtons.clear();

            for (Map.Entry<Integer, Button> entry : likedMusicButtons.entrySet()) {
                if (currentButtons.add(entry.getValue())) {
                    resultsBox.addComponent(entry.getValue());
                }
            }

            try {
                textGUI.getScreen().refresh();
                Logger.debug("Screen refreshed successfully.");
            } catch (IOException iE) {
                Logger.error("[!] Error while redrawing screen, please restart the app.");
            }
        });
    }

    private void handleSongSelection(Song song) {
        windowManager.buildSongOptionPage(song, this);
        windowManager.transitionTo(windowManager.songOptionPage);
    }
}
