package com.walit.streamline;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.TerminalSize;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class WindowStateTest {

    private Core core;

    @Before
    public void setup() {
        core = new Core(MODE.TESTING);
    }

    // Ensure getSize is returning a valid TerminalSize
    @Test
    public void getTerminalSize() {
        MatcherAssert.assertThat(core.getSize(buttonWidth, buttonHeight), is(new TerminalSize(core.buttonWidth, core.buttonHeight)));
    }
}
