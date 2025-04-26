package com.streamline.frontend.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.graphics.*;

/**
 * Factory for creating consistently styled UI components. Centralizes styling and component creation logic.
 * @author wellatleastitried
 */
public class TerminalComponentFactory {
    private final TerminalSize terminalSize;
    private final int buttonWidth;
    private final int buttonHeight;
    private final StreamLineTheme theme;

    public TerminalComponentFactory(TerminalSize terminalSize) {
        this.terminalSize = terminalSize;
        this.buttonWidth = terminalSize.getColumns() / 4;
        this.buttonHeight = 2;
        this.theme = new StreamLineTheme();
    }

    public Label createLabel(String text) {
        Label label = new Label(addPadding(text));
        label.setTheme(theme);
        return label;
    }

    public Label createLabelWithSize(String text) {
        return createLabelWithSize(text, buttonWidth, buttonHeight);
    }

    public Label createLabelWithSize(String text, int width, int height) {
        Label label = createLabel(text);
        label.setPreferredSize(new TerminalSize(width, height));
        label.setTheme(theme);
        return label;
    }

    public Button createButton(String text, Runnable runner) {
        return createButton(text, runner, buttonWidth, buttonHeight);
    }

    public Button createButton(String text, Runnable runner, int width, int height) {
        Button button = new Button(text, runner);
        button.setPreferredSize(new TerminalSize(width, height));
        button.setTheme(theme);
        return button;
    }

    public EmptySpace createEmptySpace() {
        EmptySpace space = new EmptySpace();
        space.setPreferredSize(new TerminalSize(buttonWidth, buttonHeight));
        space.setVisible(false);
        space.setTheme(theme);
        return space;
    }

    /*
    public Panel createSearchResultsPanel() {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1).setLeftMarginSize(1).setRightMarginSize(1));
        panel.setPreferredSize(new TerminalSize(terminalSize.getColumns() - 8, terminalSize.getRows() - 8));
        return Panels.bordered(panel, "Results", Borders.singleLine());
    }
    */

    public Panel createStandardPanel() {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1).setLeftMarginSize(2).setRightMarginSize(2));
        panel.setPreferredSize(new TerminalSize(terminalSize.getColumns() - 4, terminalSize.getRows() - 4));
        panel.setTheme(theme);
        return panel;
    }

    /*
    public Panel createBorderedPanel(String title) {
        Panel panel = createStandardPanel();
        return Panels.bordered(panel, title, BorderStyle.DOUBLE);
    }
    */

    public TextBox createSearchBox() {
        TextBox textBox = new TextBox(new TerminalSize(terminalSize.getColumns() / 2, 1));
        textBox = (TextBox) textBox.withBorder(Borders.singleLine("Enter search term"));
        textBox.setTheme(theme);
        return textBox;
    }

    public Component addMargin(Component component, int margin) {
        Panel wrapper = new Panel();
        wrapper.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        wrapper.addComponent(new EmptySpace(new TerminalSize(0, margin)));
        wrapper.addComponent(component);
        wrapper.addComponent(new EmptySpace(new TerminalSize(0, margin)));
        wrapper.setTheme(theme);
        return wrapper;
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

    public StreamLineTheme getTheme() {
        return this.theme;
    }

    private String addPadding(String text) {
        return "  " + text + "  ";
    }
}
