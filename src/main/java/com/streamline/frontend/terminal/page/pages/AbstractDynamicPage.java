package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.backend.Dispatcher;

import org.tinylog.Logger;

public abstract class AbstractDynamicPage extends AbstractBasePage {

    public AbstractDynamicPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    public final BasicWindow updateWindow() {
        try {
            Logger.debug("Starting updateWindow() in {}", getClass().getSimpleName());
            if (!canUpdate()) {
                Logger.debug("Cannot update window in {}", getClass().getSimpleName());
                return window;
            }
            Logger.debug("Calling preUpdateHook() in {}", getClass().getSimpleName());
            preUpdateHook();
            Logger.debug("Clearing window content in {}", getClass().getSimpleName());
            clearWindowContent();
            Logger.debug("Calling rebuildContent() in {}", getClass().getSimpleName());
            rebuildContent();
            Logger.debug("Finalizing update in {}", getClass().getSimpleName());
            finalizeUpdate();
            Logger.debug("Calling postUpdateHook() in {}", getClass().getSimpleName());
            postUpdateHook();
            Logger.debug("Completed updateWindow() in {}, returning window", getClass().getSimpleName());
            return window;
        } catch (Exception e) {
            Logger.error("Error in updateWindow() for {}: {}", getClass().getSimpleName(), e.getMessage(), e);
            handleUpdateError(e);
            return window;
        }
    }

    private void clearWindowContent() {
        if (mainPanel != null) {
            mainPanel.removeAllComponents();
        }
    }

    private void finalizeUpdate() {
        if (mainPanel != null && window != null) {
            window.setComponent(mainPanel);
        }
    }

    protected abstract void rebuildContent();

    /* Override if needed, called before the update starts */
    protected void preUpdateHook() {}

    /* Override if needed, called after the update completes */
    protected void postUpdateHook() {}

    protected boolean canUpdate() {
        return window != null && mainPanel != null;
    }

    protected void handleUpdateError(Exception e) {
        Logger.error("Error updating window for page: " + getClass().getSimpleName(), e);
    }
}
