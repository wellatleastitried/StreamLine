package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.*;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.Pages;

/**
 * Window for language selection.
 * @author wellatleastitried
 */
public class LanguagePage extends AbstractBasePage {
    
    public LanguagePage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
        setWindowTitle(getText("window.languageTitle"));
    }

    @Override
    public BasicWindow createWindow() {
        addSpace();
        
        /* Language selection buttons */
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.english"),
            () -> handleLanguageChange("en")
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.spanish"),
            () -> handleLanguageChange("es")
        ));
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.russian"),
            () -> handleLanguageChange("ru")
        ));
        
        addSpace();
        
        mainPanel.addComponent(componentFactory.createButton(
            getText("button.back"),
            () -> wm.navigateBack(),
            componentFactory.getButtonWidth() / 3,
            componentFactory.getButtonHeight() / 2
        ));
        
        window.setComponent(mainPanel);
        return window;
    }

    private void handleLanguageChange(String languageCode) {
        backend.changeLanguage(languageCode);
        guiThread.invokeLater(() -> {
            wm.triggerPageRebuild();
            wm.transitionTo(Pages.MAIN_MENU_PAGE);
        });
    }

    @Override
    public String getPageName() {
        return Pages.LANGUAGE_PAGE;
    }
}
