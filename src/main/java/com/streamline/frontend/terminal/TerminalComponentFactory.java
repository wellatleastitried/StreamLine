package com.streamline.frontend.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import com.streamline.frontend.terminal.themes.*;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

/**
 * Factory for creating consistently styled UI components. Centralizes styling and component creation logic.
 * @author wellatleastitried
 */
public class TerminalComponentFactory {

    private static TerminalComponentFactory instance;

    private final TerminalSize terminalSize;
    private final int buttonWidth;
    private final int buttonHeight;
    private final AbstractStreamLineTheme theme;

    private TerminalComponentFactory(Config config, TerminalSize terminalSize) {
        this.terminalSize = terminalSize;
        this.buttonWidth = terminalSize.getColumns() / 4;
        this.buttonHeight = 2;
        this.theme = config.getTheme();
        Logger.debug("TerminalComponentFactory: Initialized with terminal size " + terminalSize);
    }

    public static TerminalComponentFactory getInstance() {
        if (instance == null) {
            Logger.debug("TerminalComponentFactory: Instance not initialized. Call getInstance(Config, TerminalSize) first.");
            throw new IllegalStateException("TerminalComponentFactory not initialized. Call getInstance(Config, TerminalSize) first.");
        }
        return instance;
    }

    public static TerminalComponentFactory createInstance(Config config, TerminalSize terminalSize) {
        if (instance == null) {
            instance = new TerminalComponentFactory(config, terminalSize);
        }
        return instance;
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

    public Button createSongButton(String text, Runnable runner, TerminalSize sizeOfResultsBox) {
        Button button = new Button(text, runner);
        TerminalSize buttonSize = sizeOfResultsBox.withRows(buttonHeight / 3);
        button.setPreferredSize(buttonSize);
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

    public Panel createStandardPanel() {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1).setLeftMarginSize(2).setRightMarginSize(2));
        panel.setPreferredSize(new TerminalSize(terminalSize.getColumns() - 4, terminalSize.getRows() - 4));
        panel.setTheme(theme);
        return panel;
    }

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

    public String getFormattedTextForSongButton(int widthOfButton, int index, String title, String artist, String length) {
        int effectiveWidth = widthOfButton - 8; /* Take 8 off the width so the text isn't touching the border */

        final int MAX_TITLE_LENGTH = effectiveWidth / 3;
        final int MAX_ARTIST_LENGTH = effectiveWidth / 3;

        String formattedTitle = formatTextToFixedLength(title, MAX_TITLE_LENGTH);
        String formattedArtist = formatTextToFixedLength(artist, MAX_ARTIST_LENGTH);

        String indexStr = index + ") ";
        int fixedContentLength = indexStr.length() + MAX_TITLE_LENGTH + MAX_ARTIST_LENGTH + length.length();
        int totalPaddingNeeded = effectiveWidth - fixedContentLength;

        int titleToArtistPadding = totalPaddingNeeded / 2;
        int artistToLengthPadding = totalPaddingNeeded - titleToArtistPadding;

        String titleArtistSeparator = " ".repeat(Math.max(0, titleToArtistPadding));
        String artistLengthSeparator = " ".repeat(Math.max(0, artistToLengthPadding));

        String formattedText = String.format("%s%s%s%s%s%s",
                indexStr,
                formattedTitle,
                titleArtistSeparator,
                formattedArtist,
                artistLengthSeparator,
                length);
        if (formattedText.length() < widthOfButton) {
            formattedText += " ".repeat(widthOfButton - formattedText.length());
        } else if (formattedText.length() > widthOfButton) {
            formattedText = formattedText.substring(0, widthOfButton);
        }

        return formattedText;
    }

    // TODO: I need to fix this to make sure that maxLength is actually the size of the panel for results.
    private String formatTextToFixedLength(String text, int maxLength) {
        if (text == null || maxLength <= 0) {
            return "An error occurred while formatting text.";
        }
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        if (text.length() < maxLength) {
            return text + " ".repeat(maxLength - text.length());
        }
        return text;
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

    public AbstractStreamLineTheme getTheme() {
        return this.theme;
    }

    private String addPadding(String text) {
        return "  " + text + "  ";
    }
}
