package com.streamline.utilities;

import com.streamline.utilities.internal.StreamLineConstants;

import java.io.FileInputStream;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.tinylog.Logger;

/**
 * Allows for the quick retrieval of text in the appropriate language to be used for the interface during runtime.
 * @author wellatleastitried
 */
public final class LanguagePeer {

    private static ResourceBundle resource;

    static {
        String languageCode = getLanguageCode();
        setLanguage(languageCode);
    }

    /**
     * Manually set the language to be used during runtime.
     * @param languageCode The two character code to be used for the app's locale.
     */
    public static void setLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        resource = ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * Get the language code to be used for the Locale from the configuration file, if it exists. If it does not exist, get the locale from the operating system.
     * @return The two character language code to be used for the app's locale
     */
    private static String getLanguageCode() {
        try {
            Properties config = new Properties();
            switch (ConfigManager.getOSOfUser()) {
                case WINDOWS -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_WINDOWS));
                case MAC -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_MAC));
                default -> config.load(new FileInputStream(StreamLineConstants.STREAMLINE_CONFIG_PATH_LINUX));
            }
            String language = config.getProperty("language", "en");
            return language;
        } catch (Exception e) {
            Logger.warn("Error loading language configuration, defaulting to English.");
        }
        return "en";
    }

    /**
     * Get the text corresponding to the passed key using the appropriate locale for the user.
     * @param key The key that corresponds to the necessary text in the language properties file.
     * @return The string of text corresponding to the passed key.
     */
    public static String getText(String key) {
        if (resource == null) {
            setLanguage("en");
        }

        try {
            return resource.getString(key);
        } catch (Exception e) {
            return "!! " + key + " !!";
        }
    }

    public static String getSystemLocale() {
        return Locale.getDefault().getLanguage();
    }
}
