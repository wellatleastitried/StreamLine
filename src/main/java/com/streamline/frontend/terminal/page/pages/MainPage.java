package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RuntimeManager;
import com.streamline.frontend.terminal.page.Page;

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
        wm.transitionTo(Page.SEARCH);
    }

    private void navigateToLikedMusic() {
        wm.transitionTo(Page.LIKED_MUSIC);
    }

    private void navigateToPlaylists() {
        wm.transitionTo(Page.PLAYLISTS);
    }

    private void navigateToRecentlyPlayed() {
        wm.transitionTo(Page.RECENTLY_PLAYED);
    }

    private void navigateToDownloadedMusic() {
        wm.transitionTo(Page.DOWNLOADED_MUSIC);
    }

    private void navigateToHelp() {
        wm.transitionTo(Page.HELP);
    }

    private void navigateToSettings() {
        wm.transitionTo(Page.SETTINGS);
    }

    @Override
    public String getPageName() {
        return Page.MAIN_MENU;
    }
}
