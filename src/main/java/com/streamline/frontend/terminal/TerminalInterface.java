package com.streamline.frontend.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

import java.io.IOException;

import org.tinylog.Logger;

public final class TerminalInterface extends com.streamline.frontend.FrontendInterface {

    private WindowBasedTextGUI textGUI;
    private TextGUIThread guiThread;

    private BasicWindow mainMenu;

    public TerminalScreen screen;

    private Terminal terminal;
    private TerminalSize terminalSize;

    public int buttonCount;
    public int buttonWidth;
    public int buttonHeight;

    public TerminalInterface(Dispatcher backend) {
        super(backend);
        initializeUI();
    }

    @Override
    public boolean run() {
        try {
            screen.startScreen();
            runMainWindow();
            screen.stopScreen();
        } catch (IOException iE) {
            return false;
        }
        return true;
    }

    private void initializeUI() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try {
            terminal = terminalFactory.createTerminal();
            screen = new TerminalScreen(terminal);
            terminalSize = screen.getTerminalSize();
            buttonHeight = 2;
            buttonWidth = terminalSize.getColumns() / 4;
            textGUI = new MultiWindowTextGUI(screen);
            guiThread = textGUI.getGUIThread();
            initializeWindows();
        } catch (IOException iE) {
            Logger.error("[!] A fatal error has occured while starting StreamLine, please try reloading the app.");
            System.exit(0);
        }
    }

    private void initializeWindows() {
        try {
            TerminalWindowManager windowManager = new TerminalWindowManager(textGUI, guiThread, backend, new TerminalComponentFactory(backend.config, terminalSize));
            if (windowManager.mainPage == null) {
                throw new IllegalStateException("Main page is null");
            } else {
                mainMenu = windowManager.mainPage;
            }
        } catch (Exception e) {
            Logger.error("[!] There was an error while initializing the windows for the terminal interface.\n[!] Error message: " + e.getMessage());
        }
    }

    public Label createLabelWithSize(String text) {
        return createLabelWithSize(text, buttonWidth, buttonHeight);
    }

    public Label createLabelWithSize(String text, int width, int height) {
        Label label = createLabel(text);
        label.setPreferredSize(getSize(width, height));
        return label;
    }

    public Label createLabel(String text) {
        Label label = new Label(getString(text));
        label.addStyle(SGR.BOLD);
        return label;
    }

    public Button createButton(String text, Runnable runner, int width, int height) {
        Button button = new Button(text, runner);
        button.setPreferredSize(getSize(width, height));
        return button;
    }

    public Button createButton(String text, Runnable runner) {
        return createButton(text, runner, buttonWidth, buttonHeight);
    }

    public TerminalSize getSize(int bWidth, int bHeight) {
        return new TerminalSize(bWidth, bHeight);
    }

    public EmptySpace generateNewSpace() {
        return new EmptySpace(getSize(buttonWidth, buttonHeight));
    }

    private void runMainWindow() {
        assert mainMenu != null;
        textGUI.addWindowAndWait(mainMenu);
    }

    public String getString(String text) {
        return LanguagePeer.getText(text);
    }

    @Override
    public void shutdown() {
        try {
            guiThread.invokeLater(() -> {
                for (Window window : textGUI.getWindows()) {
                    textGUI.removeWindow(window);
                }
            });
            screen.stopScreen();
            terminal.close();
        } catch (IOException iE) {
            Logger.error("[!] There was an error while shutting down the terminal interface.");
        }
    }
}
