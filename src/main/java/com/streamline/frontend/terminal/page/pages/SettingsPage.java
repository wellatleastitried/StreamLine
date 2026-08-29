package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.Page;

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
                    () -> navigateTo(Page.LANGUAGE)
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

    @Override
    public String getPageName() {
        return Page.SETTINGS;
    }
}
