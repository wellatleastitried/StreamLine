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

public class Core {

    public static final HashMap<Integer, Button> buttons = new HashMap<>();
    public static int selectedIndex = 0;
    public static int buttonCount;
    public static int buttonWidth = 0;
    public static int buttonHeight = 2;

    public static void main(String [] args) {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try {
            Terminal terminal = terminalFactory.createTerminal();
            TerminalScreen screen = new TerminalScreen(terminal);
            screen.startScreen();

            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            BasicWindow window = new BasicWindow("StreamLine Music Player");

            window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

            TerminalSize terminalSize = screen.getTerminalSize();
            buttonWidth = terminalSize.getColumns() / 4;

            assert(buttonWidth != 0);

            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(1));
            panel.setPreferredSize(new TerminalSize(40, 20));
            panel.setFillColorOverride(TextColor.ANSI.BLACK);

            // CREATE LABELS AND BUTTONS
            Label titleLabel = new Label("    Welcome to StreamLine    ");
            titleLabel.addStyle(SGR.BOLD);

            Button searchButton= new Button("Search for a song", () -> System.out.println("Playing song"));
            searchButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
            buttons.put(0, searchButton);

            Button likedButton = new Button("View liked music", () -> System.out.println("Liked songs"));
            likedButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
            buttons.put(1, likedButton);

            Button playlistsButton = new Button("Playlists", () -> System.out.println("Playlists"));
            playlistsButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
            buttons.put(2, playlistsButton);

            Button quitButton = new Button("Quit", window::close);
            quitButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
            buttons.put(3, quitButton);

            buttonCount = buttons.size();
            
            panel.addComponent(titleLabel);
            for (int i = 0; i < buttonCount; i++) {
                panel.addComponent(buttons.get(i));
            }

            window.setComponent(panel);

            // Allow vim keys for navigation
            textGUI.addWindowAndWait(window);

            screen.stopScreen();
        } catch (IOException e) {
            System.out.println("Error running StreamLine, try again.");
        }
    }

    public static Button createButton(String text, Runnable runner) {
        Button button = new Button(text, runner);
        button.setPreferredSize(getSize(buttonWidth, buttonHeight));
        return button;
    }

    public static TerminalSize getSize(int bWidth, int bHeight) {
        return new TerminalSize(bWidth, bHeight);
    }
}
