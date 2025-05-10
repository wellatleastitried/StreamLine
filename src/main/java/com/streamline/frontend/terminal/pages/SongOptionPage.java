package com.streamline.frontend.terminal.pages;

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
    private Panel panel;
    private Button likeButton;

    private Button downloadButton;
    private boolean isDownloading = false;

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
        panel = componentFactory.createStandardPanel();
        fillPanelComponents();
        window.setComponent(panel);
        return window;
    }

    private void fillPanelComponents() {
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.songOptionPageTitle")));

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createButton(LanguagePeer.getText("button.playSong"), () -> {
            backend.playSong(selectedSong);
        }));


        likeButton = createLikeButton();
        panel.addComponent(likeButton);

        panel.addComponent(componentFactory.createButton(LanguagePeer.getText("button.addToPlaylist"), () -> {
            windowManager.transitionToPlaylistChoicePage(previousPage, selectedSong, previousResultsForSearchPage);
        }));

        downloadButton = createDownloadButton();
        panel.addComponent(downloadButton);

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> handlePageTransition(),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                    ));
    }

    private Button createLikeButton() {
        return componentFactory.createButton(backend.isSongLiked(selectedSong) ? LanguagePeer.getText("button.dislikeSong") : LanguagePeer.getText("button.likeSong"), () -> {
            String jobId = backend.handleSongLikeStatus(selectedSong);
            while (backend.getJob(jobId).isRunning()) {
                Thread.onSpinWait();
            }
            selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
            likeButton = createLikeButton();
            updatePanel();
            windowManager.refresh();
        });
    }

    private Button createDownloadButton() {
        String buttonText;
        Runnable buttonAction;
        boolean songIsDownloaded = backend.isSongDownloaded(selectedSong).getDownloadPath() != null;
        if (!songIsDownloaded && !isDownloading) {
            buttonText = LanguagePeer.getText("button.downloadSong");
            buttonAction = () -> {
                isDownloading = true;
                backend.downloadSong(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    windowManager.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    windowManager.buildSongOptionPage(selectedSong, previousPage);
                }
                windowManager.refresh();
            };
        } else if (!songIsDownloaded && isDownloading) {
            buttonText = LanguagePeer.getText("button.cancelDownload");
            buttonAction = () -> {
                backend.cancelSongDownload(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    windowManager.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    windowManager.buildSongOptionPage(selectedSong, previousPage);
                }
                windowManager.refresh();
            };
        } else {
            songIsDownloaded = false;
            buttonText = LanguagePeer.getText("button.removeDownload");
            buttonAction = () -> {
                backend.removeDownloadedSong(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    windowManager.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    windowManager.buildSongOptionPage(selectedSong, previousPage);
                }
                windowManager.refresh();
            };
        }
        if (isDownloading) {
            buttonText = LanguagePeer.getText("button.cancelDownload");
        } else {
            buttonText = LanguagePeer.getText("button.downloadSong");
        }

        return componentFactory.createButton(buttonText, buttonAction);
    }

    private void updatePanel() {
        guiThread.invokeLater(() -> {
            panel.removeAllComponents();
            fillPanelComponents();
        });
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
