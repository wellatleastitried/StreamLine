package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;

import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.commands.NavigationCommand;
import com.streamline.frontend.terminal.navigation.commands.NavigationCommandFactory;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.window.TerminalComponentFactory;
import com.streamline.frontend.terminal.window.TerminalWindowManager;
import com.streamline.utilities.LanguagePeer;

import org.tinylog.Logger;

import java.util.Arrays;
import java.util.Map;

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

    // Public navigation method for external window managers
    public final void performNavigation() {
        navigateBack();
    }
    
    public final void performNavigation(Map<String, Object> contextData) {
        navigateBack(contextData);
    }

    // Navigation methods using the new navigation system
    protected final void navigateBack() {
        Logger.debug("Navigating back from {}", getClass().getSimpleName());

        NavigationContext context = createNavigationContext();

        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        Logger.debug("Created navigation command: {}", command.getDescription());

        executeNavigation(command);
    }
    
    protected final void navigateBack(Map<String, Object> contextData) {
        Logger.debug("Navigating back from {} with context data", getClass().getSimpleName());

        NavigationContext context = createNavigationContext();
        contextData.forEach(context::setContextData);

        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        Logger.debug("Created navigation command with context: {}", command.getDescription());

        executeNavigation(command);
    }

    protected final void navigateTo(NavigationContext context) {
        if (context == null || context.getDestination() == null) {
            Logger.debug("No target destination provided in context for navigation from {}", getClass().getSimpleName());
            return;
        }
        wm.navigateTo(context);
    }

    protected NavigationContext createNavigationContext() {
        return new NavigationContext(this, getPreviousPage());
    }

    protected AbstractBasePage getPreviousPage() {
        return null;
    }
    
    private void executeNavigation(NavigationCommand command) {
        Logger.debug("Executing navigation command in {}: {}", getClass().getSimpleName(), command.getDescription());
        if (command.canExecute()) {
            try {
                Logger.debug("Command can execute, calling execute() with windowManager");
                command.execute(wm);
                Logger.debug("Navigation command executed successfully");
            } catch (Exception e) {
                Logger.error("Navigation failed: " + command.getDescription(), e);
                Logger.debug("Using fallback navigation - returnToMainMenu");
                wm.returnToMainMenu(window);
            }
        } else {
            Logger.debug("Command cannot execute");
        }
    }

    public void setWindowManager(TerminalWindowManager windowManager) {
        this.wm = windowManager;
    }

    public BasicWindow getWindow() {
        return window;
    }
}
