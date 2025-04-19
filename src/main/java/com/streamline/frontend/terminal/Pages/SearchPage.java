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
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Window for the search functionality.
 * @author wellatleastitried
 */
public class SearchPage extends BasePage {

    private final TextGUI textGUI;

    private Map<Integer, Button> searchResultButtons;

    public SearchPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;
    }

    public SearchPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI, Map<Integer, Button> searchResultButtons) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;
        this.searchResultButtons = searchResultButtons;
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.searchTitle"));

        Panel panel = componentFactory.createStandardPanel();

        // Panel for search results
        Panel resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(1));
        resultsBox.setPreferredSize(new TerminalSize(
                    componentFactory.getTerminalSize().getColumns(), 
                    componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15
                    ));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.search")));

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(createSearchBox(resultsBox, currentButtons));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(resultsBox);
        if (searchResultButtons != null && searchResultButtons.size() > 0) {
            updateResultsDisplay(resultsBox, currentButtons);
        }

        // Back button
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> windowManager.returnToMainMenu(window),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));

        window.setComponent(panel);
        return window;
    }

    private TextBox createSearchBox(Panel resultsBox, Set<Button> currentButtons) {
        return new TextBox(new TerminalSize(componentFactory.getTerminalSize().getColumns() / 2, 1)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    String enteredText = this.getText();
                    RetrievedStorage results = backend.doSearch(enteredText);
                    if (results == null) {
                        return Result.HANDLED;
                    }

                    resultsToButtons(results);
                    updateResultsDisplay(resultsBox, currentButtons);
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }


        };
    }
    private void handleSongSelection(Song song) {
        windowManager.buildSongOptionPage(song, this, searchResultButtons);
        windowManager.transitionTo(windowManager.songOptionPage);
    }

    private String getOffsetForSongButton(int digits) {
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < 7 - String.valueOf(digits).length(); i++) {
            sB.append(" ");
        }
        return sB.toString();
    }
    private void resultsToButtons(RetrievedStorage results) {
        searchResultButtons = new HashMap<>();
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
            searchResultButtons.put(i, new Button(text, () -> handleSongSelection(song)));
        }
    }

    private void updateResultsDisplay(Panel resultsBox, Set<Button> currentButtons) {
        guiThread.invokeLater(() -> {
            for (Button button : currentButtons) {
                resultsBox.removeComponent(button);
            }
            currentButtons.clear();

            for (Map.Entry<Integer, Button> entry : searchResultButtons.entrySet()) {
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
}
