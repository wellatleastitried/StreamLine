package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.window.TerminalWindowManager;

public interface NavigationCommand {
    void execute(TerminalWindowManager windowManager);
    String getDescription();
    boolean canExecute();
}
