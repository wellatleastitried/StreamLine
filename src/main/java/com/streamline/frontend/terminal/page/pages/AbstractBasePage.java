package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;

import com.streamline.backend.Dispatcher;
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
        Logger.debug("Navigating back from {}", getClass().getSimpleName());
        if (wm != null) {
            wm.returnToMainMenu();
        }
    }
    
    protected final void navigateTo(Class<? extends AbstractBasePage> pageClass) {
        Logger.debug("Navigating to {} from {}", pageClass.getSimpleName(), getClass().getSimpleName());
        if (wm != null) {
            wm.navigateToPage(pageClass);
        }
    }
    
    protected final void navigateToMainMenu() {
        Logger.debug("Navigating to main menu from {}", getClass().getSimpleName());
        if (wm != null) {
            wm.showMainMenu();
        }
    }

    public void setWindowManager(TerminalWindowManager windowManager) {
        this.wm = windowManager;
    }

    public BasicWindow getWindow() {
        return window;
    }
}
