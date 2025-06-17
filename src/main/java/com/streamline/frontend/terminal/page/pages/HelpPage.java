package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;

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
        setWindowTitle(getText("window.helpTitle"));
        addSpace();
        mainPanel.addComponent(componentFactory.createLabelWithSize(getText("label.searchHelpTitle")));
        mainPanel.addComponent(componentFactory.createLabel(getText("label.searchHelpBody")));
        addSpace();
        mainPanel.addComponent(componentFactory.createLabelWithSize(getText("label.likedMusicTitle")));
        mainPanel.addComponent(componentFactory.createLabel(getText("label.likedMusicBody")));
        addSpace(2);
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.back"),
            () -> wm.returnToMainMenu(window),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        return window;
    }
}
