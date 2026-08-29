package com.streamline.frontend.terminal.themes;

import com.googlecode.lanterna.*;

public class SolarizedTheme extends AbstractStreamLineTheme {

    private static final TextColor PRIMARY_BACKGROUND = rgb(0, 43, 54);
    private static final TextColor SECONDARY_BACKGROUND = rgb(7, 54, 66);
    private static final TextColor ACTIVE_BACKGROUND = rgb(38, 139, 210);

    private static final TextColor PRIMARY_FOREGROUND = rgb(238, 232, 213);
    private static final TextColor ACCENT_COLOR = rgb(181, 137, 0);

    public SolarizedTheme() {
        super(PRIMARY_BACKGROUND, SECONDARY_BACKGROUND, PRIMARY_FOREGROUND, ACTIVE_BACKGROUND, ACCENT_COLOR);
    }

}
