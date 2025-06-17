package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RuntimeManager;

public class MainPage extends AbstractBasePage {
    
    public MainPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    @Override
    public BasicWindow createWindow() {
        setWindowTitle(getText("app.title"));
        
        Label titleLabel = componentFactory.createLabel(getText("label.greeting"));
        addSpace();
        mainPanel.addComponent(titleLabel);
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.searchForSong"), 
            () -> navigateToSearch()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.viewLikedSong"), 
            () -> navigateToLikedMusic()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.playlists"), 
            () -> navigateToPlaylists()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.recentlyPlayed"), 
            () -> navigateToRecentlyPlayed()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.downloadedMusic"), 
            () -> navigateToDownloadedMusic()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.help"), 
            () -> navigateToHelp()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.settings"), 
            () -> navigateToSettings()
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.quit"), 
            () -> {
                RuntimeManager.shutdown();
            }
        ));
        
        window.setComponent(mainPanel);
        return window;
    }
    
    // Navigation methods
    private void navigateToSearch() {
        wm.transitionTo(wm.searchPageWindow);
    }

    private void navigateToLikedMusic() {
        wm.transitionTo(wm.likedMusicPageWindow);
    }

    private void navigateToPlaylists() {
        wm.transitionTo(wm.playlistPageWindow);
    }

    private void navigateToRecentlyPlayed() {
        wm.transitionTo(wm.recentlyPlayedPageWindow);
    }

    private void navigateToDownloadedMusic() {
        wm.transitionTo(wm.downloadedPageWindow);
    }

    private void navigateToHelp() {
        wm.transitionTo(wm.helpPageWindow);
    }

    private void navigateToSettings() {
        wm.transitionTo(wm.settingsPageWindow);
    }
}
