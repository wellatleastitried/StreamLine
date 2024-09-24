package com.walit.streamline;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

public class WindowStateTest {

    private Core core;

    @Before
    public void setup() {
        core = new Core();
    }

    @Test
    public void getButtonHeight() {
        MatcherAssert.assertThat(core.buttonHeight, is(2));
    }
}
