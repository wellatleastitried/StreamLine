
package com.streamline.frontend.terminal.themes;

import com.googlecode.lanterna.*;

public class DarkTheme extends AbstractStreamLineTheme {

    private static final TextColor PRIMARY_BACKGROUND = rgb(25, 25, 35);
    private static final TextColor SECONDARY_BACKGROUND = rgb(35, 35, 50);
    private static final TextColor ACTIVE_BACKGROUND = rgb(80, 45, 120);

    private static final TextColor PRIMARY_FOREGROUND = rgb(220, 220, 230);
    private static final TextColor ACCENT_COLOR = rgb(130, 80, 255);

    public DarkTheme() {
        super(PRIMARY_BACKGROUND, SECONDARY_BACKGROUND, PRIMARY_FOREGROUND, ACTIVE_BACKGROUND, ACCENT_COLOR);
    }

}
