package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.window.TerminalWindowManager;
import org.tinylog.Logger;

public class RebuildCachedSearchCommand implements NavigationCommand {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute(TerminalWindowManager wm) {
        try {
            Logger.debug("Executing rebuild cached search command");
            wm.transitionToCachedSearchPage();
        } catch (Exception e) {
            Logger.error("Failed to execute rebuild cached search command", e);
            throw e;
        }
    }

    @Override
    public String getDescription() {
        return "Rebuild and navigate to cached search page";
    }
}
