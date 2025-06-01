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
            if (!canUpdate()) {
                return window;
            }
            preUpdateHook();
            clearWindowContent();
            rebuildContent();
            finalizeUpdate();
            postUpdateHook();
            return window;
        } catch (Exception e) {
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
