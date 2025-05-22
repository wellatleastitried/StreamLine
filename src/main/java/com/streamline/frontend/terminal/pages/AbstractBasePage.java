package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;

import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;
import com.streamline.utilities.LanguagePeer;

import java.util.Arrays;

/**
 * Base class for all windows in the application. Provides common functionality and enforces consistent structure.
 * @author wellatleastitried
 */
public abstract class AbstractBasePage {

    protected final Panel mainPanel;

    protected TerminalWindowManager windowManager;
    protected final Dispatcher backend;
    protected final TextGUIThread guiThread;
    protected final TerminalComponentFactory componentFactory;

    public AbstractBasePage(Dispatcher backend, TextGUIThread guiThread) {
        this.backend = backend;
        this.guiThread = guiThread;
        this.componentFactory = TerminalComponentFactory.getInstance();
        this.mainPanel = componentFactory.createStandardPanel();
    }

    public abstract BasicWindow createWindow();

    protected BasicWindow createStandardWindow(String title) {
        BasicWindow window = new BasicWindow(title);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        return window;
    }

    protected String get(String key) {
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

    public void setWindowManager(TerminalWindowManager windowManager) {
        this.windowManager = windowManager;
    }

}
