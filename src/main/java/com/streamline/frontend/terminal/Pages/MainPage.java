package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.RuntimeManager;

import org.tinylog.Logger;

public class MainPage extends BasePage {
    
    public MainPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
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
        Logger.debug("MainPage: Search button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.viewLikedSong"), 
            () -> windowManager.transitionTo(windowManager.likedMusicPage)
        ));
        Logger.debug("MainPage: Liked music button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.playlists"), 
            () -> windowManager.transitionTo(windowManager.playlistPage)
        ));
        Logger.debug("MainPage: Playlists button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.recentlyPlayed"), 
            () -> windowManager.transitionTo(windowManager.recentlyPlayedPage)
        ));
        Logger.debug("MainPage: Recently played button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.downloadedMusic"), 
            () -> windowManager.transitionTo(windowManager.downloadedPage)
        ));
        Logger.debug("MainPage: Downloaded music button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.help"), 
            () -> windowManager.transitionTo(windowManager.helpPage)
        ));
        Logger.debug("MainPage: Help button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.settings"), 
            () -> windowManager.transitionTo(windowManager.settingsPage)
        ));
        Logger.debug("MainPage: Settings button created.");
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.quit"), 
            () -> {
                RuntimeManager.shutdown();
            }
        ));
        Logger.debug("MainPage: Quit button created.");
        
        window.setComponent(panel);
        Logger.debug("MainPage: Window created.");
        return window;
    }
}
