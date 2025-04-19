package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;

import java.util.Map;

public class SongOptionPage extends BasePage {

    private final Song selectedSong;
    public final BasePage previousPage;
    private final Map<Integer, Button> previousResultsForSearchPage;

    public <T extends BasePage> SongOptionPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, Song selectedSong, T previousPage) {
        super(windowManager, backend, guiThread, componentFactory);
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = null;
    }

    public <T extends BasePage> SongOptionPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, Song selectedSong, T previousPage, Map<Integer, Button> previousResultsForSearchPage) {
        super(windowManager, backend, guiThread, componentFactory);
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = previousResultsForSearchPage;
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.songOptionPageTitle"));

        Panel panel = componentFactory.createStandardPanel();

        // Panel for search results
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.songOptionPageTitle")));

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createButton(selectedSong.isSongLiked() ? "button.likeSong" : "button.unlikeSong", () -> {
            backend.handleSongLikeStatus(selectedSong);
        }));

        panel.addComponent(componentFactory.createEmptySpace());
        // Back button
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> {
                        windowManager.returnToMainMenu(window);
                        windowManager.rebuildSearchPage(previousResultsForSearchPage);
                    },
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));

        window.setComponent(panel);
        return window;
    }
}
