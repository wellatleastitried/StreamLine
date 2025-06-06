package com.streamline.frontend.terminal.window;

import com.streamline.frontend.terminal.page.PageState;
import com.streamline.frontend.terminal.page.pages.*;
import org.tinylog.Logger;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Enhanced window state manager that handles window dirty flags, page states, and rebuild triggers.
 * Provides centralized state management for all terminal windows.
 */
public class TerminalWindowStateManager {

    private final Set<Class<?>> dirtyWindows;
    private final Map<Class<?>, PageState> pageStates;
    private final Map<Class<?>, Boolean> rebuildFlags;

    public TerminalWindowStateManager() {
        this.dirtyWindows = new HashSet<>();
        this.pageStates = new HashMap<>();
        this.rebuildFlags = new HashMap<>();
        initializeRebuildFlags();
    }

    private void initializeRebuildFlags() {
        // Initialize rebuild flags for specific pages
        rebuildFlags.put(SearchPage.class, false);
        rebuildFlags.put(PlaylistPage.class, false);
        Logger.debug("Initialized rebuild flags for window state manager");
    }

    public void markWindowDirty(Class<?> pageClass) {
        dirtyWindows.add(pageClass);
        Logger.debug("Marked window dirty: {}", pageClass.getSimpleName());
    }

    public void rebuildDirtyWindows() {
        if (!dirtyWindows.isEmpty()) {
            Logger.debug("Rebuilding {} dirty windows", dirtyWindows.size());
            dirtyWindows.clear();
        }
    }

    public void savePageState(Class<?> pageClass, PageState state) {
        pageStates.put(pageClass, state);
        Logger.debug("Saved page state for {}", pageClass.getSimpleName());
    }

    public PageState getPageState(Class<?> pageClass) {
        return pageStates.get(pageClass);
    }

    public boolean isWindowDirty(Class<?> pageClass) {
        return dirtyWindows.contains(pageClass);
    }

    public void clearDirtyFlag(Class<?> pageClass) {
        dirtyWindows.remove(pageClass);
        Logger.debug("Cleared dirty flag for {}", pageClass.getSimpleName());
    }

    public Set<Class<?>> getDirtyWindows() {
        return new HashSet<>(dirtyWindows);
    }

    public void markDynamicWindowsDirty() {
        // Mark all dynamic pages as dirty
        markWindowDirty(LikedMusicPage.class);
        markWindowDirty(PlaylistPage.class);
        markWindowDirty(RecentlyPlayedPage.class);
        markWindowDirty(DownloadedMusicPage.class);
        Logger.debug("Marked all dynamic windows as dirty");
    }

    public void clearAllDirtyFlags() {
        dirtyWindows.clear();
        Logger.debug("Cleared all dirty flags");
    }

    // Rebuild flag management
    public void setRebuildFlag(Class<?> pageClass, boolean shouldRebuild) {
        rebuildFlags.put(pageClass, shouldRebuild);
        Logger.debug("Set rebuild flag for {} to {}", pageClass.getSimpleName(), shouldRebuild);
    }

    public boolean getRebuildFlag(Class<?> pageClass) {
        return rebuildFlags.getOrDefault(pageClass, false);
    }

    public void clearRebuildFlag(Class<?> pageClass) {
        rebuildFlags.put(pageClass, false);
        Logger.debug("Cleared rebuild flag for {}", pageClass.getSimpleName());
    }

    public void clearAllRebuildFlags() {
        rebuildFlags.replaceAll((k, v) -> false);
        Logger.debug("Cleared all rebuild flags");
    }

    // Convenience methods for specific pages
    public boolean shouldRebuildSearchPage() {
        return getRebuildFlag(SearchPage.class);
    }

    public void setRebuildSearchPageFlag(boolean shouldRebuild) {
        setRebuildFlag(SearchPage.class, shouldRebuild);
    }

    public boolean shouldRebuildPlaylistPage() {
        return getRebuildFlag(PlaylistPage.class);
    }

    public void setRebuildPlaylistPageFlag(boolean shouldRebuild) {
        setRebuildFlag(PlaylistPage.class, shouldRebuild);
    }

    // State validation and cleanup
    public void validateState() {
        Logger.debug("Validating window state - {} dirty windows, {} page states, {} rebuild flags", 
                    dirtyWindows.size(), pageStates.size(), rebuildFlags.size());
    }

    public void cleanup() {
        clearAllDirtyFlags();
        clearAllRebuildFlags();
        pageStates.clear();
        Logger.debug("Cleaned up window state manager");
    }
}
