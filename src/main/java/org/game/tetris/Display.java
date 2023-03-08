package org.game.tetris;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Display {
    private Terminal terminal;
    private Size size;
    public Display(){
        init();
    }

    private void init() {
        try {
            terminal = TerminalBuilder.terminal();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public Size getSize() {
        return terminal.getSize();
    }
}
