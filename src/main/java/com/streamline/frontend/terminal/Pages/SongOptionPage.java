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
    private final Map<Integer, Button> previousResultsForSearchPage;

    private BasicWindow window;
    public final BasePage previousPage;

    public <T extends BasePage> SongOptionPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, Song selectedSong, T previousPage) {
        super(windowManager, backend, guiThread, componentFactory);
        selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = null;
    }

    public <T extends BasePage> SongOptionPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory, Song selectedSong, T previousPage, Map<Integer, Button> previousResultsForSearchPage) {
        super(windowManager, backend, guiThread, componentFactory);
        selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = previousResultsForSearchPage;
    }

    @Override
    public BasicWindow createWindow() {
        window = createStandardWindow(LanguagePeer.getText("window.songOptionPageTitle"));

        Panel panel = componentFactory.createStandardPanel();

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.songOptionPageTitle")));

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createButton(LanguagePeer.getText("button.playSong"), () -> {
            backend.playSong(selectedSong);
            handlePageTransition();
        }));


        panel.addComponent(componentFactory.createButton(selectedSong.isSongLiked() ? LanguagePeer.getText("button.unlikeSong") : LanguagePeer.getText("button.likeSong"), () -> {
            backend.handleSongLikeStatus(selectedSong);
            if (previousResultsForSearchPage != null) {
                windowManager.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
            } else {
                windowManager.buildSongOptionPage(selectedSong, previousPage);
            }
            handlePageTransition();
        }));

        panel.addComponent(componentFactory.createButton(LanguagePeer.getText("button.addToPlaylist"), () -> {
            windowManager.transitionToPlaylistChoicePage(previousPage, selectedSong, previousResultsForSearchPage);
        }));

        panel.addComponent(componentFactory.createButton(LanguagePeer.getText("button.downloadSong"), () -> {
            backend.downloadSong(selectedSong);
            if (previousResultsForSearchPage != null) {
                windowManager.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
            } else {
                windowManager.buildSongOptionPage(selectedSong, previousPage);
            }
            windowManager.refresh();
        }));

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> handlePageTransition(),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));

        window.setComponent(panel);
        return window;
    }

    private void handlePageTransition() {
        switch (previousPage.getClass().getSimpleName()) {
            case "SearchPage":
                windowManager.rebuildSearchPage(previousResultsForSearchPage);
                windowManager.transitionToCachedSearchPage();
                break;
            case "LikedMusicPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.likedMusicPage);
                break;
            case "DownloadedMusicPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.downloadedPage);
                break;
            case "PlaylistPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.playlistPage);
                break;
            default:
                windowManager.rebuildAllWindows();
                windowManager.returnToMainMenu(window);
                break;
        }
    }
}
