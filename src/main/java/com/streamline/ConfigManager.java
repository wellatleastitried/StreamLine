package com.streamline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Properties;

import com.streamline.backend.DockerManager;
import com.streamline.backend.handle.InvidiousHandle;
import com.streamline.frontend.terminal.themes.*;
import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.Mode;
import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;

import org.apache.commons.cli.CommandLine;

import org.tinylog.Logger;

public class ConfigManager {

    private static OS os;
    public static final Config config = new Config();

    private ConfigManager() {}

    static {
        os = getOSOfUser();
        if (!initializeLogger()) {
            System.out.println("[!] There was an error while initializing the logger, please try reloading the app!");
            System.exit(0);
        }
        checkExistenceOfConfiguration();
    }

    protected static Config getConfigurationForRuntime() {
        config.setMode(Mode.TERMINAL);
        config.setTheme(getTerminalTheme(os));

        config.setOS(os);

        config.setAudioSource('y');
        config.setBinaryPath(getBinaryPath());

        return config;
    }

    protected static Config getConfigurationForRuntime(CommandLine commandLine) {
        if (commandLine.hasOption("youtube")) {
            config.setAudioSource('y');
        } else {
            config.setAudioSource('d');
            String apiHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
            if (apiHost == null || apiHost.length() < 1) {
                new Thread(() -> DockerManager.startInvidiousContainer()).start();
                config.setIsOnline(false);
                config.setHost(null);
            } else {
                config.setIsOnline(true);
                config.setHost(apiHost);
            }
        }
        return config;
    }

    private static void checkExistenceOfConfiguration() {
        String path = switch (os) {
            case WINDOWS -> StreamLineConstants.STREAMLINE_CONFIG_PATH_WINDOWS;
            case MAC -> StreamLineConstants.STREAMLINE_CONFIG_PATH_MAC;
            default -> StreamLineConstants.STREAMLINE_CONFIG_PATH_LINUX;
        };
        if (new File(path).exists()) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(createLine("language", LanguagePeer.getSystemLocale()));
        stringBuilder.append(createLine("theme", "default"));
        writeConfigurationFile(path, stringBuilder.toString());
    }

    private static void writeConfigurationFile(String path, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(text);
        } catch (IOException iE) {
            Logger.warn("Error writing configuration file, check permissions and try again.");
        }
    }

    private static String createLine(String id, String value) {
        return String.format("%s=%s\n", id, value);
    }


    public static OS getOSOfUser() {
        String osString = System.getProperty("os.name").toLowerCase();
        if (osString.contains("win")) {
            return OS.WINDOWS;
        } else if (osString.contains("nix") || osString.contains("nux")) {
            return OS.LINUX;
        } else if (osString.contains("mac")) {
            return OS.MAC;
        } else {
            return OS.UNKNOWN;
        }
    }

    private static boolean initializeLogger() {
        String configurationPath;
        String configurationFileContents;
        switch (os) {
            case WINDOWS -> {
                configurationPath = StreamLineConstants.WINDOWS_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.WINDOWS_LOG_CONFIG_CONTENTS;
            }
            case MAC -> {
                configurationPath = StreamLineConstants.MAC_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.UNIX_LOG_CONFIG_CONTENTS;
            }
            default -> {
                configurationPath = StreamLineConstants.LINUX_LOG_CONFIG_DIR_PATH;
                configurationFileContents = StreamLineConstants.UNIX_LOG_CONFIG_CONTENTS;
            }
        };

        File configDirectory = new File(configurationPath);
        if (!configDirectory.exists()) {
            if (!configDirectory.mkdirs()) {
                return false;
            }
        }

        configurationPath = configurationPath + "tinylog.properties";
        File configFile = new File(configurationPath);
        if (!configFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write(configurationFileContents);
                writer.flush();
            } catch (IOException iE) {
                return false;
            }
        }

        System.setProperty("tinylog.configuration", configurationPath);
        return true;
    }

    private static String getBinaryPath() {
        if (os == OS.WINDOWS) {
            return StreamLineConstants.YT_DLP_BIN_LOCATION_WINDOWS + "yt-dlp.exe";
        } else if (config.getOS() == OS.MAC) {
            return StreamLineConstants.YT_DLP_BIN_LOCATION_MAC + "yt-dlp";
        }
        return StreamLineConstants.YT_DLP_BIN_LOCATION_LINUX + "yt-dlp";
    }

    private static AbstractStreamLineTheme getTerminalTheme(OS os) {
        try {
            Properties config = new Properties();
            switch (os) {
                case WINDOWS -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_WINDOWS));
                case MAC -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_MAC));
                default -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_LINUX));
            }
            String themeName = config.getProperty("theme", "default");
            return getThemeFromName(themeName);
        } catch (IOException iE) {
            Logger.warn("[!] Error loading configuration file, using default theme.");
        }
        return new DefaultTheme();
    }

    private static AbstractStreamLineTheme getThemeFromName(String themeName) {
        if (themeName.equals("default")) {
            return new DefaultTheme();
        } else if (themeName.equals("dark")) {
            return new DarkTheme();
        } else if (themeName.equals("light")) {
            return new LightTheme();
        } else if (themeName.equals("solarized")) {
            return new SolarizedTheme();
        }
        Logger.info("[!] Unexpected error while parsing theme; using default theme.");
        return new DefaultTheme();
    }
}
