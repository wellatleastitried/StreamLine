package com.walit.streamline.Interface;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.walit.streamline.Backend.Core;
import com.walit.streamline.Utilities.RetrievedStorage;
import com.walit.streamline.Utilities.Internal.HelpMessages;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

import java.io.IOException;

public final class TerminalInterface extends FrontendInterface {

    private WindowBasedTextGUI textGUI;
    private TextGUIThread guiThread;

    private BasicWindow mainMenu;
    private BasicWindow settingsMenu;
    private BasicWindow helpMenu;
    private BasicWindow searchPage;
    private BasicWindow recentlyPlayedPage;
    private BasicWindow downloadedPage;
    private BasicWindow playlistPage;
    private BasicWindow likedMusicPage;

    public TerminalScreen screen;

    private Terminal terminal;
    private TerminalSize terminalSize;

    public int buttonCount;
    public int buttonWidth;
    public int buttonHeight;

    public TerminalInterface(Core backend) {
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
            mainMenu = createMainMenuWindow();
            searchPage = createSearchPage();
            likedMusicPage = createLikeMusicPage();
            playlistPage = createPlaylistPage();
            recentlyPlayedPage = createRecentlyPlayedPage();
            downloadedPage = createDownloadedMusicPage();
            helpMenu = createHelpMenu();
            settingsMenu = createSettingsMenu();
        } catch (IOException iE) {
            logSevere(StreamLineMessages.FatalStartError.getMessage());
            System.exit(0);
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

    public BasicWindow createMainMenuWindow() {
        BasicWindow window = new BasicWindow("StreamLine Music Player");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        // CREATE LABELS AND BUTTONS
        Label titleLabel = createLabel("    Welcome to StreamLine    ");
        panel.addComponent(generateNewSpace());
        panel.addComponent(titleLabel);
        panel.addComponent(createButton("Search for a song", () -> transitionMenus(searchPage)));
        panel.addComponent(createButton("View liked music", () -> transitionMenus(likedMusicPage)));
        panel.addComponent(createButton("Playlists", () -> transitionMenus(playlistPage)));
        panel.addComponent(createButton("Recently Played", () -> transitionMenus(recentlyPlayedPage)));
        panel.addComponent(createButton("Downloaded Music", () -> transitionMenus(downloadedPage)));
        panel.addComponent(createButton("Help", () -> transitionMenus(helpMenu)));
        panel.addComponent(createButton("Settings", () -> transitionMenus(settingsMenu)));
        panel.addComponent(createButton("Quit", () -> {
            shutdown();
            backend.shutdown();
        }));
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
        panel.addComponent(createLabelWithSize("Search help"));
        panel.addComponent(createLabel(HelpMessages.SearchInformation.getMessage()));
        panel.addComponent(generateNewSpace());
        panel.addComponent(createLabelWithSize("Liked music help"));
        panel.addComponent(createLabel(getString(HelpMessages.LikedMusicInformation.getMessage())));
        panel.addComponent(generateNewSpace());
        panel.addComponent(generateNewSpace());
        panel.addComponent(createButton("  <- Back  ", () -> {
            guiThread.invokeLater(() -> {
                dropWindow(helpMenu);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2));

        window.setComponent(panel);
        return window;
    }

    public BasicWindow createSettingsMenu() {
        BasicWindow window = new BasicWindow("StreamLine Settings");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        panel.addComponent(generateNewSpace());
        panel.addComponent(createButton(getString("Clear cache"), () -> {
            backend.clearCache();
        }));
        panel.addComponent(generateNewSpace());
        panel.addComponent(createButton("  <- Back  ", () -> {
            guiThread.invokeLater(() -> {
                dropWindow(settingsMenu);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2));

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

    public BasicWindow createDownloadedMusicPage() {
        BasicWindow window = new BasicWindow("Downloaded Music");
        return window;
    }

    public BasicWindow createSearchPage() {
        BasicWindow window = new BasicWindow("Search");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();

        // This is the box that will store the search results in the form of <button button> where the first button contains the song information, and the second contains options on what to do with the song
        Panel resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(/*2*/1));
        resultsBox.setPreferredSize(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - panel.getSize().getRows() - 15));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);
        // Making sure that the api responses can be turned into the proper object for the TUI
        resultsBox.addComponent(createLabel(backend.testStatsCall()));

        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20)); panel.setFillColorOverride(TextColor.ANSI.BLACK);

        panel.addComponent(generateNewSpace());
        panel.addComponent(createLabel(getString("Search:")));

        panel.addComponent(new TextBox(new TerminalSize(terminalSize.getColumns() / 2, 1)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    // TODO: Change to display the results from the search (a row for each song within the "resultsBox"
                    guiThread.invokeLater(() -> {
                        /*
                        String enteredText = this.getText();
                        RetrievedStorage results = fetchSearchResults(enteredText);
                        for (Button button : resultsToButtons(results)) {
                            resultsBox.addComponent(button);
                        }
                        */
                        try {
                            textGUI.getScreen().refresh();
                        } catch (IOException iE) {
                            logSevere(StreamLineMessages.RedrawError.getMessage());
                            handleKeyStroke(keyStroke);
                        }
                    });
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        });
        panel.addComponent(generateNewSpace());

        panel.addComponent(resultsBox);
        panel.addComponent(createButton("  <- Back  ", () -> {
            guiThread.invokeLater(() -> {
                dropWindow(searchPage);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2));

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

    @Override
    public void shutdown() {
        try {
            guiThread.invokeLater(() -> {
                try {
                    for (Window window : textGUI.getWindows()) {
                        textGUI.removeWindow(window);
                    }
                } catch (IllegalStateException iE) {
                    logWarning(StreamLineMessages.IllegalStateExceptionInShutdown.getMessage() + iE.getMessage());
                }
            });
            screen.stopScreen();
            terminal.close();
        } catch (IOException iE) {
            logSevere(StreamLineMessages.UnexpectedErrorInShutdown.getMessage());
        } catch (IllegalStateException iE) {
            logWarning(StreamLineMessages.IllegalStateExceptionInShutdown.getMessage() + iE.getMessage());
        }
    }
}
