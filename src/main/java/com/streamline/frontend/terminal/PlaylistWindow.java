package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for displaying and managing playlists.
 */
public class PlaylistWindow extends BaseWindow {
    
    public PlaylistWindow(TerminalWindowManager windowManager, Dispatcher backend, 
                         TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.playlistsTitle"));
        
        Panel panel = componentFactory.createStandardPanel();
        
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.playlistsFeature")));
        panel.addComponent(componentFactory.createEmptySpace());
        
        // Back button
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.back"),
            () -> windowManager.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(panel);
        return window;
    }
}

