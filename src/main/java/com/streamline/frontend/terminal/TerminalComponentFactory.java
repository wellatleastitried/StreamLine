package com.streamline.frontend.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.streamline.utilities.LanguagePeer;

/**
 * Factory for creating consistently styled UI components. Centralizes styling and component creation logic.
 * @author wellatleastitried
 */
public class TerminalComponentFactory {
    private final TerminalSize terminalSize;
    private final int buttonWidth;
    private final int buttonHeight;

    public TerminalComponentFactory(TerminalSize terminalSize) {
        this.terminalSize = terminalSize;
        this.buttonWidth = terminalSize.getColumns() / 4;
        this.buttonHeight = 2;
    }

    public Label createLabel(String text) {
        Label label = new Label(addPadding(text));
        label.addStyle(SGR.BOLD);
        return label;
    }

    public Label createLabelWithSize(String text) {
        return createLabelWithSize(text, buttonWidth, buttonHeight);
    }

    public Label createLabelWithSize(String text, int width, int height) {
        Label label = createLabel(text);
        label.setPreferredSize(new TerminalSize(width, height));
        return label;
    }

    public Button createButton(String text, Runnable runner) {
        return createButton(text, runner, buttonWidth, buttonHeight);
    }

    public Button createButton(String text, Runnable runner, int width, int height) {
        Button button = new Button(text, runner);
        button.setPreferredSize(new TerminalSize(width, height));
        return button;
    }

    public EmptySpace createEmptySpace() {
        EmptySpace space = new EmptySpace();
        space.setPreferredSize(new TerminalSize(buttonWidth, buttonHeight));
        space.setVisible(false);
        return space;
    }

    public Panel createStandardPanel() {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.setPreferredSize(new TerminalSize(40, 20));
        panel.setFillColorOverride(TextColor.ANSI.BLACK);
        return panel;
    }

    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public int getButtonHeight() {
        return buttonHeight;
    }

    private String addPadding(String text) {
        return "  " + text + "  ";
    }
}
