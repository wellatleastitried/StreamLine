package com.walit.streamline.utilities;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Allows for the quick retrieval of text in the appropriate language to be used for the interface during runtime.
 * @author wellatleastitried
 */
public final class LanguageManager {

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
        resource = ResourceBundle.getBundle("il8n.messages", locale);
    }

    /**
     * Get the language code to be used for the Locale from the configuration file, if it exists. If it does not exist, get the locale from the operating system.
     * @return The two character language code to be used for the app's locale
     */
    private static String getLanguageCode() {
        return "";
    }

    /**
     * Get the text corresponding to the passed key using the appropriate locale for the user.
     * @param key The key that corresponds to the necessary text in the language properties file.
     * @return The string of text corresponding to the passed key.
     */
    public static String getText(String key) {
        return resource.getString(key);
    }
}
