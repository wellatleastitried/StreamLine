package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.utilities.RuntimeManager;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.SEARCH);
        navigateBack(contextData);
    }

    private void navigateToLikedMusic() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.LIKED_MUSIC);
        navigateBack(contextData);
    }

    private void navigateToPlaylists() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.PLAYLISTS);
        navigateBack(contextData);
    }

    private void navigateToRecentlyPlayed() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.RECENTLY_PLAYED);
        navigateBack(contextData);
    }

    private void navigateToDownloadedMusic() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.DOWNLOADS);
        navigateBack(contextData);
    }

    private void navigateToHelp() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.HELP);
        navigateBack(contextData);
    }

    private void navigateToSettings() {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.SETTINGS);
        navigateBack(contextData);
    }
}
