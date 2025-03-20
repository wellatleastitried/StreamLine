package com.streamline.frontend.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RetrievedStorage;
import com.streamline.utilities.internal.StreamLineMessages;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Window for the search functionality.
 * @author wellatleastitried
 */
public class SearchPageWindow extends BaseWindow {

    private final TextGUI textGUI;

    public SearchPageWindow(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow("Search");

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
        panel.addComponent(componentFactory.createLabel("Search:"));

        Set<Button> currentButtons = new LinkedHashSet<>();
        panel.addComponent(createSearchBox(resultsBox, currentButtons));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(resultsBox);

        // Back button
        panel.addComponent(componentFactory.createButton(
                    "  <- Back  ", 
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

                    Button[] buttons = resultsToButtons(results);
                    updateResultsDisplay(resultsBox, currentButtons, buttons);
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }

            private Button[] resultsToButtons(RetrievedStorage results) {
                Button[] buttons = new Button[results.size()];
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
                    buttons[i] = new Button(text, () -> handleSongSelection(song));
                }
                return buttons;
            }

            private void updateResultsDisplay(Panel resultsBox, Set<Button> currentButtons, Button[] buttons) {
                guiThread.invokeLater(() -> {
                    for (Button button : currentButtons) {
                        resultsBox.removeComponent(button);
                    }
                    currentButtons.clear();

                    for (Button button : buttons) {
                        if (currentButtons.add(button)) {
                            resultsBox.addComponent(button);
                        }
                    }

                    try {
                        textGUI.getScreen().refresh();
                    } catch (IOException iE) {
                        Logger.error(StreamLineMessages.RedrawError.getMessage());
                    }
                });
            }

            private void handleSongSelection(Song song) {
                // Implementation for handling song selection
            }

            private String getOffsetForSongButton(int digits) {
                StringBuilder sB = new StringBuilder();
                for (int i = 0; i < 7 - String.valueOf(digits).length(); i++) {
                    sB.append(" ");
                }
                return sB.toString();
            }
        };
    }
}
