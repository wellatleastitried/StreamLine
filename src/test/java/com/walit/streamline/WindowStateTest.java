package com.walit.streamline;

import com.googlecode.lanterna.gui2.*;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.MatcherAssert;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class WindowStateTest {

    private Core core;

    @Before
    public void setup() {
        core = new Core("TESING");
    }
    
    @Test
    public void getTerminalSize() {
        MatcherAssert.assertThat(core.getSize(10, 5), is(not(null)));
    }
}
