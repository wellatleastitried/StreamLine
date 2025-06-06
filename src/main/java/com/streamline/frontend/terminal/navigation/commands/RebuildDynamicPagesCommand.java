package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.window.NewTerminalWindowManager;

import org.tinylog.Logger;

public class RebuildDynamicPagesCommand implements NavigationCommand {
    
    private final NavigationDestination targetDestination;
    
    public RebuildDynamicPagesCommand(NavigationDestination targetDestination) {
        this.targetDestination = targetDestination;
    }
    
    @Override
    public void execute(NewTerminalWindowManager windowManager) {
        Logger.debug("RebuildDynamicPagesCommand.execute() - rebuilding dynamic windows");
        // Rebuild dynamic windows first
        windowManager.rebuildDynamicWindows();
        
        // Then navigate to the target destination
        if (targetDestination != null) {
            Logger.debug("Navigating to target destination: {}", targetDestination);
            NavigateToPageCommand<?> navigateCommand = 
                new NavigateToPageCommand<>(targetDestination.getPageClass());
            navigateCommand.execute(windowManager);
        }
    }
    
    @Override
    public String getDescription() {
        if (targetDestination != null) {
            return "Rebuild dynamic pages and navigate to " + targetDestination.getPageClass().getSimpleName();
        } else {
            return "Rebuild dynamic pages";
        }
    }
    
    @Override
    public boolean canExecute() {
        return true;
    }
}
