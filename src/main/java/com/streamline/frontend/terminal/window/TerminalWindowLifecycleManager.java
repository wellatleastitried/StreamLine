package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.gui2.Window;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.page.pages.*;
import org.tinylog.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Enhanced window lifecycle manager that handles window creation, caching, transitions, and cleanup.
 * Provides centralized window management for all terminal operations.
 */
public class TerminalWindowLifecycleManager {

    private final Map<Class<?>, BasicWindow> windowCache;
    private final Map<Class<?>, AbstractBasePage> pageCache;
    private final WindowBasedTextGUI textGUI;
    private final TextGUIThread guiThread;
    private final Dispatcher backend;

    public TerminalWindowLifecycleManager(WindowBasedTextGUI textGUI, TextGUIThread guiThread, Dispatcher backend) {
        this.windowCache = new HashMap<>();
        this.pageCache = new HashMap<>();
        this.textGUI = textGUI;
        this.guiThread = guiThread;
        this.backend = backend;
        Logger.debug("Initialized window lifecycle manager");
    }

    public <T extends AbstractBasePage> BasicWindow createWindow(Class<T> pageClass, T page) {
        try {
            BasicWindow window = page.createWindow();
            windowCache.put(pageClass, window);
            pageCache.put(pageClass, page);
            Logger.debug("Created window for {}", pageClass.getSimpleName());
            return window;
        } catch (Exception e) {
            Logger.error("Failed to create window for {}: {}", pageClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Window creation failed", e);
        }
    }

    public void destroyWindow(Class<?> pageClass) {
        BasicWindow window = windowCache.remove(pageClass);
        pageCache.remove(pageClass);
        if (window != null) {
            Logger.debug("Destroyed window for {}", pageClass.getSimpleName());
        }
    }

    public void invalidateWindow(Class<?> pageClass) {
        BasicWindow window = windowCache.get(pageClass);
        if (window != null) {
            window.invalidate();
            Logger.debug("Invalidated window for {}", pageClass.getSimpleName());
        }
    }

    public BasicWindow getWindow(Class<?> pageClass) {
        return windowCache.get(pageClass);
    }

    public AbstractBasePage getPage(Class<?> pageClass) {
        return pageCache.get(pageClass);
    }

    public boolean hasWindow(Class<?> pageClass) {
        return windowCache.containsKey(pageClass);
    }

    public void cleanupUnusedWindows() {
        // Memory management - remove unused windows
        Logger.debug("Cleanup called for {} cached windows", windowCache.size());
    }

    public void clearAllWindows() {
        windowCache.clear();
        pageCache.clear();
        Logger.debug("Cleared all cached windows");
    }

    public int getCachedWindowCount() {
        return windowCache.size();
    }

    // Window transition and management
    public void transitionTo(BasicWindow targetWindow) {
        Logger.debug("Transitioning to window: {}", targetWindow != null ? targetWindow.toString() : "null");
        guiThread.invokeLater(() -> {
            Logger.debug("In guiThread, getting current windows");
            Collection<Window> openWindows = textGUI.getWindows();
            Logger.debug("Current open window count: {}", openWindows.size());
            for (Window openWindow : openWindows) {
                if (openWindow != targetWindow) {
                    Logger.debug("Removing window: {}", openWindow);
                    textGUI.removeWindow(openWindow);
                }
            }
            if (!openWindows.contains(targetWindow)) {
                Logger.debug("Adding target window: {}", targetWindow);
                textGUI.addWindowAndWait(targetWindow);
                Logger.debug("Window added");
            } else {
                Logger.debug("Target window already in open windows, not adding");
            }
        });
    }

    public void transitionTo(Class<?> pageClass) {
        Logger.debug("Transitioning to page class: {}", pageClass.getSimpleName());
        BasicWindow targetWindow = windowCache.get(pageClass);
        Logger.debug("Window from cache: {}", targetWindow != null ? "found" : "not found");
        if (targetWindow != null) {
            transitionTo(targetWindow);
        } else {
            Logger.warn("Window for {} not found in cache during transition", pageClass.getSimpleName());
        }
    }

    public void showMainMenu(BasicWindow mainWindow) {
        guiThread.invokeLater(() -> {
            mainWindow.setVisible(true);
            Collection<Window> openWindows = textGUI.getWindows();
            for (Window window : openWindows) {
                if (window != mainWindow) {
                    textGUI.removeWindow(window);
                }
            }
            if (!openWindows.contains(mainWindow)) {
                textGUI.addWindowAndWait(mainWindow);
            }
        });
    }

    public void showMainMenu() {
        // Find the main window in cache
        BasicWindow mainWindow = windowCache.get(com.streamline.frontend.terminal.page.pages.MainPage.class);
        if (mainWindow != null) {
            showMainMenu(mainWindow);
        } else {
            Logger.warn("Main window not found in cache for showMainMenu()");
        }
    }

    public void returnToMainMenu(BasicWindow currentWindow, BasicWindow mainWindow) {
        guiThread.invokeLater(() -> {
            textGUI.removeWindow(currentWindow);
            showMainMenu(mainWindow);
        });
    }

    public void closeAllWindows() {
        guiThread.invokeLater(() -> {
            try {
                for (Window window : textGUI.getWindows()) {
                    textGUI.removeWindow(window);
                }
            } catch (IllegalStateException e) {
                Logger.warn("Exception while cleaning up terminal interface: {}", e.getMessage());
            }
        });
    }

    // Window rebuilding support
    public void rebuildWindow(Class<?> pageClass) {
        AbstractBasePage page = pageCache.get(pageClass);
        if (page != null) {
            try {
                BasicWindow newWindow = page.createWindow();
                windowCache.put(pageClass, newWindow);
                Logger.debug("Rebuilt window for {}", pageClass.getSimpleName());
            } catch (Exception e) {
                Logger.error("Failed to rebuild window for {}: {}", pageClass.getSimpleName(), e.getMessage());
            }
        }
    }

    public BasicWindow rebuildDynamicWindow(Class<?> pageClass) {
        Logger.debug("Starting to rebuild dynamic window for {}", pageClass.getSimpleName());
        AbstractBasePage page = pageCache.get(pageClass);
        Logger.debug("Page from cache: {}", page == null ? "null" : page.getClass().getSimpleName());
        if (page instanceof AbstractDynamicPage) {
            try {
                Logger.debug("Calling updateWindow() on {}", pageClass.getSimpleName());
                BasicWindow updatedWindow = ((AbstractDynamicPage) page).updateWindow();
                Logger.debug("Updated window result: {}", updatedWindow == null ? "null" : "not null");
                windowCache.put(pageClass, updatedWindow);
                Logger.debug("Rebuilt dynamic window for {}", pageClass.getSimpleName());
                return updatedWindow;
            } catch (Exception e) {
                Logger.error("Failed to rebuild dynamic window for {}: {}", pageClass.getSimpleName(), e.getMessage(), e);
            }
        } else {
            Logger.debug("Page {} is not an AbstractDynamicPage", pageClass.getSimpleName());
        }
        return windowCache.get(pageClass);
    }

    // Screen refresh support
    public void refresh() {
        guiThread.invokeLater(() -> {
            try {
                textGUI.getScreen().refresh();
            } catch (Exception e) {
                Logger.error("Error while refreshing screen: {}", e.getMessage());
            }
        });
    }
}
