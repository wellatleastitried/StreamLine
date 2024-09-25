package com.walit.streamline;

import com.googlecode.lanterna.TerminalSize;

import com.walit.streamline.Communicate.Mode;

import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.is;

public class WindowStateTest {

    private Core core;

    @Before
    public void setup() {
        core = new Core(Mode.TESTING);
    }

    // Ensure getSize is returning a valid TerminalSize
    @Test
    public void getTerminalSize() {
        MatcherAssert.assertThat(core.getSize(core.buttonWidth, core.buttonHeight), is(new TerminalSize(core.buttonWidth, core.buttonHeight)));
    }
}
