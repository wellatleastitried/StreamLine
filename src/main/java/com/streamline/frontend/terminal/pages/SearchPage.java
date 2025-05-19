package com.streamline.frontend.terminal.pages;

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
public class SearchPage extends AbstractBasePage {

    private final TextGUI textGUI;

    private Map<Integer, Button> searchResultButtons;

    public SearchPage(Dispatcher backend, TextGUIThread guiThread, TextGUI textGUI) {
        super(backend, guiThread);
        this.textGUI = textGUI;
    }

    public SearchPage(Dispatcher backend, TextGUIThread guiThread, TextGUI textGUI, Map<Integer, Button> searchResultButtons) {
        super(backend, guiThread);
        this.textGUI = textGUI;
        this.searchResultButtons = searchResultButtons;
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.searchTitle"));

        Panel panel = componentFactory.createStandardPanel();

        /* Panel for search results */
        Panel resultsPanel = new Panel();
        resultsPanel.setLayoutManager(new GridLayout(1));
        resultsPanel.setPreferredSize(new TerminalSize(
                    componentFactory.getTerminalSize().getColumns(), 
                    componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15
                    ));
        resultsPanel.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.search")));

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(createSearchBox(resultsPanel, currentButtons));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(resultsPanel);
        if (searchResultButtons != null && searchResultButtons.size() > 0) {
            updateResultsDisplay(resultsPanel, currentButtons);
        }

        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> {
                        windowManager.rebuildSearchPage(null);
                        windowManager.returnToMainMenu(window);
                    },
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));

        window.setComponent(panel);
        return window;
    }

    private TextBox createSearchBox(Panel resultsPanel, Set<Button> currentButtons) {
        return new TextBox(new TerminalSize(componentFactory.getTerminalSize().getColumns() / 2, 1)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    String enteredText = this.getText();
                    RetrievedStorage results = backend.doSearch(enteredText);
                    if (results == null) {
                        return Result.HANDLED;
                    }

                    resultsToButtons(results, resultsPanel);
                    updateResultsDisplay(resultsPanel, currentButtons);
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

    private void resultsToButtons(RetrievedStorage results, Panel resultsPanel) {
        if (searchResultButtons != null) {
            searchResultButtons.clear();
        } else {
            searchResultButtons = new HashMap<>();
        }
        Logger.debug("SEARCH PAGE ResultsPanel width: {}", resultsPanel.getSize().getColumns());
        for (int i = 0; i < results.size(); i++) {
            Song song = results.getSongFromIndex(i);
            String formattedText = componentFactory.getFormattedTextForSongButton(
                    resultsPanel.getSize().getColumns(),
                    results.getIndexFromSong(song) + 1,
                    song.getSongName(),
                    song.getSongArtist(),
                    song.getDuration());
            Logger.debug("Search: {}", formattedText);
            Logger.debug("Search Button created for song with text: {}, width: {}, height: {}", formattedText, resultsPanel.getSize().getColumns(), componentFactory.getButtonHeight());
            searchResultButtons.put(i, componentFactory.createButton(
                        formattedText, 
                        () -> handleSongSelection(song),
                        resultsPanel.getSize().getColumns(),
                        componentFactory.getButtonHeight()));
        }
    }

    private void updateResultsDisplay(Panel resultsPanel, Set<Button> currentButtons) {
        guiThread.invokeLater(() -> {
            for (Button button : currentButtons) {
                resultsPanel.removeComponent(button);
            }
            currentButtons.clear();

            for (Map.Entry<Integer, Button> entry : searchResultButtons.entrySet()) {
                if (currentButtons.add(entry.getValue())) {
                    resultsPanel.addComponent(entry.getValue());
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
