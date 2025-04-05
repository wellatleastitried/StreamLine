package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for displaying liked music.
 */
public class LikedMusicWindow extends BaseWindow {

    public LikedMusicWindow(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow("Liked Music");

        Panel panel = componentFactory.createStandardPanel();

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel("Your Liked Songs"));
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
