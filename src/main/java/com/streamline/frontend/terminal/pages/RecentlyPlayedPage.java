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
 * Window for displaying recently played songs.
 * @author wellatleastitried
 */
public class RecentlyPlayedPage extends BasePage {

    private final TextGUI textGUI;

    private Map<Integer, Button> recentlyPlayedButtons;

    public RecentlyPlayedPage(Dispatcher backend, TextGUIThread guiThread, TextGUI textGUI) {
        super(backend, guiThread);
        this.textGUI = textGUI;
    }

    @Override
    public BasicWindow createWindow() {
        recentlyPlayedButtons = new HashMap<Integer, Button>();

        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.recentlyPlayedTitle"));

        Panel panel = componentFactory.createStandardPanel();

        /* Panel for search results */
        Panel resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(1));
        resultsBox.setPreferredSize(new TerminalSize(
                    componentFactory.getTerminalSize().getColumns(), 
                    componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15
                    ));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.recentlyPlayedTitle")));

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(componentFactory.createEmptySpace());
        resultsBox = handleSongRendering(resultsBox, currentButtons);
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

    private Panel handleSongRendering(Panel resultsBox, Set<Button> currentButtons) {
        RetrievedStorage results = backend.getRecentlyPlayedSongs();
        if (results == null) {
            return resultsBox;
        }

        resultsToButtons(resultsBox, results);
        updateResultsDisplay(resultsBox, currentButtons);
        return resultsBox;
    }

    private void resultsToButtons(Panel resultsBox, RetrievedStorage results) {
        recentlyPlayedButtons = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            Song song = results.getSongFromIndex(i);
            String formattedText = componentFactory.getFormattedTextForSongButton(
                    resultsBox.getSize().getColumns(),
                    results.getIndexFromSong(song) + 1,
                    song.getSongName(),
                    song.getSongArtist(),
                    song.getDuration());
            recentlyPlayedButtons.put(i, componentFactory.createButton(
                        formattedText, 
                        () -> handleSongSelection(song),
                        resultsBox.getSize().getColumns(),
                        componentFactory.getButtonHeight()));
        }
    }

    private void updateResultsDisplay(Panel resultsBox, Set<Button> currentButtons) {
        guiThread.invokeLater(() -> {
            for (Button button : currentButtons) {
                resultsBox.removeComponent(button);
            }
            currentButtons.clear();

            for (Map.Entry<Integer, Button> entry : recentlyPlayedButtons.entrySet()) {
                if (currentButtons.add(entry.getValue())) {
                    resultsBox.addComponent(entry.getValue());
                }
            }

            try {
                textGUI.getScreen().refresh();
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
