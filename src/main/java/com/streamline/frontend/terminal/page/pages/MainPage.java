package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.RuntimeManager;
import com.streamline.frontend.terminal.page.Pages;

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
        wm.transitionTo(Pages.SEARCH_PAGE);
    }

    private void navigateToLikedMusic() {
        wm.transitionTo(Pages.LIKED_MUSIC_PAGE);
    }

    private void navigateToPlaylists() {
        wm.transitionTo(Pages.PLAYLISTS_PAGE);
    }

    private void navigateToRecentlyPlayed() {
        wm.transitionTo(Pages.RECENTLY_PLAYED_PAGE);
    }

    private void navigateToDownloadedMusic() {
        wm.transitionTo(Pages.DOWNLOADED_MUSIC_PAGE);
    }

    private void navigateToHelp() {
        wm.transitionTo(Pages.HELP_PAGE);
    }

    private void navigateToSettings() {
        wm.transitionTo(Pages.SETTINGS_PAGE);
    }

    @Override
    public String getPageName() {
        return Pages.MAIN_MENU_PAGE;
    }
}
