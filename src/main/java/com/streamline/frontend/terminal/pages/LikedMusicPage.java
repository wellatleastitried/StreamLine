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

/**
 * Window for displaying liked music.
 */
public class LikedMusicPage extends BasePage {

    private final TextGUI textGUI;

    private Map<Integer, Button> likedMusicButtons;
    Panel resultsBox = null;

    public LikedMusicPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;
    }

    @Override
    public BasicWindow createWindow() {
        likedMusicButtons = new HashMap<Integer, Button>();

        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.likedMusicTitle"));

        Panel panel = componentFactory.createStandardPanel();

        /* Panel for search results */
        resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(1));
        resultsBox.setPreferredSize(new TerminalSize(
                    componentFactory.getTerminalSize().getColumns(), 
                    componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15
                    ));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicTitle")));

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(componentFactory.createEmptySpace());
        handleSongRendering(currentButtons);
        Logger.debug("Child count of resultsBox: " + resultsBox.getChildCount());
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

    private Panel handleSongRendering(Set<Button> currentButtons) {
        RetrievedStorage results = backend.getLikedSongs();
        if (results == null || results.size() < 1) {
            Logger.debug("No liked songs found.");
            return resultsBox;
        }

        resultsToButtons(results);
        updateResultsDisplay(currentButtons);
        Logger.debug("Display has been updated with {} liked songs.", results.size());
        return resultsBox;
    }

    private void resultsToButtons(RetrievedStorage results) {
        likedMusicButtons = new HashMap<>();
        for (int i = 1; i < results.size(); i++) {
            Song song = results.getSongFromIndex(i);
            if (song == null) {
                Logger.debug("Song is null at index {}", i);
                continue;
            }
            String formattedText = componentFactory.getFormattedTextForSongButton(
                    resultsBox.getSize().getColumns(),
                    results.getIndexFromSong(song) + 1,
                    song.getSongName(),
                    song.getSongArtist(),
                    song.getDuration());
            likedMusicButtons.put(i, componentFactory.createButton(
                        formattedText, 
                        () -> handleSongSelection(song),
                        resultsBox.getSize().getColumns(),
                        componentFactory.getButtonHeight()));
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
