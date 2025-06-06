package com.streamline.frontend.terminal.window;

import com.streamline.frontend.terminal.navigation.commands.NavigationCommand;
import com.streamline.frontend.terminal.navigation.commands.NavigationCommandFactory;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import org.tinylog.Logger;

import java.util.Stack;

/**
 * Enhanced navigation manager that handles all navigation responsibilities.
 * Manages navigation history, commands, and transitions between pages.
 */
public class TerminalWindowNavigationManager {

    private final Stack<Class<?>> navigationHistory;
    private final TerminalWindowLifecycleManager lifecycleManager;

    public TerminalWindowNavigationManager(TerminalWindowLifecycleManager lifecycleManager) {
        this.navigationHistory = new Stack<>();
        this.lifecycleManager = lifecycleManager;
        Logger.debug("TerminalWindowNavigationManager initialized with enhanced lifecycle manager");
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
                Logger.debug("Navigating back to {}", previousPageClass.getSimpleName());
                // Navigate back without adding to history again
                navigateBackTo(previousPageClass);
            } else {
                Logger.debug("Navigation history empty, returning to main menu");
                returnToMainMenu();
            }
        } else {
            Logger.debug("Cannot navigate back, returning to main menu");
            returnToMainMenu();
        }
    }

    /**
     * Navigate to a page without modifying navigation history (used for back navigation).
     */
    private void navigateBackTo(Class<?> targetPageClass) {
        Logger.debug("Navigating back to {} without history modification", targetPageClass.getSimpleName());
        
        // Create navigation context for the target page
        NavigationDestination destination = NavigationDestination.fromPageClass(targetPageClass);
        NavigationContext context = new NavigationContext(null, null);
        context.setContextData("directDestination", destination);
        
        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        executeNavigation(command);
    }

    public void returnToMainMenu() {
        Logger.debug("Returning to main menu");
        clearNavigationHistory();
        
        if (lifecycleManager != null) {
            // Use enhanced lifecycle manager
            lifecycleManager.showMainMenu();
        } else {
            Logger.error("No lifecycle manager available for returnToMainMenu");
        }
    }

    public void executeNavigation(NavigationCommand command) {
        if (command != null && command.canExecute()) {
            try {
                Logger.debug("Executing navigation command: {}", command.getDescription());
                
                // Get the current NewTerminalWindowManager instance
                NewTerminalWindowManager windowManager = NewTerminalWindowManager.getInstance();
                if (windowManager != null) {
                    // Execute the navigation command with the new window manager
                    command.execute(windowManager);
                    Logger.debug("Successfully executed navigation command: {}", command.getDescription());
                } else {
                    Logger.error("NewTerminalWindowManager instance not available for navigation");
                    returnToMainMenu();
                }
            } catch (Exception e) {
                Logger.error("Navigation command execution failed: {}", e.getMessage());
                // Fallback to main menu on navigation failure
                returnToMainMenu();
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

    // Enhanced navigation methods for better control
    public void navigateWithContext(Class<?> targetPageClass, NavigationContext context) {
        Logger.debug("Navigating to {} with context", targetPageClass.getSimpleName());
        
        navigationHistory.push(targetPageClass);
        NavigationDestination destination = NavigationDestination.fromPageClass(targetPageClass);
        context.setContextData("directDestination", destination);
        
        NavigationCommand command = NavigationCommandFactory.createNavigationCommand(context);
        executeNavigation(command);
    }

    public void replaceCurrentPage(Class<?> targetPageClass) {
        Logger.debug("Replacing current page with {}", targetPageClass.getSimpleName());
        
        if (!navigationHistory.isEmpty()) {
            navigationHistory.pop(); // Remove current page
        }
        navigateTo(targetPageClass);
    }

    public Class<?> getCurrentPage() {
        if (!navigationHistory.isEmpty()) {
            return navigationHistory.peek();
        }
        return null;
    }

    public Class<?> getPreviousPage() {
        if (navigationHistory.size() > 1) {
            return navigationHistory.get(navigationHistory.size() - 2);
        }
        return null;
    }

    public int getNavigationHistorySize() {
        return navigationHistory.size();
    }

    public boolean isAtMainMenu() {
        return navigationHistory.isEmpty() || 
               (navigationHistory.size() == 1 && "MainPage".equals(getCurrentPage().getSimpleName()));
    }

    // Transition management methods
    public void transitionToPage(Class<?> pageClass) {
        if (lifecycleManager != null) {
            lifecycleManager.transitionTo(pageClass);
        } else {
            Logger.warn("Lifecycle manager not available for page transition");
            navigateTo(pageClass);
        }
    }

    public void showMainMenu() {
        clearNavigationHistory();
        if (lifecycleManager != null) {
            lifecycleManager.showMainMenu();
        } else {
            Logger.error("No lifecycle manager available for showMainMenu");
        }
    }
}
