package com.streamline.frontend.terminal.Pages;

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

    public BasePage(TerminalWindowManager windowManager, Dispatcher backend, TextGUIThread guiThread, TerminalComponentFactory componentFactory) {
        this.windowManager = windowManager;
        this.backend = backend;
        this.guiThread = guiThread;
        this.componentFactory = componentFactory;
    }

    public abstract BasicWindow createWindow();

    protected BasicWindow createStandardWindow(String title) {
        BasicWindow window = new BasicWindow(title);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        return window;
    }
}
