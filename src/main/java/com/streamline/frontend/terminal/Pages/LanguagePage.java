package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for language selection.
 * @author wellatleastitried
 */
public class LanguagePage extends BasePage {
    
    public LanguagePage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.languageTitle"));
        
        Panel panel = componentFactory.createStandardPanel();
        
        panel.addComponent(componentFactory.createEmptySpace());
        
        // Language selection buttons
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.english"),
            () -> {
                backend.changeLanguage("en");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPage);
                });
            }
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.spanish"),
            () -> {
                LanguagePeer.setLanguage("es");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPage);
                });
            }
        ));
        
        panel.addComponent(componentFactory.createButton(
            LanguagePeer.getText("button.russian"),
            () -> {
                backend.changeLanguage("ru");
                guiThread.invokeLater(() -> {
                    windowManager.rebuildAllWindows();
                    windowManager.transitionTo(windowManager.settingsPage);
                });
            }
        ));
        
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
