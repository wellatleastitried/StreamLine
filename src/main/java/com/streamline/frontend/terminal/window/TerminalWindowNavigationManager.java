package com.streamline.frontend.terminal.window;

import com.streamline.frontend.terminal.navigation.commands.NavigationCommand;
import com.streamline.frontend.terminal.navigation.commands.NavigationCommandFactory;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import org.tinylog.Logger;

import java.util.Stack;

public class TerminalWindowNavigationManager {

    private final Stack<Class<?>> navigationHistory;
    private final TerminalWindowManager legacyWindowManager;

    public TerminalWindowNavigationManager(TerminalWindowManager legacyWindowManager) {
        this.navigationHistory = new Stack<>();
        this.legacyWindowManager = legacyWindowManager;
    }

    public void navigateTo(Class<?> targetPageClass) {
        Logger.debug("Navigating to {}", targetPageClass.getSimpleName());
        // Add to navigation history
        navigationHistory.push(targetPageClass);
        
        // Create navigation context for the target page
        NavigationDestination destination = NavigationDestination.fromPageClass(targetPageClass);
        NavigationContext context = new NavigationContext(null, null);
        context.setContextData("directDestination", destination);
        
        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        executeNavigation(command);
    }

    public void navigateBack() {
        Logger.debug("Navigating back");
        if (canNavigateBack()) {
            navigationHistory.pop(); // Remove current page
            if (!navigationHistory.isEmpty()) {
                Class<?> previousPageClass = navigationHistory.peek();
                navigateTo(previousPageClass);
            } else {
                returnToMainMenu();
            }
        } else {
            returnToMainMenu();
        }
    }

    public void returnToMainMenu() {
        Logger.debug("Returning to main menu");
        clearNavigationHistory();
        legacyWindowManager.showMainMenu();
    }

    public void executeNavigation(NavigationCommand command) {
        if (command != null && command.canExecute()) {
            try {
                Logger.debug("Executing navigation command: {}", command.getDescription());
                command.execute(legacyWindowManager);
            } catch (Exception e) {
                Logger.error("Navigation command execution failed: {}", e.getMessage());
                // Fallback to main menu on navigation failure
                legacyWindowManager.showMainMenu();
            }
        } else {
            Logger.warn("Cannot execute navigation command - command is null or cannot execute");
        }
    }

    public void navigateToDestination(NavigationDestination destination, NavigationContext context) {
        Logger.debug("Navigating to destination: {}", destination);
        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        executeNavigation(command);
    }

    public boolean canNavigateBack() {
        return !navigationHistory.isEmpty() && navigationHistory.size() > 1;
    }

    public void clearNavigationHistory() {
        navigationHistory.clear();
        Logger.debug("Navigation history cleared");
    }
}
