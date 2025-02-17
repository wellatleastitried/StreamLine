package com.walit.streamline;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
// import java.util.concurrent.CompletableFuture;

import com.walit.streamline.Utilities.CacheManager;
import com.walit.streamline.Utilities.Internal.HelpMessages;
import com.walit.streamline.Utilities.Internal.Config;
import com.walit.streamline.Utilities.Internal.Mode;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;
import com.walit.streamline.Utilities.Internal.StreamLineConstants;
import com.walit.streamline.Interact.DatabaseLinker;
import com.walit.streamline.Interact.DatabaseRunner;
import com.walit.streamline.Utilities.StatementReader;
import com.walit.streamline.Utilities.RetrievedStorage;
import com.walit.streamline.Audio.AudioPlayer;
import com.walit.streamline.Audio.Song;
import com.walit.streamline.Communicate.InvidiousHandle;
import com.walit.streamline.Hosting.DockerManager;

public final class Core {

    private final Logger logger;

    private boolean exitedGracefully = false;

    private WindowBasedTextGUI textGUI;

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

    private final DatabaseLinker dbLink;
    private final DatabaseRunner dbRunner;
    private final InvidiousHandle apiHandle;
    private HashMap<String, String> queries;

    private final String cacheDirectory;

    private final Config config;

    public Core(Config config) {
        this.config = config;
        this.logger = config.getLogger();
        this.cacheDirectory = getCacheDirectory();
        setShutdownHandler();
        this.queries = Core.getMapOfQueries(logger);
        this.dbLink = initializeDatabaseConnection();
        this.dbRunner = new DatabaseRunner(dbLink.getConnection(), queries, logger);
        this.apiHandle = initializeAPI();
        handleExecutionMode();
    }

    private DatabaseLinker initializeDatabaseConnection() {
        return new DatabaseLinker(config.getOS(), queries.get("INITIALIZE_TABLES"), logger);
    }

    private InvidiousHandle initializeAPI() {
        return InvidiousHandle.getInstance(config, logger);
    }

    private void handleExecutionMode() {
        switch (config.getMode()) {
            case HEADLESS: // Web interface initialization
                clearExpiredCacheOnStartup();
                break;
            case TESTING: // Headless testing
                setupTestingMode();
                break;
            case CACHE_MANAGEMENT: // Allow for clearing the cache without starting the full application
                handleCacheManagement();
                break;
            default:
                initializeUI(); // Otherwise run default TUI app
        }
    }

    private void setupTestingMode() {
        System.out.println("Setting up testing configuration.");
        buttonWidth = 10;
        buttonHeight = 10;
    }

    private void handleCacheManagement() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Would you like to:\n1) Clear all existing cache\n2) Clear expired cache\nEnter a 1 or 2 to choose: ");
            String response = scanner.nextLine();
            if (response.trim().equals("1")) {
                clearCache();
            } else if (response.trim().equals("2")) {
                clearExpiredCacheOnStartup();
            }
        }
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
            mainMenu = createMainMenuWindow();
            searchPage = createSearchPage();
            likedMusicPage = createLikeMusicPage();
            playlistPage = createPlaylistPage();
            recentlyPlayedPage = createRecentlyPlayedPage();
            downloadedPage = createDownloadedMusicPage();
            helpMenu = createHelpMenu();
            settingsMenu = createSettingsMenu();
            clearExpiredCacheOnStartup();
            if (!config.getIsOnline()) {
                checkIfConnectionEstablished();
            }
        } catch (IOException iE) {
            logger.log(Level.SEVERE, StreamLineMessages.FatalStartError.getMessage());
            System.exit(1);
        }
    }

    private void checkIfConnectionEstablished() {
        Thread connectionTesting = new Thread(() -> {
            try {
                String reachableHost = InvidiousHandle.canConnectToAPI(logger);
                boolean dockerIsResponding = DockerManager.isContainerRunning(logger);
                while (reachableHost == null && !dockerIsResponding) {
                    reachableHost = InvidiousHandle.canConnectToAPI(logger);
                    if (reachableHost == null) {
                        dockerIsResponding = DockerManager.isContainerRunning(logger);
                    }
                    Thread.sleep(1000);
                }
                if (reachableHost != null) {
                    config.setHost(reachableHost);
                    config.setIsOnline(true);
                } else if (dockerIsResponding) {
                    config.setHost(StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS);
                    config.setIsOnline(true);
                }
            } catch (InterruptedException iE) {
                logger.log(Level.WARNING, StreamLineMessages.PeriodicConnectionTestingError.getMessage());
            }

        });
        connectionTesting.setDaemon(true);
        connectionTesting.start();
    }

    public static Process runCommandExpectWait(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            return process;
        } catch (IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return null;
        }
    }
    
    public static boolean runCommand(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return false;
        }
    }

    private void setShutdownHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            Future<?> future = executor.submit(this::shutdown);

            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException tE) {
                logger.log(Level.WARNING, StreamLineMessages.ShutdownTookTooLong.getMessage());
            } catch (Exception e) {
                logger.log(Level.SEVERE, StreamLineMessages.UnexpectedErrorInShutdown.getMessage());
            } finally {
                executor.shutdownNow();
            }
        }));
    }

    protected String getCacheDirectory() {
        switch (config.getOS()) {
            case WINDOWS:
                return StreamLineConstants.WINDOWS_CACHE_ADDRESS;
            case MAC:
                return StreamLineConstants.MAC_CACHE_ADDRESS;
            case LINUX:
            default:
                return StreamLineConstants.LINUX_CACHE_ADDRESS;
        }
    }

    public boolean start() {
        try {
            screen.startScreen();
            runMainWindow();
            screen.stopScreen();
        } catch (IOException iE) {
            return false;
        }
        return true;
    }
    
    /**
     * Reaches out to the SQL files in the resources folder that house the queries needed at runtime.
     * @return Map containing the full queries with a key for easy access
     */
    public static HashMap<String, String> getMapOfQueries(Logger logger) {
        HashMap<String, String> map = new HashMap<>();
        try {
            map.put("INITIALIZE_TABLES", StatementReader.readQueryFromFile("/sql/init/DatabaseInitialization.sql"));
            map.put("CLEAR_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearCachedSongs.sql"));
            map.put("CLEAR_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/updates/ClearExpiredCache.sql"));
            map.put("GET_EXPIRED_CACHE", StatementReader.readQueryFromFile("/sql/queries/GetExpiredCache.sql"));
            map.put("GET_LIKED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForLikedMusicScreen.sql"));
            map.put("GET_DOWNLOADED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForDownloadedScreen.sql"));
            map.put("GET_RECENTLY_PLAYED_SONGS", StatementReader.readQueryFromFile("/sql/queries/GetSongForRecPlayedScreen.sql"));
            map.put("ENSURE_RECENTLY_PLAYED_SONG_COUNT", StatementReader.readQueryFromFile("/sql/updates/UpdateRecentlyPlayed.sql"));
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.SQLFileReadError.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.MissingConfigurationFiles.getMessage());
            System.exit(1);
        }
        if (map.isEmpty()) {
            logger.log(Level.SEVERE, StreamLineMessages.DatabaseQueryCollectionError.getMessage());
            System.exit(1);
        }
        return map;
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
        final HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        BasicWindow window = new BasicWindow("StreamLine Music Player");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);

        // CREATE LABELS AND BUTTONS
        Label titleLabel = createLabel("    Welcome to StreamLine    ");

        Button searchButton = createButton("Search for a song", () -> transitionMenus(searchPage));
        buttons.put(buttonCount++, searchButton);

        Button likedButton = createButton("View liked music", () -> transitionMenus(likedMusicPage));
        buttons.put(buttonCount++, likedButton);

        Button playlistsButton = createButton("Playlists", () -> transitionMenus(playlistPage));
        buttons.put(buttonCount++, playlistsButton);

        Button recentlyPlayedButton = createButton("Recently Played", () -> transitionMenus(recentlyPlayedPage));
        buttons.put(buttonCount++, recentlyPlayedButton);

        Button downloadedPageButton = createButton("Downloaded Music", () -> transitionMenus(downloadedPage));
        buttons.put(buttonCount++, downloadedPageButton);

        Button helpButton = createButton("Help", () -> transitionMenus(helpMenu));
        buttons.put(buttonCount++, helpButton);

        Button settingsButton = createButton("Settings", () -> transitionMenus(settingsMenu));
        buttons.put(buttonCount++, settingsButton);

        Button quitButton = createButton("Quit", () -> System.exit(0));
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

        Label searchHelpLabel = createLabelWithSize("Search help");
        panel.addComponent(searchHelpLabel);

        Label searchHelpInfo = createLabel(HelpMessages.SearchInformation.getMessage());
        panel.addComponent(searchHelpInfo);

        panel.addComponent(generateNewSpace());

        Label likedMusicLabel = createLabelWithSize("Liked music help");
        panel.addComponent(likedMusicLabel);

        Label likedMusicInfo = createLabel(getString(HelpMessages.LikedMusicInformation.getMessage()));
        panel.addComponent(likedMusicInfo);

        panel.addComponent(generateNewSpace());
        panel.addComponent(generateNewSpace());

        Button backButton = createButton("  <- Back  ", () -> {
            textGUI.getGUIThread().invokeLater(() -> {
                dropWindow(helpMenu);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2);
        panel.addComponent(backButton);

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

        Button clearCacheButton = createButton(getString("Clear cache"), () -> {
            clearCache();
        });
        panel.addComponent(clearCacheButton);

        panel.addComponent(generateNewSpace());

        Button backButton = createButton("  <- Back  ", () -> {
            textGUI.getGUIThread().invokeLater(() -> {
                dropWindow(settingsMenu);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2);
        panel.addComponent(backButton);

        window.setComponent(panel);
        return window;
    }
    public BasicWindow createPlaylistPage() {
        RetrievedStorage songs = dbRunner.getRecentlyPlayedSongs();
        BasicWindow window = new BasicWindow("Playlists");
        return window;
    }

    public BasicWindow createRecentlyPlayedPage() {
        RetrievedStorage songs = dbRunner.getRecentlyPlayedSongs();
        BasicWindow window = new BasicWindow("Recently Played");
        return window;
    }

    public BasicWindow createLikeMusicPage() {
        RetrievedStorage songs = dbRunner.getRecentlyPlayedSongs();
        BasicWindow window = new BasicWindow("Liked Music");
        return window;
    }

    public BasicWindow createDownloadedMusicPage() {
        RetrievedStorage songs = dbRunner.getDownloadedSongs();
        BasicWindow window = new BasicWindow("Downloaded Music");
        return window;
    }

    public BasicWindow createSearchPage() {
        BasicWindow window = new BasicWindow("Search");

        window.setHints(java.util.Arrays.asList(Window.Hint.FULL_SCREEN));

        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20)); panel.setFillColorOverride(TextColor.ANSI.BLACK);

        panel.addComponent(generateNewSpace());

        Label searchLabel = createLabel(getString("Search:"));
        panel.addComponent(searchLabel);

        TextBox searchBar = new TextBox(new TerminalSize(terminalSize.getColumns() / 2, 1)) {
            @Override
            public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    // TODO: Change to display the results from the search (a row for each song within the "resultsBox"
                    textGUI.getGUIThread().invokeLater(() -> {
                        System.out.println("Enter pressed!"); // TODO: Change this to display the results
                        try {
                            textGUI.getScreen().refresh();
                        } catch (IOException iE) {
                            logger.log(Level.SEVERE, StreamLineMessages.RedrawError.getMessage());
                            handleKeyStroke(keyStroke);
                        }
                    });
                    return Result.HANDLED;
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
        panel.addComponent(searchBar);

        panel.addComponent(generateNewSpace());

        // This is the box that will store the search results in the form of <button button> where the first button contains the song information, and the second contains options on what to do with the song
        Panel resultsBox = new Panel();
        resultsBox.setLayoutManager(new GridLayout(/*2*/1));
        resultsBox.setPreferredSize(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - panel.getSize().getRows() - 15));
        resultsBox.setFillColorOverride(TextColor.ANSI.BLACK_BRIGHT);

        // Making sure that the api responses can be turned into the proper object for the TUI
        Label statsResponse = createLabel(testStatsCall());
        resultsBox.addComponent(statsResponse);

        panel.addComponent(resultsBox);

        Button backButton = createButton("  <- Back  ", () -> {
            textGUI.getGUIThread().invokeLater(() -> {
                dropWindow(searchPage);
                runMainWindow();
            });
        }, buttonWidth / 3, buttonHeight / 2);
        panel.addComponent(backButton);

        window.setComponent(panel);

        return window;
    }

    // Temporary function for getting TUI figured out
    public String testStatsCall() {
        InvidiousHandle handle = InvidiousHandle.getInstance(config, logger);
        return handle.retrieveStats();
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

    private void playQueue(RetrievedStorage songQueue) {
        // Something like this...
        AudioPlayer audioPlayer = new AudioPlayer(songQueue);
        // TODO: This may not need to be an async call as the GUI actions will be on the GUI event thread so the main thread should be free
        // CompletableFuture.runAsync(() -> audioPlayer);
    }

    private void clearCache() {
        CacheManager.clearCache(cacheDirectory);
        dbRunner.clearCachedSongs();
    }

    private void clearExpiredCacheOnStartup() {
        CacheManager.clearExpiredCacheOnStartup(cacheDirectory, dbRunner.getExpiredCache());
        dbRunner.clearExpiredCache();
    }

    private void dropWindow(BasicWindow window) {
        textGUI.removeWindow(window);
    }

    public String getString(String text) {
        return "  " + text + "  ";
    }

    private void shutdown() {
        if (!exitedGracefully) {
            exitedGracefully = true;
        } else {
            return;
        }

        if (config.getMode() != Mode.TESTING) {
            dbLink.shutdown();
            try {
                screen.stopScreen();
                if (DockerManager.isContainerRunning(logger)) {
                    DockerManager.stopContainer(logger);
                }
            } catch (InterruptedException | IOException iE) {
                logger.log(Level.SEVERE, StreamLineMessages.UnexpectedErrorInShutdown.getMessage());
            }
        }
        System.out.println(StreamLineMessages.Farewell.getMessage());
    }
}
