package com.streamline.frontend.terminal;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.gui2.*;

import org.tinylog.Logger;

public class TerminalKeybinds implements TextGUI.Listener {

    private final TextGUI textGUI;
    private TextBox currentTextBox = null;

    public TerminalKeybinds(TextGUI textGUI) {
        this.textGUI = textGUI;
        Logger.debug("Registering keybinds to terminal interface");
        this.textGUI.addListener(this);
    }

    @Override
    public boolean onUnhandledKeyStroke(TextGUI textGUI, KeyStroke keyStroke) {
        Window activeWindow = null;
        if (textGUI instanceof WindowBasedTextGUI) {
            activeWindow = ((WindowBasedTextGUI) textGUI).getActiveWindow();
        }

        if (activeWindow == null) {
            return false;
        }

        Interactable focused = activeWindow.getFocusedInteractable();
        if (focused instanceof TextBox) {
            if (currentTextBox != focused) {
                currentTextBox = (TextBox) focused;
            }

            if (keyStroke.getKeyType() == KeyType.Escape) {
                Logger.debug("Escape pressed, exiting input mode");

                /* Send a 'j' to escape from the text box */
                KeyStroke simKeyStroke = new KeyStroke('j', false, false);
                return handleCustomKeybinds(simKeyStroke, (BasicWindow) activeWindow);
            }
        } else {
            currentTextBox = null;
        }

        if (keyStroke.getCharacter() != null) {
            return handleCustomKeybinds(keyStroke, (BasicWindow) activeWindow);
        }

        return false;
    }

    private boolean handleCustomKeybinds(KeyStroke keyStroke, BasicWindow activeWindow) {
        Character c = keyStroke.getCharacter();
        if (c == null) {
            return false;
        }

        KeyType mappedKey = null;
        switch (c) {
            case 'h':
                mappedKey = KeyType.ArrowLeft;
                break;
            case 'j':
                mappedKey = KeyType.ArrowDown;
                break;
            case 'k':
                mappedKey = KeyType.ArrowUp;
                break;
            case 'l':
                mappedKey = KeyType.ArrowRight;
                break;
            case 'd':
                if (keyStroke.isCtrlDown() && !keyStroke.isShiftDown() && !keyStroke.isAltDown()) {
                    for (int i = 0; i < 10; i++) {
                        mappedKey = KeyType.ArrowDown;
                        if (!activeWindow.handleInput(new KeyStroke(mappedKey))) {
                            Logger.warn("Error handling Ctrl+d, exiting key sim early.");
                            break;
                        }
                    }
                }
                mappedKey = null;
                break;
            case 'u':
                if (keyStroke.isCtrlDown() && !keyStroke.isShiftDown() && !keyStroke.isAltDown()) {
                    for (int i = 0; i < 10; i++) {
                        mappedKey = KeyType.ArrowUp;
                        if (!activeWindow.handleInput(new KeyStroke(mappedKey))) {
                            Logger.warn("Error handling Ctrl+u, exiting key sim early.");
                            break;
                        }
                    }
                }
                mappedKey = null;
                break;
            default:
                return false;
        }

        if (mappedKey != null) {
            KeyStroke arrowKeyStroke = new KeyStroke(mappedKey);
            return activeWindow.handleInput(arrowKeyStroke);
        }
        return false;
    }
}
