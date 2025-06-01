package com.streamline.frontend.terminal.window;

import com.streamline.frontend.terminal.page.PageState;
import com.streamline.frontend.terminal.page.pages.*;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public class TerminalWindowStateManager {

    private final Set<Class<?>> dirtyWindows;
    private final Map<Class<?>, PageState> pageStates;

    public TerminalWindowStateManager() {
        this.dirtyWindows = new HashSet<>();
        this.pageStates = new HashMap<>();
    }

    public void markWindowDirty(Class<?> pageClass) {
        dirtyWindows.add(pageClass);
    }

    public void rebuildDirtyWindows() {
        // For now, just clear the dirty windows set
        // In the future, this could trigger actual rebuilding
        dirtyWindows.clear();
    }

    public void savePageState(Class<?> pageClass, PageState state) {
        pageStates.put(pageClass, state);
    }

    public PageState getPageState(Class<?> pageClass) {
        return pageStates.get(pageClass);
    }

    public boolean isWindowDirty(Class<?> pageClass) {
        return dirtyWindows.contains(pageClass);
    }

    public void clearDirtyFlag(Class<?> pageClass) {
        dirtyWindows.remove(pageClass);
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
    }

    public void clearAllDirtyFlags() {
        dirtyWindows.clear();
    }
}
