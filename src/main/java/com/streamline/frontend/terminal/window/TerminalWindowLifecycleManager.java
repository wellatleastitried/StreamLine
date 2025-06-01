package com.streamline.frontend.terminal.window;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import org.tinylog.Logger;

import java.util.Map;
import java.util.HashMap;

public class TerminalWindowLifecycleManager {

    private final Map<Class<?>, BasicWindow> windowCache;

    public TerminalWindowLifecycleManager() {
        this.windowCache = new HashMap<>();
    }

    public <T extends AbstractBasePage> BasicWindow createWindow(Class<T> pageClass, T page) {
        try {
            BasicWindow window = page.createWindow();
            windowCache.put(pageClass, window);
            Logger.debug("Created window for {}", pageClass.getSimpleName());
            return window;
        } catch (Exception e) {
            Logger.error("Failed to create window for {}: {}", pageClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Window creation failed", e);
        }
    }

    public void destroyWindow(Class<?> pageClass) {
        BasicWindow window = windowCache.remove(pageClass);
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

    public boolean hasWindow(Class<?> pageClass) {
        return windowCache.containsKey(pageClass);
    }

    public void cleanupUnusedWindows() {
        // Memory management - remove unused windows
        // For now, this is a placeholder for future optimization
        Logger.debug("Cleanup called for {} cached windows", windowCache.size());
    }

    public void clearAllWindows() {
        windowCache.clear();
        Logger.debug("Cleared all cached windows");
    }

    public int getCachedWindowCount() {
        return windowCache.size();
    }
}
