package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for displaying and managing playlists.
 */
public class PlaylistPage extends AbstractDynamicPage {
    
    public PlaylistPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.playlistsTitle"));
        
        addSpace();
        mainPanel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.playlistsFeature")));
        addSpace();
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.back"),
            () -> windowManager.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        return window;
    }

    @Override
    public BasicWindow updateWindow() {
        return null;
    }
}

