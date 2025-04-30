package com.streamline.frontend.terminal.themes;

import com.googlecode.lanterna.*;

public class LightTheme extends AbstractStreamLineTheme {

    private static final TextColor PRIMARY_BACKGROUND = rgb(255, 255, 255);
    private static final TextColor SECONDARY_BACKGROUND = rgb(240, 240, 240);
    private static final TextColor ACTIVE_BACKGROUND = rgb(255, 200, 200);

    private static final TextColor PRIMARY_FOREGROUND = rgb(50, 50, 50);
    private static final TextColor ACCENT_COLOR = rgb(215, 0, 0);

    public LightTheme() {
        super(PRIMARY_BACKGROUND, SECONDARY_BACKGROUND, PRIMARY_FOREGROUND, ACTIVE_BACKGROUND, ACCENT_COLOR);
    }

}
