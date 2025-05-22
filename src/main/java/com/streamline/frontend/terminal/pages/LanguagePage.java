package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for language selection.
 * @author wellatleastitried
 */
public class LanguagePage extends AbstractBasePage {
    
    public LanguagePage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.languageTitle"));
        
        addSpace();
        
        /* Language selection buttons */
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.english"),
            () -> {
                backend.changeLanguage("en");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPageWindow);
                });
            }
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.spanish"),
            () -> {
                LanguagePeer.setLanguage("es");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPageWindow);
                });
            }
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.russian"),
            () -> {
                backend.changeLanguage("ru");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPageWindow);
                });
            }
        ));
        
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
}
