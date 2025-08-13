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
            Logger.debug("Starting updateWindow() in {}", getPageName());
            if (!canUpdate()) {
                Logger.debug("Cannot update window in {}", getPageName());
                return window;
            }
            Logger.debug("Calling preUpdateHook() in {}", getPageName());
            preUpdateHook();
            Logger.debug("Clearing window content in {}", getPageName());
            clearWindowContent();
            Logger.debug("Calling rebuildContent() in {}", getPageName());
            rebuildContent();
            Logger.debug("Finalizing update in {}", getPageName());
            finalizeUpdate();
            Logger.debug("Calling postUpdateHook() in {}", getPageName());
            postUpdateHook();
            Logger.debug("Completed updateWindow() in {}, returning window", getPageName());
            return window;
        } catch (Exception e) {
            Logger.error("Error in updateWindow() for {}: {}", getPageName(), e.getMessage(), e);
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
        Logger.error("Error updating window for page: " + getPageName(), e);
    }
}
