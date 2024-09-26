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

import com.walit.streamline.Communicate.HelpMessages;
import com.walit.streamline.Communicate.StreamLineMessages;
import com.walit.streamline.Communicate.Mode;
import com.walit.streamline.Communicate.OS;
import com.walit.streamline.Interact.DatabaseLinker;
import com.walit.streamline.Utilities.StatementReader;

public final class Core {

    private WindowBasedTextGUI textGUI;

    // Windows
    private BasicWindow mainMenu;
    private BasicWindow helpMenu;
    private BasicWindow searchPage;
    private BasicWindow recentlyPlayedPage;
    private BasicWindow playlistPage;
    private BasicWindow likedMusicPage;

    public TerminalScreen screen;

    private Terminal terminal;
    private TerminalSize terminalSize;

    public int buttonCount;
    public int buttonWidth;
    public int buttonHeight;

    public final OS whichOS;
    private final DatabaseLinker dbLink;

    public Core(Mode mode) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            whichOS = OS.WINDOWS;
        } else if (os.contains("nix") || os.contains("nux")) {
            whichOS = OS.LINUX;
        } else if (os.contains("mac")) {
            whichOS = OS.MAC;
        } else {
            whichOS = OS.UNKNOWN;
        }
        HashMap<String, String> queries = getMapOfQueries();
        dbLink = new DatabaseLinker(whichOS, queries.get("INITIALIZE"));
        switch (mode) {
            case DELAYEDRUN:
                System.out.println("Work this out");
                break;
            case TESTING:
                this.buttonWidth = 10;
                this.buttonHeight = 10;
                break;
            case AUTORUN:
            default:
                DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
                try {
                    this.terminal = terminalFactory.createTerminal();
                    this.screen = new TerminalScreen(terminal);
                    this.terminalSize = screen.getTerminalSize();
                    this.buttonHeight = 2;
                    this.buttonWidth = terminalSize.getColumns() / 4;
                    this.textGUI = new MultiWindowTextGUI(screen);
                    this.mainMenu = createMainMenuWindow();
                    this.searchPage = createSearchPage();
                    this.likedMusicPage = createLikeMusicPage();
                    this.playlistPage = createPlaylistPage();
                    this.recentlyPlayedPage = createRecentlyPlayedPage();
                    this.helpMenu = createHelpMenu();
                } catch (IOException iE) {
                    System.err.println(StreamLineMessages.FatalStartError.getMessage());
                    System.exit(1);
                }
                break;
        }
    }

    public boolean start() {
        try {
            screen.startScreen();
            runMainWindow();
            screen.stopScreen();
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.FatalStartError.getMessage());
            System.exit(1);
            return false;
        }
        return true;
    }

    public static void main(String [] args) {
        Core streamline = new Core(Mode.AUTORUN);
        if (!streamline.start()) {
            System.err.println(StreamLineMessages.FatalStartError.getMessage());
            System.exit(1);
        }
        System.out.println(StreamLineMessages.Farewell.getMessage());
        System.exit(0);
    }

    public HashMap getMapOfQueries() {
        HashMap<String, String> map = new HashMap<>();
        map.put("INITIALIZE", StatementReader.readQueryFromFile("/sql/DatabaseInitialization.sql"));
        map.put("getLikedSongs", StatementReader.readQueryFromFile("/sql/GetSongForLikedMusicScreen.sql"));
        map.put("getDownloadedSongs", StatementReader.readQueryFromFile("/sql/GetSongForDownloadedScreen.sql"));
        map.put("getRecentlyPlayedSongs", StatementReader.readQueryFromFile("/sql/GetSongForRecPlayedScreen.sql"));
        map.put("ensureRecentlyPlayedCount", StatementReader.readQueryFromFile("/sql/UpdateRecentlyPlayed.sql"));
        return map;
    }

    public Button createButton(String text, Runnable runner) {
        Button button = new Button(text, runner);
        button.setPreferredSize(getSize(buttonWidth, buttonHeight));
        return button;
    }

    public TerminalSize getSize(int bWidth, int bHeight) {
        return new TerminalSize(bWidth, bHeight);
    }

    public BasicWindow createMainMenuWindow() {
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        BasicWindow window = new BasicWindow("StreamLine Music Player");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        // CREATE LABELS AND BUTTONS
        Label titleLabel = new Label("    Welcome to StreamLine    ");
        titleLabel.addStyle(SGR.BOLD);

        Button searchButton= new Button("Search for a song", () -> transitionMenus(searchPage));
        searchButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, searchButton);

        Button likedButton = new Button("View liked music", () -> transitionMenus(likedMusicPage));
        likedButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, likedButton);

        Button playlistsButton = new Button("Playlists", () -> transitionMenus(playlistPage));
        playlistsButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, playlistsButton);

        Button recentlyPlayedButton = new Button("Recently Played", () -> transitionMenus(recentlyPlayedPage));
        recentlyPlayedButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, recentlyPlayedButton);

        Button helpButton = new Button("Help", () -> transitionMenus(helpMenu));
        helpButton.setPreferredSize(getSize(buttonWidth, buttonHeight));
        buttons.put(buttonCount++, helpButton);

        Button quitButton = new Button("Quit", () -> shutdown());
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

    public BasicWindow createHelpMenu() {
        BasicWindow window = new BasicWindow("StreamLine Help Menu");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        panel.addComponent(generateNewSpace());

        Label searchHelpLabel = new Label(getString("Search help"));
        searchHelpLabel.setPreferredSize(getSize(buttonWidth, buttonHeight));
        searchHelpLabel.addStyle(SGR.BOLD);
        panel.addComponent(searchHelpLabel);

        Label searchHelpInfo = new Label(getString(HelpMessages.SearchInformation.getMessage()));
        searchHelpInfo.addStyle(SGR.BOLD);
        panel.addComponent(searchHelpInfo);

        panel.addComponent(generateNewSpace());

        Label likedMusicLabel = new Label(getString("Liked music help"));
        likedMusicLabel.setPreferredSize(getSize(buttonWidth, buttonHeight));
        likedMusicLabel.addStyle(SGR.BOLD);
        panel.addComponent(likedMusicLabel);

        Label likedMusicInfo = new Label(getString(HelpMessages.LikedMusicInformation.getMessage()));
        likedMusicInfo.addStyle(SGR.BOLD);
        panel.addComponent(likedMusicInfo);

        panel.addComponent(generateNewSpace());
        panel.addComponent(generateNewSpace());

        Button backButton = new Button("  <- Back  ", () -> {
            dropWindow(helpMenu);
            runMainWindow();
        });
        backButton.setPreferredSize(getSize(buttonWidth / 3, buttonHeight / 2));
        panel.addComponent(backButton);

        window.setComponent(panel);
        return window;
    }

    public BasicWindow createPlaylistPage() {
        BasicWindow window = new BasicWindow("Playlists");
        return window;
    }

    public BasicWindow createRecentlyPlayedPage() {
        BasicWindow window = new BasicWindow("Recently Played");
        return window;
    }

    public BasicWindow createLikeMusicPage() {
        BasicWindow window = new BasicWindow("Liked Music");
        return window;
    }

    public BasicWindow createSearchPage() {
        BasicWindow window = new BasicWindow("Search");
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

    private void transitionMenus(BasicWindow windowToTransitionTo) {
        mainMenu.setVisible(false);
        java.util.Collection<Window> openWindows = textGUI.getWindows();
        if (!openWindows.contains(windowToTransitionTo)) {
            textGUI.addWindowAndWait(windowToTransitionTo);
        }
    }

    private void dropWindow(BasicWindow window) {
        textGUI.removeWindow(window);
    }

    public String getString(String text) {
        return "  " + text + "  ";
    }

    private void shutdown() {
        java.util.Collection<Window> openWindows = textGUI.getWindows();
        for (Window window : openWindows) {
            textGUI.removeWindow(window);
        }
        // dbLink.close();
    }
}
