package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.window.TerminalWindowManager;

import org.tinylog.Logger;

public class ReturnToMainMenuCommand implements NavigationCommand {
    
    private final NavigationContext context;
    
    public ReturnToMainMenuCommand(NavigationContext context) {
        this.context = context;
    }
    
    @Override
    public void execute(TerminalWindowManager wm) {
        Logger.debug("Executing ReturnToMainMenuCommand");
        if (context.getCurrentPage() != null && context.getCurrentPage().getWindow() != null) {
            Logger.debug("Returning to main menu from current page window");
            wm.returnToMainMenu(context.getCurrentPage().getWindow());
        } else {
            Logger.debug("Current page or window is null, showing main menu directly");
            wm.showMainMenu();
        }
    }
    
    @Override
    public String getDescription() {
        return "Return to main menu";
    }
    
    @Override
    public boolean canExecute() {
        return true;
    }
}
