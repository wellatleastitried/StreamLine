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
        windowManager.transitionTo(windowManager.searchPageWindow);
    }

    private void navigateToLikedMusic() {
        windowManager.transitionTo(windowManager.likedMusicPageWindow);
    }

    private void navigateToPlaylists() {
        windowManager.transitionTo(windowManager.playlistPageWindow);
    }

    private void navigateToRecentlyPlayed() {
        windowManager.transitionTo(windowManager.recentlyPlayedPageWindow);
    }

    private void navigateToDownloadedMusic() {
        windowManager.transitionTo(windowManager.downloadedPageWindow);
    }

    private void navigateToHelp() {
        windowManager.transitionTo(windowManager.helpPageWindow);
    }

    private void navigateToSettings() {
        windowManager.transitionTo(windowManager.settingsPageWindow);
    }
}
