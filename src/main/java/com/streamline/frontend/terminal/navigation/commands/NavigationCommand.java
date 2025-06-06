package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.window.NewTerminalWindowManager;

public interface NavigationCommand {
    void execute(NewTerminalWindowManager windowManager);
    String getDescription();
    boolean canExecute();
}
