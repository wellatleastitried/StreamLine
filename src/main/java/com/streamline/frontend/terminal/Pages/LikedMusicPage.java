package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
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

import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

/**
 * Window for displaying liked music.
 */
public class LikedMusicPage extends BasePage {

    private Map<Integer, Button> likedMusicButtons;

    public LikedMusicPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        likedMusicButtons = new HashMap<Integer, Button>();

        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.likedMusicTitle"));

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
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicTitle")));

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
        RetrievedStorage results = backend.getLikedSongs();
        if (results == null) {
            return resultsBox;
        }

        resultsToButtons(results);
        updateResultsDisplay(resultsBox, currentButtons);
        return resultsBox;
    }

    private void resultsToButtons(RetrievedStorage results) {
        likedMusicButtons = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            Song song = results.getSongFromIndex(i);
            String text = String.format(
                    "%d%s%s - %s   %s",
                    results.getIndexFromSong(song) + 1,
                    getOffsetForSongButton(results.getIndexFromSong(song)),
                    song.getSongName(),
                    song.getSongArtist(),
                    song.getDuration()
                    );
            likedMusicButtons.put(i, new Button(text, () -> handleSongSelection(song)));
        }
    }

    private void updateResultsDisplay(Panel resultsBox, Set<Button> currentButtons) {
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
        });
    }

    private void handleSongSelection(Song song) {
        windowManager.buildSongOptionPage(song, this);
        windowManager.transitionTo(windowManager.songOptionPage);
    }

    private String getOffsetForSongButton(int digits) {
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < 7 - String.valueOf(digits).length(); i++) {
            sB.append(" ");
        }
        return sB.toString();
    }
}
