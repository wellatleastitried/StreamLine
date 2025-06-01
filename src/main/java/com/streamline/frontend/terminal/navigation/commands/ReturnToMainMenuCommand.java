package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.window.TerminalWindowManager;

public class ReturnToMainMenuCommand implements NavigationCommand {
    
    private final NavigationContext context;
    
    public ReturnToMainMenuCommand(NavigationContext context) {
        this.context = context;
    }
    
    @Override
    public void execute(TerminalWindowManager windowManager) {
        if (context.getCurrentPage() != null && context.getCurrentPage().getWindow() != null) {
            windowManager.returnToMainMenu(context.getCurrentPage().getWindow());
        } else {
            windowManager.showMainMenu();
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
