package com.streamline.frontend.terminal.themes;

import com.googlecode.lanterna.*;

public class DarkTheme extends AbstractStreamLineTheme {

    private static final TextColor PRIMARY_BACKGROUND = rgb(10, 10, 15);
    private static final TextColor SECONDARY_BACKGROUND = rgb(20, 20, 30);
    private static final TextColor ACTIVE_BACKGROUND = rgb(50, 30, 45);

    private static final TextColor PRIMARY_FOREGROUND = rgb(220, 220, 230);
    private static final TextColor ACCENT_COLOR = rgb(150, 150, 150);

    public DarkTheme() {
        super(PRIMARY_BACKGROUND, SECONDARY_BACKGROUND, PRIMARY_FOREGROUND, ACTIVE_BACKGROUND, ACCENT_COLOR);
    }

}
