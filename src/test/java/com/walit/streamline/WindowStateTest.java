package com.walit.streamline;

import com.googlecode.lanterna.gui2.*;
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
        core = new Core("TESING");
    }

    // Ensure getSize is returning a valid TerminalSize
    @Test
    public void getTerminalSize() {
        MatcherAssert.assertThat(core.getSize(10, 5), is(notNullValue()));
    }
}
