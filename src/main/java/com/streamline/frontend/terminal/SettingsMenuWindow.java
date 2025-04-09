package com.streamline.frontend.terminal;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for application settings.
 * @author wellatleastitried
 */
public class SettingsMenuWindow extends BaseWindow {

    public SettingsMenuWindow(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.settingsTitle"));

        Panel panel = componentFactory.createStandardPanel();

        panel.addComponent(componentFactory.createEmptySpace());

        // Clear cache button
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.clearCache"),
                    () -> backend.clearCache()
        ));

        // Language selection button
        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.chooseLanguage"),
                    () -> windowManager.transitionTo(windowManager.languagePage)
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
