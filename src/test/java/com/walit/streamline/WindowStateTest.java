package com.walit.streamline;

import com.googlecode.lanterna.TerminalSize;

import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.is;

public class WindowStateTest {

    private Core core;

    @Before
    public void setup() {
        core = new Core(Core.Mode.TESTING);
    }

    // Ensure getSize is returning a valid TerminalSize
    @Test
    public void getTerminalSize() {
        MatcherAssert.assertThat(core.getSize(core.buttonWidth, core.buttonHeight), is(new TerminalSize(core.buttonWidth, core.buttonHeight)));
    }

    @Test
    public void ensureSuccessfulWindowBuilds() {
        MatcherAssert.assertThat(core.createMainMenuWindow().getTitle(), is("StreamLine Music Player"));
        MatcherAssert.assertThat(core.createHelpMenu().getTitle(), is("StreamLine Help Menu"));
    }
}
