package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for displaying help information.
 * @author wellatleastitried
 */
public class HelpPage extends BasePage {
    
    public HelpPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.helpTitle"));
        
        Panel panel = componentFactory.createStandardPanel();
        
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabelWithSize(LanguagePeer.getText("label.searchHelpTitle")));
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.searchHelpBody")));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabelWithSize(LanguagePeer.getText("label.likedMusicTitle")));
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicBody")));
        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createEmptySpace());
        
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
