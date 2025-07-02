package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;

import java.util.Map;

public class SongOptionPage extends AbstractDynamicPage {

    private final Song selectedSong;
    private final Map<Integer, Button> previousResultsForSearchPage;

    private Button likeButton;

    private Button downloadButton;
    private boolean isDownloading = false;

    public final AbstractBasePage previousPage;

    public <T extends AbstractBasePage> SongOptionPage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage) {
        super(backend, guiThread);
        setWindowTitle(getText("window.songOptionPageTitle"));
        selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = null;
    }

    public <T extends AbstractBasePage> SongOptionPage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage, Map<Integer, Button> previousResultsForSearchPage) {
        super(backend, guiThread);
        setWindowTitle(getText("window.songOptionPageTitle"));
        selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = previousResultsForSearchPage;
    }

    @Override
    public BasicWindow createWindow() {
        rebuildContent();
        return window;
    }

    @Override
    protected void rebuildContent() {
        fillPanelComponents();
        if (window != null && mainPanel != null) {
            window.setComponent(mainPanel);
        }
    }

    private void fillPanelComponents() {
        if (mainPanel.getChildren().size() > 0) {
            mainPanel.removeAllComponents();
        }
        addSpace();
        mainPanel.addComponent(componentFactory.createLabel(getText("label.songOptionPageTitle")));

        addSpace();
        mainPanel.addComponent(componentFactory.createButton(getText("button.playSong"), () -> {
            backend.playSong(selectedSong);
        }));


        likeButton = createLikeButton();
        mainPanel.addComponent(likeButton);

        mainPanel.addComponent(componentFactory.createButton(getText("button.addToPlaylist"), () -> {
            wm.transitionToPlaylistChoicePage(previousPage, selectedSong, previousResultsForSearchPage);
        }));

        downloadButton = createDownloadButton();
        mainPanel.addComponent(downloadButton);

        addSpace();
        mainPanel.addComponent(componentFactory.createButton(
                    getText("button.back"), 
                    this::navigateBack,
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2));
    }

    private Button createLikeButton() {
        return componentFactory.createButton(backend.isSongLiked(selectedSong) ? getText("button.dislikeSong") : getText("button.likeSong"), () -> {
            String jobId = backend.handleSongLikeStatus(selectedSong);
            while (backend.getJob(jobId).isRunning()) {
                Thread.onSpinWait();
            }
            selectedSong.setSongLikeStatus(backend.isSongLiked(selectedSong));
            likeButton = createLikeButton();
            updatePanel();
            wm.markWindowAsDirty(LikedMusicPage.class, wm.likedMusicPage);
            wm.refresh();
        });
    }

    private Button createDownloadButton() {
        String buttonText;
        Runnable buttonAction;
        boolean songIsDownloaded = backend.isSongDownloaded(selectedSong).getDownloadPath() != null;
        if (!songIsDownloaded && !isDownloading) {
            buttonText = getText("button.downloadSong");
            buttonAction = () -> {
                isDownloading = true;
                backend.downloadSong(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    wm.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    wm.buildSongOptionPage(selectedSong, previousPage);
                }
                wm.markWindowAsDirty(DownloadedMusicPage.class, wm.downloadedPage);
                wm.refresh();
            };
        } else if (!songIsDownloaded && isDownloading) {
            buttonText = getText("button.cancelDownload");
            buttonAction = () -> {
                backend.cancelSongDownload(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    wm.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    wm.buildSongOptionPage(selectedSong, previousPage);
                }
                wm.markWindowAsDirty(DownloadedMusicPage.class, wm.downloadedPage);
                wm.refresh();
            };
        } else {
            songIsDownloaded = false;
            buttonText = getText("button.removeDownload");
            buttonAction = () -> {
                backend.removeDownloadedSong(selectedSong);
                isDownloading = false;
                if (previousResultsForSearchPage != null) {
                    wm.buildSongOptionPage(selectedSong, previousPage, previousResultsForSearchPage);
                } else {
                    wm.buildSongOptionPage(selectedSong, previousPage);
                }
                wm.markWindowAsDirty(DownloadedMusicPage.class, wm.downloadedPage);
                wm.refresh();
            };
        }
        if (isDownloading) {
            buttonText = getText("button.cancelDownload");
        } else {
            buttonText = getText("button.downloadSong");
        }

        return componentFactory.createButton(buttonText, buttonAction);
    }

    private void updatePanel() {
        guiThread.invokeLater(() -> {
            mainPanel.removeAllComponents();
            fillPanelComponents();
        });
    }

    @Override
    protected AbstractBasePage getPreviousPage() {
        return previousPage;
    }
}
