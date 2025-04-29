package com.streamline.frontend.terminal;

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
public class StreamLineTheme extends AbstractTheme {
    
    private final Map<String, ThemeDefinition> themeDefinitions;

    private static final TextColor PRIMARY_BACKGROUND = new TextColor.RGB(25, 25, 35);
    private static final TextColor SECONDARY_BACKGROUND = new TextColor.RGB(35, 35, 50);
    private static final TextColor ACTIVE_BACKGROUND = new TextColor.RGB(80, 45, 120);

    private static final TextColor PRIMARY_FOREGROUND = new TextColor.RGB(220, 220, 230);
    private static final TextColor ACCENT_COLOR = new TextColor.RGB(130, 80, 255);

    public StreamLineTheme() {
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
        themeDefinitions.put("Button", new StreamLineThemeDefinition(
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
        themeDefinitions.put("TextBox", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                SECONDARY_BACKGROUND,
                PRIMARY_BACKGROUND,
                SECONDARY_BACKGROUND,
                true,
                false
        ));
        themeDefinitions.put("Label", new StreamLineThemeDefinition(
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
        themeDefinitions.put("Window", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                ACCENT_COLOR,
                PRIMARY_BACKGROUND,
                true,
                true
        ));
        themeDefinitions.put("Panel", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                false, 
                false
        ));
        themeDefinitions.put("EmptySpace", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                false,
                false
        ));
        themeDefinitions.put("ActionListBox", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                ACCENT_COLOR,
                SECONDARY_BACKGROUND,
                TextColor.ANSI.WHITE,
                ACTIVE_BACKGROUND,
                true,
                true
        ));
        themeDefinitions.put("CheckBox", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                ACCENT_COLOR,
                PRIMARY_BACKGROUND,
                TextColor.ANSI.WHITE,
                ACTIVE_BACKGROUND,
                true,
                false
        ));
        themeDefinitions.put("ComboBox", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                SECONDARY_BACKGROUND,
                ACCENT_COLOR,
                SECONDARY_BACKGROUND,
                TextColor.ANSI.WHITE,
                ACTIVE_BACKGROUND,
                true,
                true
        ));
        themeDefinitions.put("RadioBoxList", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                ACCENT_COLOR,
                SECONDARY_BACKGROUND,
                TextColor.ANSI.WHITE,
                ACTIVE_BACKGROUND,
                true,
                true
        ));
        themeDefinitions.put("Table", new StreamLineThemeDefinition(
                PRIMARY_FOREGROUND,
                PRIMARY_BACKGROUND,
                ACCENT_COLOR,
                SECONDARY_BACKGROUND,
                TextColor.ANSI.WHITE,
                ACTIVE_BACKGROUND,
                true,
                true
        ));
    }

    @Override
    public ThemeDefinition getDefaultDefinition() {
        return themeDefinitions.get("DEFAULT");
    }

    @Override
    public ThemeDefinition getDefinition(Class<?> clazz) {

        /* First, check if we have a direct match for the class name */
        String className = clazz.getSimpleName();
        if (themeDefinitions.containsKey(className)) {
            return themeDefinitions.get(className);
        }
        
        /* Then try to find a matching definition by class compatibility */
        String key = null;
        if (clazz == Component.class) {
            key = "DEFAULT";
        } else {
            for (String definitionKey : themeDefinitions.keySet()) {
                if (definitionKey.equals("DEFAULT")) {
                    continue;
                }
                
                try {
                    Class<?> definitionClass = Class.forName("com.googlecode.lanterna.gui2." + definitionKey);
                    if (definitionClass.isAssignableFrom(clazz)) {
                        key = definitionKey;
                        break;
                    }
                } catch (ClassNotFoundException e) {

                    /* Try loading as a fully qualified class name */
                    try {
                        Class<?> definitionClass = Class.forName(definitionKey);
                        if (definitionClass.isAssignableFrom(clazz)) {
                            key = definitionKey;
                            break;
                        }
                    } catch (Exception ignore) {
                        Logger.debug("Failed to load class: " + definitionKey, ignore);
                        /* Log and try the next key */
                    }
                } catch (Exception ignore) {
                    Logger.debug("Failed to load class: " + definitionKey, ignore);
                    /* Log and try the next key */
                }
            }
        }

        if (key == null) {
            return getDefaultDefinition();
        } else {
            return themeDefinitions.get(key);
        }
    }

    private static class StreamLineThemeDefinition implements ThemeDefinition {

        private final TextColor normalForeground;
        private final TextColor normalBackground;
        private final TextColor selectedForeground;
        private final TextColor selectedBackground;
        private final TextColor activeForeground;
        private final TextColor activeBackground;
        private final boolean useRenderer;
        private final boolean useBorder;
        private final SGR[] extraStyles;

        private final DefaultThemeStyle normalStyle;
        private final DefaultThemeStyle selectedStyle;
        private final DefaultThemeStyle activeStyle;
        private final DefaultThemeStyle preLightStyle;
        private final DefaultThemeStyle insensitiveStyle;

        public StreamLineThemeDefinition(TextColor normalForeground, TextColor normalBackground, TextColor selectedForeground, TextColor selectedBackground, TextColor activeForeground, TextColor activeBackground, boolean useRenderer, boolean useBorder, SGR... extraStyles) {
            this.normalForeground = normalForeground;
            this.normalBackground = normalBackground;
            this.selectedForeground = selectedForeground;
            this.selectedBackground = selectedBackground;
            this.activeForeground = activeForeground;
            this.activeBackground = activeBackground;
            this.useRenderer = useRenderer;
            this.useBorder = useBorder;
            this.extraStyles = extraStyles;
            
            this.normalStyle = new DefaultThemeStyle(normalForeground, normalBackground, extraStyles);
            this.selectedStyle = new DefaultThemeStyle(selectedForeground, selectedBackground, extraStyles);
            this.activeStyle = new DefaultThemeStyle(activeForeground, activeBackground, extraStyles);
            this.preLightStyle = new DefaultThemeStyle(selectedForeground, normalBackground, extraStyles);
            
            TextColor dimmedForeground;
            if (normalForeground instanceof TextColor.RGB) {
                TextColor.RGB rgb = (TextColor.RGB) normalForeground;
                dimmedForeground = new TextColor.RGB(
                    Math.max(0, rgb.getRed() - 70),
                    Math.max(0, rgb.getGreen() - 70),
                    Math.max(0, rgb.getBlue() - 70)
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
            if (name.equals("RENDERER")) {
                return useRenderer;
            } else if (name.equals("USE_BORDER")) {
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
            this.sgrs = sgrs != null ? EnumSet.copyOf(Arrays.asList(sgrs)) : EnumSet.noneOf(SGR.class);
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
