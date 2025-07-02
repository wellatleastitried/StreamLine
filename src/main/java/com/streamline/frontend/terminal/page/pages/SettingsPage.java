package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;

/**
 * Window for application settings.
 * @author wellatleastitried
 */
public class SettingsPage extends AbstractBasePage {

    public SettingsPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
        setWindowTitle(getText("window.settingsTitle"));
    }

    @Override
    public BasicWindow createWindow() {

        addSpace();

        mainPanel.addComponent(componentFactory.createButton(
                    getText("button.clearCache"),
                    () -> backend.clearCache()
        ));

        mainPanel.addComponent(componentFactory.createButton(
                    getText("button.chooseLanguage"),
                    () -> navigateToLanguagePage()
        ));

        addSpace();

        mainPanel.addComponent(componentFactory.createButton(
                    getText("button.back"),
                    () -> navigateBack(),
                    componentFactory.getButtonWidth() / 3,
                    componentFactory.getButtonHeight() / 2
        ));

        window.setComponent(mainPanel);
        return window;
    }

    private void navigateToLanguagePage() {
        navigateTo(LanguagePage.class);
    }
}
