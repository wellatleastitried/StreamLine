package com.streamline.frontend.terminal.themes;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.graphics.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.tinylog.Logger;

/**
 * StreamLineTerminalTheme is a custom theme for the StreamLine terminal application.
 * It defines a color scheme and styles for various UI components.
 * The theme is designed to provide a consistent and visually appealing user interface.
 * 
 * @author wellatleastitried
 */
public abstract class AbstractStreamLineTheme extends AbstractTheme {
    
    private final Map<String, ThemeDefinition> themeDefinitions;

    private static final String[] COMPONENT_CLASSES = {
        "Button",
        "Label",
        "TextBox",
        "Panel",
        "Window",
        "BasicWindow", 
        "EmptySpace",
        "ActionListBox",
        "CheckBox",
        "ComboBox",
        "RadioBoxList",
        "Separator",
        "Table"
    };

    public AbstractStreamLineTheme(TextColor PRIMARY_BACKGROUND, TextColor SECONDARY_BACKGROUND, TextColor PRIMARY_FOREGROUND, TextColor ACTIVE_BACKGROUND, TextColor ACCENT_COLOR) {
        super(null, null);

        this.themeDefinitions = new HashMap<>();

        themeDefinitions.put("DEFAULT", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                false,
                true,
                SGR.BOLD
        ));

        for (String componentClass : COMPONENT_CLASSES) {
            if (componentClass.equals("Button")) {
                themeDefinitions.put(componentClass, new StreamLineThemeDefinition(
                        PRIMARY_FOREGROUND,
                        SECONDARY_BACKGROUND,
                        ACCENT_COLOR,
                        SECONDARY_BACKGROUND,
                        TextColor.ANSI.WHITE,
                        ACTIVE_BACKGROUND,
                        true,
                        true,
                        SGR.BOLD
                ));
            } else if (componentClass.equals("TextBox")) {
                themeDefinitions.put(componentClass, new StreamLineThemeDefinition(
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        SECONDARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        SECONDARY_BACKGROUND,
                        true,
                        false
                ));
            } else if (componentClass.equals("Label")) {
                themeDefinitions.put(componentClass, new StreamLineThemeDefinition(
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        ACCENT_COLOR,
                        PRIMARY_BACKGROUND,
                        false,
                        false,
                        SGR.BOLD
                ));
            } else if (componentClass.equals("Window") || componentClass.equals("BasicWindow")) {
                themeDefinitions.put(componentClass, new StreamLineThemeDefinition(
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        ACCENT_COLOR,
                        PRIMARY_BACKGROUND,
                        true,
                        true
                ));
            } else {
                /* All other components get the default theme */
                themeDefinitions.put(componentClass, new StreamLineThemeDefinition(
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        PRIMARY_FOREGROUND,
                        PRIMARY_BACKGROUND,
                        false,
                        false
                ));
            }
        }
        
        Logger.debug("StreamLineTheme: Initialized with " + themeDefinitions.size() + " component theme definitions");
    }

    @Override
    public ThemeDefinition getDefaultDefinition() {
        return themeDefinitions.get("DEFAULT");
    }

    @Override
    public ThemeDefinition getDefinition(Class<?> clazz) {
        String className = clazz.getSimpleName();
        
        if (themeDefinitions.containsKey(className)) {
            Logger.trace("StreamLineTheme: Found theme for " + className + " by direct lookup");
            return themeDefinitions.get(className);
        }
        
        if (clazz == Component.class) {
            Logger.trace("StreamLineTheme: Using default theme for base Component class");
            return getDefaultDefinition();
        }
        
        for (String definitionKey : themeDefinitions.keySet()) {
            if (definitionKey.equals("DEFAULT")) {
                continue;
            }
            
            try {
                Class<?> definitionClass = Class.forName("com.googlecode.lanterna.gui2." + definitionKey);
                if (definitionClass.isAssignableFrom(clazz)) {
                    Logger.trace("StreamLineTheme: Found theme for " + className + " via parent class " + definitionKey);
                    return themeDefinitions.get(definitionKey);
                }
            } catch (ClassNotFoundException e) {
                Logger.debug("[*] StreamLineTheme: Class not found for " + definitionKey + ": " + e.getMessage());
            } catch (Exception e) {
                Logger.debug("[*] StreamLineTheme: Unknown error for " + definitionKey + ": " + e.getMessage());
            }
        }
        
        Logger.trace("StreamLineTheme: No theme definition found for " + className + ", using default");
        return getDefaultDefinition();
    }

    protected static TextColor.RGB rgb(int r, int g, int b) {
        return new TextColor.RGB(r, g, b);
    }

    private static class StreamLineThemeDefinition implements ThemeDefinition {

        private final boolean useRenderer;
        private final boolean useBorder;
        private final SGR[] extraStyles;

        private final DefaultThemeStyle normalStyle;
        private final DefaultThemeStyle selectedStyle;
        private final DefaultThemeStyle activeStyle;
        private final DefaultThemeStyle preLightStyle;
        private final DefaultThemeStyle insensitiveStyle;

        public StreamLineThemeDefinition(TextColor normalForeground, TextColor normalBackground, TextColor selectedForeground, TextColor selectedBackground, TextColor activeForeground, TextColor activeBackground, boolean useRenderer, boolean useBorder, SGR... extraStyles) {
            this.useRenderer = useRenderer;
            this.useBorder = useBorder;
            this.extraStyles = extraStyles != null ? extraStyles : new SGR[0];
            
            this.normalStyle = new DefaultThemeStyle(normalForeground, normalBackground, this.extraStyles);
            this.selectedStyle = new DefaultThemeStyle(selectedForeground, selectedBackground, this.extraStyles);
            this.activeStyle = new DefaultThemeStyle(activeForeground, activeBackground, this.extraStyles);
            this.preLightStyle = new DefaultThemeStyle(selectedForeground, normalBackground, this.extraStyles);
            
            TextColor dimmedForeground;
            if (normalForeground instanceof TextColor.RGB) {
                TextColor.RGB RGB = (TextColor.RGB) normalForeground;
                dimmedForeground = new TextColor.RGB(
                    Math.max(0, RGB.getRed() - 70),
                    Math.max(0, RGB.getGreen() - 70),
                    Math.max(0, RGB.getBlue() - 70)
                );
            } else {
                dimmedForeground = normalForeground;
            }
            this.insensitiveStyle = new DefaultThemeStyle(dimmedForeground, normalBackground);
        }

        @Override
        public ThemeStyle getNormal() {
            return normalStyle;
        }

        @Override
        public ThemeStyle getSelected() {
            return selectedStyle;
        }

        @Override
        public ThemeStyle getActive() {
            return activeStyle;
        }

        @Override
        public ThemeStyle getPreLight() {
            return preLightStyle;
        }

        @Override
        public ThemeStyle getInsensitive() {
            return insensitiveStyle;
        }

        @Override
        public ThemeStyle getCustom(String name) {
            return null;
        }

        @Override
        public ThemeStyle getCustom(String name, ThemeStyle defaultValue) {
            return defaultValue;
        }

        @Override
        public char getCharacter(String name, char fallback) {
            if (name.equals("HORIZONTAL_LINE")) {
                return '─';
            } else if (name.equals("VERTICAL_LINE")) {
                return '│';
            } else if (name.equals("TOP_LEFT_CORNER")) {
                return '┌';
            } else if (name.equals("TOP_RIGHT_CORNER")) {
                return '┐';
            } else if (name.equals("BOTTOM_LEFT_CORNER")) {
                return '└';
            } else if (name.equals("BOTTOM_RIGHT_CORNER")) {
                return '┘';
            } else if (name.equals("TITLE_SEPARATOR")) {
                return ' ';
            }
            return fallback;
        }

        @Override
        public boolean getBooleanProperty(String name, boolean defaultValue) {
            if ("RENDERER".equals(name)) {
                return useRenderer;
            } else if ("USE_BORDER".equals(name)) {
                return useBorder;
            }
            return defaultValue;
        }

        @Override
        public <T extends Component> ComponentRenderer<T> getRenderer(Class<T> type) {
            return null;
        }

        @Override
        public boolean isCursorVisible() {
            return true;
        }
    }
    
    /**
     * Implementation of ThemeStyle to be used with the theme
     */
    private static class DefaultThemeStyle implements ThemeStyle {
        private final TextColor foreground;
        private final TextColor background;
        private final EnumSet<SGR> sgrs;
        
        public DefaultThemeStyle(TextColor foreground, TextColor background, SGR... sgrs) {
            this.foreground = foreground;
            this.background = background;
            this.sgrs = sgrs.length > 0 ? EnumSet.copyOf(Arrays.asList(sgrs)) : EnumSet.noneOf(SGR.class);
        }
        
        @Override
        public TextColor getForeground() {
            return foreground;
        }
        
        @Override
        public TextColor getBackground() {
            return background;
        }
        
        @Override
        public EnumSet<SGR> getSGRs() {
            return sgrs;
        }
    }
}
