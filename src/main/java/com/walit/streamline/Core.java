package com.walit.streamline;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.HashMap;

import com.walit.streamline.Debug.StreamLineMessages;

public class Core {

    private WindowBasedTextGUI textGUI;

    // Windows
    private BasicWindow mainMenu;
    private BasicWindow helpMenu;

    public TerminalScreen screen;

    private Terminal terminal;
    private TerminalSize terminalSize;

    public final HashMap<Integer, Button> buttons;
    public int buttonCount;
    public final int buttonWidth;
    public final int buttonHeight;

    public Core() {)

    public Core(boolean startDesired) {
        if (!startDesired) return;
        this.buttons = new HashMap<Integer, Button>();
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try {
            this.terminal = terminalFactory.createTerminal();
            this.screen = new TerminalScreen(terminal);
            this.terminalSize = screen.getTerminalSize();
            this.buttonHeight = 2;
            this.buttonWidth = terminalSize.getColumns() / 4;
            this.textGUI = new MultiWindowTextGUI(screen);
            this.mainMenu = createMainMenuWindow(textGUI);
            this.helpMenu = createHelpMenu(textGUI);
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.FatalError.getMessage());
            System.exit(1);
        }
    }

    public boolean start() {
        try {
            screen.startScreen();
            runMainWindow();
            screen.stopScreen();
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.FatalError.getMessage());
            System.exit(1);
            return false;
        }
        return true;
    }

    public static void main(String [] args) {
        Core streamline = new Core(true);
        if (!streamline.start()) {
            System.err.println(StreamLineMessages.FatalError.getMessage());
            System.exit(1);
        }
        System.out.println(StreamLineMessages.Farewell.getMessage());
        System.exit(0);
    }

    public Button createButton(String text, Runnable runner) {
        Button button = new Button(text, runner);
        button.setPreferredSize(getSize(buttonWidth, buttonHeight));
        return button;
    }

    private TerminalSize getSize(int bWidth, int bHeight) {
        return new TerminalSize(bWidth, bHeight);
    }

    // Create WindowFactory
    public BasicWindow createMainMenuWindow(WindowBasedTextGUI textGUI) {
        BasicWindow window = new BasicWindow("StreamLine Music Player");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        // CREATE LABELS AND BUTTONS
        Label titleLabel = new Label("    Welcome to StreamLine    ");
        titleLabel.addStyle(SGR.BOLD);

        Button searchButton= new Button("Search for a song", () -> System.out.println("Playing song"));
        searchButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, searchButton);

        Button likedButton = new Button("View liked music", () -> System.out.println("Liked songs"));
        likedButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, likedButton);

        Button playlistsButton = new Button("Playlists", () -> System.out.println("Playlists"));
        playlistsButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, playlistsButton);

        Button recentlyPlayedButton = new Button("Recently Played", () -> System.out.println("Recently played"));
        recentlyPlayedButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, recentlyPlayedButton);

        Button helpButton = new Button("Help", () -> transitionToHelpMenu(textGUI));
        helpButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, helpButton);

        Button quitButton = new Button("Quit", window::close);
        quitButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, quitButton);

        panel.addComponent(generateNewSpace());
        panel.addComponent(titleLabel);
        for (int i = 0; i < buttonCount; i++) {
            panel.addComponent(buttons.get(i));
        }

        window.setComponent(panel);
        return window;
    }

    // Create WindowFactory
    public BasicWindow createHelpMenu(WindowBasedTextGUI textGUI) {
        BasicWindow window = new BasicWindow("StreamLine Help Menu");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        panel.addComponent(generateNewSpace());

        Label searchHelpLabel = new Label("Search help");
        searchHelpLabel.setPreferredSize(getSize(buttonWidth, buttonHeight));
        searchHelpLabel.addStyle(SGR.BOLD);
        panel.addComponent(searchHelpLabel);

        Label searchHelpInfo = new Label(StreamLineMessages.SearchInformation.getMessage());
        searchHelpInfo.addStyle(SGR.BOLD);
        panel.addComponent(searchHelpInfo);

        panel.addComponent(generateNewSpace());

        Label likedMusicLabel = new Label("Liked music help");
        likedMusicLabel.setPreferredSize(getSize(buttonWidth, buttonHeight));
        likedMusicLabel.addStyle(SGR.BOLD);
        panel.addComponent(likedMusicLabel);

        Label likedMusicInfo = new Label(StreamLineMessages.LikedMusicInformation.getMessage());
        likedMusicInfo.addStyle(SGR.BOLD);
        panel.addComponent(likedMusicInfo);

        panel.addComponent(generateNewSpace());
        panel.addComponent(generateNewSpace());

        Button backButton = new Button("<- Back", () -> {
            dropWindow(helpMenu);
            runMainWindow();
        });
        backButton.setPreferredSize(getSize(buttonWidth, buttonHeight / 2));
        panel.addComponent(backButton);

        window.setComponent(panel);
        return window;
    }

    public EmptySpace generateNewSpace() {
        EmptySpace space = new EmptySpace();
        space.setPreferredSize(getSize(buttonWidth, buttonHeight));
        space.setVisible(false);
        return space;
    }

    private void runMainWindow() {
        mainMenu.setVisible(true);
        java.util.Collection<Window> openWindows = textGUI.getWindows();
        if (!openWindows.contains(mainMenu)) {
            textGUI.addWindowAndWait(mainMenu);
        }
    }

    private void transitionToHelpMenu(WindowBasedTextGUI textGUI) {
        mainMenu.setVisible(false);
        java.util.Collection<Window> openWindows = textGUI.getWindows();
        if (!openWindows.contains(helpMenu)) {
            textGUI.addWindowAndWait(helpMenu);
        }
    }

    private void dropWindow(BasicWindow window) {
        textGUI.removeWindow(window);
    }
}
