package com.streamline.frontend.terminal.Pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;

/**
 * Window for application settings.
 * @author wellatleastitried
 */
public class SettingsPage extends BasePage {

    public SettingsPage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        super(windowManager, backend, guiThread, componentFactory);
    }

    @Override
    public BasicWindow createWindow() {
        BasicWindow window = createStandardWindow(LanguagePeer.getText("window.settingsTitle"));

        Panel panel = componentFactory.createStandardPanel();

        panel.addComponent(componentFactory.createEmptySpace());

        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.clearCache"),
                    () -> backend.clearCache()
        ));

        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.chooseLanguage"),
                    () -> windowManager.transitionTo(windowManager.languagePage)
        ));

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
