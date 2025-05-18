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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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

    private final BasicWindow window;
    private final Panel panel;

    private final List<Panel> resultPanels;

    private final Button backButton;
    private final Button pageUpButton;
    private final Button pageDownButton;

    private final int RESULT_PANEL_WIDTH;
    private final int RESULT_PANEL_HEIGHT;
    private final int SONG_BUTTON_WIDTH;
    private final int SONG_BUTTON_HEIGHT;

    private Map<Integer, Button> likedMusicButtons;
    private Set<Button> currentButtons = new LinkedHashSet<>();

    private int CURRENT_PAGE = 0;
    private int CURRENT_PAGE_BACKEND = 0;

    public LikedMusicPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, TextGUI textGUI) {
        super(windowManager, backend, guiThread, componentFactory);
        this.textGUI = textGUI;
        this.window = createStandardWindow(LanguagePeer.getText("window.likedMusicTitle"));
        this.panel = componentFactory.createStandardPanel();
        this.resultPanels = new ArrayList<>();
        this.pageUpButton = createPageChangeButton("up");
        this.pageDownButton = createPageChangeButton("down");
        this.backButton = createPageChangeButton("back");
        this.RESULT_PANEL_WIDTH = componentFactory.getTerminalSize().getColumns();
        this.RESULT_PANEL_HEIGHT = componentFactory.getTerminalSize().getRows() - panel.getSize().getRows() - 15;
        this.SONG_BUTTON_WIDTH = RESULT_PANEL_WIDTH;
        this.SONG_BUTTON_HEIGHT = componentFactory.getButtonHeight();
    }

    @Override
    public BasicWindow createWindow() {
        likedMusicButtons = new HashMap<Integer, Button>();
        resultPanels.clear();

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicTitle")));

        CURRENT_PAGE_BACKEND = 0;
        resultPanels.add(new Panel());
        resultPanels.get(CURRENT_PAGE_BACKEND).setLayoutManager(new GridLayout(1));
        resultPanels.get(CURRENT_PAGE_BACKEND).setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
        resultPanels.get(CURRENT_PAGE_BACKEND).setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        panel.addComponent(componentFactory.createEmptySpace());

        if (currentButtons.size() > 20 && CURRENT_PAGE > 0) {
            panel.addComponent(pageUpButton);
            panel.addComponent(componentFactory.createEmptySpace());
        }
        handleSongRendering();
        panel.addComponent(resultPanels.get(CURRENT_PAGE_BACKEND));
        panel.addComponent(componentFactory.createEmptySpace());

        if (currentButtons.size() > 20 && CURRENT_PAGE < currentButtons.size() - (resultPanels.size() * 20)) {
            panel.addComponent(pageDownButton);
            panel.addComponent(componentFactory.createEmptySpace());
        }

        /* Back button */
        panel.addComponent(backButton);

        window.setComponent(panel);
        return window;
    }
    private Button createPageChangeButton(String direction) {
        if ("up".equals(direction)) {
            return componentFactory.createButton(
                    LanguagePeer.getText("button.pageUp"), 
                    () -> {
                        for (Button button : currentButtons) {
                            resultPanels.get(CURRENT_PAGE_BACKEND).removeComponent(button);
                        }
                        currentButtons.clear();
                        CURRENT_PAGE--;
                        updateResultsDisplay();
                    },
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    );
        } else if ("down".equals(direction)) {
            return componentFactory.createButton(
                    LanguagePeer.getText("button.pageDown"), 
                    () -> {
                        for (Button button : currentButtons) {
                            resultPanels.get(CURRENT_PAGE_BACKEND).removeComponent(button);
                        }
                        currentButtons.clear();
                        CURRENT_PAGE++;
                        updateResultsDisplay();
                    },
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    );
        } else if ("back".equals(direction)) {
            return componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> windowManager.returnToMainMenu(window),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    );
        } else {
            throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    private void handleSongRendering() {
        RetrievedStorage results = backend.getLikedSongs();
        if (results == null || results.size() < 1) {
            Logger.debug("No liked songs found.");
        }

        for (int i = 0; i < results.size() / 20; i++) {
            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(1));
            panel.setPreferredSize(new TerminalSize(RESULT_PANEL_WIDTH, RESULT_PANEL_HEIGHT));
            panel.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);
            resultPanels.add(panel);
        }

        resultsToButtons(results);
        updateResultsDisplay();
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
                for (int j = 0; j < 25; j++) {
                // likedMusicButtons.put(i, componentFactory.createButton(
                likedMusicButtons.put(j, componentFactory.createButton(
                            formattedText, 
                            () -> handleSongSelection(song),
                            SONG_BUTTON_WIDTH,
                            SONG_BUTTON_HEIGHT));
                }
            } catch (Exception e) {
                Writer buffer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(buffer);
                e.printStackTrace(printWriter);
                Logger.error("Error generating song button: {}", buffer.toString());
                continue;
            }
        }
    }

    private void updateResultsDisplay() {
        guiThread.invokeLater(() -> {
            List<Button> currentButtonList = new ArrayList<>(currentButtons);
            for (int i = 1; i <= resultPanels.size(); i++) {
                for (int j = 0; j < currentButtonList.size(); j++) {
                    if (j >= i * 20) {
                        break;
                    }
                    resultPanels.get(i - 1).removeComponent(currentButtonList.get(j));

                }
            }
            currentButtons.clear();

            int songButtonsAdded = 0;
            for (Map.Entry<Integer, Button> entry : likedMusicButtons.entrySet()) {
                Button button = entry.getValue();
                if (currentButtons.add(button)) {
                    Logger.debug("[*] Panel list size: {}, CURRENT_PAGE_BACKEND: {}", resultPanels.size(), CURRENT_PAGE_BACKEND);
                    resultPanels.get(CURRENT_PAGE_BACKEND).addComponent(button);
                    songButtonsAdded++;
                    if (songButtonsAdded >= 20) {
                        CURRENT_PAGE_BACKEND++;
                        if (CURRENT_PAGE_BACKEND >= resultPanels.size()) {
                            resultPanels.add(new Panel());
                        }
                    }
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
