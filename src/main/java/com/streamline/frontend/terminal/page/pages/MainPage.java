package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RuntimeManager;
import com.streamline.utilities.internal.StreamLineConstants;

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
    
    private void navigateToSearch() {
        wm.transitionTo(StreamLineConstants.SEARCH_PAGE);
    }

    private void navigateToLikedMusic() {
        wm.transitionTo(StreamLineConstants.LIKED_MUSIC_PAGE);
    }

    private void navigateToPlaylists() {
        wm.transitionTo(StreamLineConstants.PLAYLISTS_PAGE);
    }

    private void navigateToRecentlyPlayed() {
        wm.transitionTo(StreamLineConstants.RECENTLY_PLAYED_PAGE);
    }

    private void navigateToDownloadedMusic() {
        wm.transitionTo(StreamLineConstants.DOWNLOADED_MUSIC_PAGE);
    }

    private void navigateToHelp() {
        wm.transitionTo(StreamLineConstants.HELP_PAGE);
    }

    private void navigateToSettings() {
        wm.transitionTo(StreamLineConstants.SETTINGS_PAGE);
    }

    @Override
    public String getPageName() {
        return StreamLineConstants.MAIN_MENU_PAGE;
    }
}
