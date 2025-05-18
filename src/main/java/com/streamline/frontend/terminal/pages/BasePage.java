package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.*;

import java.util.Arrays;

/**
 * Base class for all windows in the application. Provides common functionality and enforces consistent structure.
 * @author wellatleastitried
 */
public abstract class BasePage {

    protected final TerminalWindowManager windowManager;
    protected final Dispatcher backend;
    protected final TextGUIThread guiThread;
    protected final TerminalComponentFactory componentFactory;

    public BasePage(Dispatcher backend, TextGUIThread guiThread) {
        this.windowManager = TerminalWindowManager.getInstance();
        this.backend = backend;
        this.guiThread = guiThread;
        this.componentFactory = TerminalComponentFactory.getInstance();
    }

    public abstract BasicWindow createWindow();

    protected BasicWindow createStandardWindow(String title) {
        BasicWindow window = new BasicWindow(title);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        return window;
    }
}
