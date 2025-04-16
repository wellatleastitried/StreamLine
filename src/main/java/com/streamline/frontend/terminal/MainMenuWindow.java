package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.Driver;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

public class MainMenuWindow extends BaseWindow {
    
    public MainMenuWindow(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("app.title"));
        
        Panel panel = componentFactory.createStandardPanel();
        
        Label titleLabel = componentFactory.createLabel(LanguagePeer.getText("label.greeting"));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(titleLabel);
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.searchForSong"), 
            () -> windowManager.transitionTo(windowManager.searchPage)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.viewLikedSong"), 
            () -> windowManager.transitionTo(windowManager.likedMusicPage)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.playlists"), 
            () -> windowManager.transitionTo(windowManager.playlistPage)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.recentlyPlayed"), 
            () -> windowManager.transitionTo(windowManager.recentlyPlayedPage)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.downloadedMusic"), 
            () -> windowManager.transitionTo(windowManager.downloadedPage)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.help"), 
            () -> windowManager.transitionTo(windowManager.helpMenu)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.settings"), 
            () -> windowManager.transitionTo(windowManager.settingsMenu)
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.quit"), 
            () -> {
                Driver.shutdown();
            }
        ));
        
        window.setComponent(panel);
        return window;
    }
}
