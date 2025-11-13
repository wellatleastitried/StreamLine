package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;

import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.Page;
import com.streamline.frontend.terminal.window.TerminalComponentFactory;
import com.streamline.frontend.terminal.window.TerminalWindowManager;
import com.streamline.utilities.LanguagePeer;

import org.tinylog.Logger;

import java.util.Arrays;

/**
 * Base class for all windows in the application. Provides common functionality and enforces consistent structure.
 * @author wellatleastitried
 */
public abstract class AbstractBasePage {

    protected BasicWindow window;
    protected final Panel mainPanel;

    protected TerminalWindowManager wm;
    protected final Dispatcher backend;
    protected final TextGUIThread guiThread;
    protected final TerminalComponentFactory componentFactory;

    public AbstractBasePage(Dispatcher backend, TextGUIThread guiThread) {
        this.backend = backend;
        this.guiThread = guiThread;
        this.componentFactory = TerminalComponentFactory.getInstance();
        this.window = createStandardWindow();
        this.mainPanel = componentFactory.createStandardPanel();
    }

    public abstract BasicWindow createWindow();

    protected BasicWindow createStandardWindow() {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        return window;
    }

    protected void setWindowTitle(String title) {
        if (window != null) {
            window.setTitle(title);
        }
    }

    protected String getText(String key) {
        return LanguagePeer.getText(key);
    }

    protected Button createButton(String text, Runnable action, int width, int height) {
        return componentFactory.createButton(text, action, width, height);
    }

    protected Label createLabel(String text) {
        return componentFactory.createLabel(text);
    }

    protected void addSpace() {
        addSpace(1);
    }

    protected void addSpace(int count) {
        for (int i = 0; i < count; i++) {
            mainPanel.addComponent(componentFactory.createEmptySpace());
        }
    }

    protected final void navigateBack() {
        Logger.debug("Navigating back from {}", getPageName());
        // TODO: Having a window manager should be a guarantee here, consider throwing an exception if not present
        if (hasWindowManager()) {
            wm.navigateBack();
        }
    }
    
    protected final void navigateTo(String pageName) {
        Logger.debug("Navigating to {} from {}", pageName, getPageName());
        if (hasWindowManager()) {
            wm.navigateToPage(getPageName(), pageName);
        }
    }
    
    protected final void navigateToMainMenu() {
        Logger.debug("Navigating to main menu from {}", getPageName());
        if (hasWindowManager()) {
            wm.showMainMenu();
        }
    }

    public void setWindowManager(TerminalWindowManager windowManager) {
        this.wm = windowManager;
    }

    public boolean hasWindowManager() {
        return wm != null;
    }

    public BasicWindow getWindow() {
        return window;
    }

    public abstract String getPageName();
}
