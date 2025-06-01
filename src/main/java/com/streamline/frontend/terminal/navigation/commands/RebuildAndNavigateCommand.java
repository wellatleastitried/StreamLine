package com.streamline.frontend.terminal.navigation.commands;

import com.googlecode.lanterna.gui2.Button;

import com.streamline.frontend.terminal.page.pages.AbstractBasePage;
import com.streamline.frontend.terminal.window.TerminalWindowManager;

import java.util.Map;

public class RebuildAndNavigateCommand<T extends AbstractBasePage> implements NavigationCommand {
    
    private final Class<T> targetPageClass;
    private final Object[] rebuildArgs;
    private final boolean rebuildBefore;
    
    public RebuildAndNavigateCommand(Class<T> targetPageClass) {
        this(targetPageClass, true, new Object[0]);
    }
    
    public RebuildAndNavigateCommand(Class<T> targetPageClass, Object... rebuildArgs) {
        this(targetPageClass, true, rebuildArgs);
    }
    
    public RebuildAndNavigateCommand(Class<T> targetPageClass, boolean rebuildBefore, Object... rebuildArgs) {
        this.targetPageClass = targetPageClass;
        this.rebuildBefore = rebuildBefore;
        this.rebuildArgs = rebuildArgs;
    }
    
    @Override
    public void execute(TerminalWindowManager windowManager) {
        if (rebuildBefore) {
            rebuildPage(windowManager);
        }
        
        // Navigate to the rebuilt page
        NavigateToPageCommand<T> navigateCommand = new NavigateToPageCommand<>(targetPageClass);
        navigateCommand.execute(windowManager);
    }
    
    private void rebuildPage(TerminalWindowManager windowManager) {
        String className = targetPageClass.getSimpleName();
        
        // Rebuild dynamic pages that support it
        switch (className) {
            case "LikedMusicPage":
            case "PlaylistPage":
            case "RecentlyPlayedPage":
            case "DownloadedMusicPage":
                windowManager.rebuildDynamicWindows();
                break;
            case "SearchPage":
                if (rebuildArgs.length > 0 && rebuildArgs[0] instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<Integer, Button> searchResults = 
                        (Map<Integer, Button>) rebuildArgs[0];
                    windowManager.rebuildSearchPage(searchResults);
                } else {
                    windowManager.rebuildSearchPage(null);
                }
                break;
            default:
                // For other pages, rebuild all windows
                windowManager.rebuildAllWindows();
                break;
        }
    }
    
    @Override
    public String getDescription() {
        return "Rebuild and navigate to " + targetPageClass.getSimpleName();
    }
    
    @Override
    public boolean canExecute() {
        return targetPageClass != null;
    }
}
