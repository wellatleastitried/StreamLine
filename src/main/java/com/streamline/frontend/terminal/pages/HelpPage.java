package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for displaying help information.
 * @author wellatleastitried
 */
public class HelpPage extends AbstractBasePage {
    
    public HelpPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.helpTitle"));
        
        addSpace();
        mainPanel.addComponent(componentFactory.createLabelWithSize(LanguagePeer.getText("label.searchHelpTitle")));
        mainPanel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.searchHelpBody")));
        addSpace();
        mainPanel.addComponent(componentFactory.createLabelWithSize(LanguagePeer.getText("label.likedMusicTitle")));
        mainPanel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.likedMusicBody")));
        addSpace(2);
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.back"),
            () -> windowManager.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        return window;
    }
}
