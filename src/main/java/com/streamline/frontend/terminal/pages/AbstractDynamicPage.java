package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.backend.Dispatcher;

public abstract class AbstractDynamicPage extends AbstractBasePage {

    public AbstractDynamicPage(Dispatcher backend, TextGUIThread guiThread) {
        super(backend, guiThread);
    }

    abstract BasicWindow updateWindow();
}
