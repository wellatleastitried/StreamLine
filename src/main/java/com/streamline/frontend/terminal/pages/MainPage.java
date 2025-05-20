package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.RuntimeManager;

public class MainPage extends AbstractBasePage{
    
    public MainPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("app.title"));
        
        Label titleLabel = componentFactory.createLabel(LanguagePeer.getText("label.greeting"));
        addSpace();
        mainPanel.addComponent(titleLabel);
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.searchForSong"), 
            () -> windowManager.transitionTo(windowManager.searchPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.viewLikedSong"), 
            () -> windowManager.transitionTo(windowManager.likedMusicPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.playlists"), 
            () -> windowManager.transitionTo(windowManager.playlistPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.recentlyPlayed"), 
            () -> windowManager.transitionTo(windowManager.recentlyPlayedPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.downloadedMusic"), 
            () -> windowManager.transitionTo(windowManager.downloadedPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.help"), 
            () -> windowManager.transitionTo(windowManager.helpPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.settings"), 
            () -> windowManager.transitionTo(windowManager.settingsPage)
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.quit"), 
            () -> {
                RuntimeManager.shutdown();
            }
        ));
        
        window.setComponent(mainPanel);
        return window;
    }
}
