package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.navigation.NavigationDestination;

import java.util.HashMap;
import java.util.Map;

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
        // Use the navigation system instead of direct transitionTo call
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("targetDestination", NavigationDestination.LANGUAGE);
        navigateBack(contextData);
    }

    @Override
    protected AbstractBasePage getPreviousPage() {
        // SettingsPage typically returns to main menu
        return null;
    }
}
