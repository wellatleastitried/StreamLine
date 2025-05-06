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

                /* Send an 'h' to escape from the text box */
                KeyStroke simKeyStroke = new KeyStroke('j', false, false);
                return handleVimKeys(simKeyStroke, (BasicWindow) activeWindow);
            }
        } else {
            currentTextBox = null;
        }

        if (keyStroke.getCharacter() != null) {
            return handleVimKeys(keyStroke, (BasicWindow) activeWindow);
        }

        return false;
    }

    private boolean handleVimKeys(KeyStroke keyStroke, BasicWindow activeWindow) {
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
