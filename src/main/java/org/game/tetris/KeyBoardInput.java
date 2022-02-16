package org.game.tetris;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class KeyBoardInput {
     private Key keyBoardKey=Key.NONE;

    public KeyBoardInput() {
      new Thread(()->{
          try(Terminal terminal= TerminalBuilder.terminal()){
              while (true) setKeyBoardKey(getKeys(terminal.reader().read()));
          } catch (IOException e) {
              e.printStackTrace();
          }
      }).start();
    }
    private static Key getKeys(int ch) {
        return switch (ch) {
            case 9->   Key.TAB;
            case 13->  Key.ENTER;
            case 27->  Key.ESC;
            case 8->   Key.BACKSPACE;
            case 65 -> Key.UP;
            case 32->  Key.SPACE;
            case 66 -> Key.DOWN;
            case 68 -> Key.LEFT;
            case 67 -> Key.RIGHT;
            case 100-> Key.DROP;
            case 104-> Key.HOLD;
            default -> Key.NONE;
        };
    }

    public Key getKeyBoardKey() {
        return keyBoardKey;
    }

    public void setKeyBoardKey(Key keyBoardKey) {

        this.keyBoardKey = keyBoardKey;
    }
}
