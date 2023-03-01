package org.game.tetris;

import org.jline.utils.InfoCmp;

import javax.sound.sampled.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


public class Game  {
    private Tile[][] box = new Tile[30][15];
   private Tile[] tetrominoes;
    //private Tile[][] _tetrominoes;
    private boolean hold_key = false;
    private double tetrominoes_speed=0.001;
    private int game_object_frame=0;
   private final Tetrominoes tetris = new Tetrominoes();
    //private Tetrominoes _tetris = new Tetrominoes();
    private int score = 0;
    private int lines = 0;
    private int tetris_no;
    private int dummy_tetris_no;
    private final int[] tetrominoes_data = new int[7];
    private int no_of_tetrominoes = 1;
    private Tile[] dummy_tetris;
    private boolean game_status = true;
    private KeyBoardInput keyBoardInput;
    private Display display;

    public Game(KeyBoardInput keyBoardInput, Display display) {
        this.keyBoardInput = keyBoardInput;
        this.display = display;
    }

    void start() throws IOException, InterruptedException {

        initialize_tetris(15, 30);
        tetris_no = 3;
        tetrominoes = tetris.getTetrominoes((int) (Math.random() * 10 + 1), 1, tetris_no);
        dummy_tetris_no = (int) (Math.random() * 7 + 1);
        dummy_tetris = tetris.getTetrominoes(30, 8, dummy_tetris_no);
        //_tetrominoes = _tetris._getTetrominoes(7, 10, tetris_no);

        while (true) {
            if (game_status) {
                draw();
                check_for_tetris();
                move();
            } else {
                if (keyBoardInput.getKeyBoardKey() == Key.ESC)
                    System.exit(-1);
                if (keyBoardInput.getKeyBoardKey() == Key.SPACE) {
                    box = new Tile[30][15];
                    initialize_tetris(15, 30);
                    game_status = true;

                }
                game_statistics();
            }
            Thread.sleep(100);
            //game_object_frame=game_object_frame>tetrominoes_speed*1000?0:game_object_frame+30;
            display.terminal.puts(InfoCmp.Capability.clear_screen);
        }

    }

    private void check_for_tetris() throws IOException, InterruptedException {
        int c = 0;
        int k = 0;
        for (int i = 28; i >= 1; i--) {
            if (i == 1) {
                game_status = false;
                play("sound/gameover.wav", -0.0f, false);
                return;
            }
            var val = isFilled(i);
            if (val.equals("Filled") || val.equals("Half_Filled")) {
                if (val.equals("Filled")) {
                    c++;
                } else {
                    if (c > 0) {
                        if (c == 1)
                            play("sound/clear.wav", -0.0f, false);
                        else play("sound/line.wav", -0.0f, false);
                        clean_this_box(i + 1, i + c);
                        k = k + c;
                        c = 0;
                    }
                    if (k > 0) {
                        box[i + k] = box[i];
                        var t = new Tile[15];
                        t[0] = new Tile(0, i, Color.WHITE);
                        t[14] = new Tile(19, i, Color.WHITE);
                        box[i] = t;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void clean_this_box(int start, int end) throws IOException, InterruptedException {
        int p = 9, q = 10;
        while (p >= 1 && q <= 13) {
            for (int i = start; i <= end; i++) {
                box[i][q] = null;
                box[i][p] = null;
            }
            p--;
            q++;
            display.terminal.puts(InfoCmp.Capability.clear_screen);
            draw();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lines = lines + end - start + 1;
        score = score + (end - start + 1) * 10;
    }

    private void move() {
        var key_pressed = keyBoardInput.getKeyBoardKey();
        boolean condition = switch (key_pressed) {
            case RIGHT -> isSpace_right();
            case LEFT -> isSpace_left();
            default -> false;
        };
        if (key_pressed == Key.DOWN) {
            rotate(key_pressed);
            play("sound/rotate.wav", -10.0f, false);
        }
        if (key_pressed == Key.UP) {
            rotate(key_pressed);
            play("sound/rotate.wav", -10.0f, false);
        }
        if (key_pressed == Key.HOLD)
            hold_key = !hold_key;
        if (key_pressed == Key.ESC)
            System.exit(-1);
        int step;
        if (key_pressed == Key.SPACE && !hold_key) {
            step = getAvailable(30);
            play("sound/fall.wav", -10.0f, false);
        } else step = getAvailable(1);
        if (step > 0 || condition) {
           for (var t : tetrominoes) {
                if (key_pressed != Key.LEFT && key_pressed != Key.RIGHT &&
                        key_pressed != Key.DOWN && key_pressed != Key.UP && !hold_key &&
                        (game_object_frame==0 || key_pressed==Key.SPACE))
                    t.y = t.y + step;
                else if (key_pressed == Key.LEFT && condition)
                    t.x = t.x - 1;
                else if (key_pressed == Key.RIGHT && condition)
                    t.x = t.x + 1;

           }
        } else {
           for (var t : tetrominoes) {
                box[t.y][t.x] = t;
            }
            no_of_tetrominoes++;
            tetris_no = dummy_tetris_no;
            tetrominoes_data[tetris_no - 1] = tetrominoes_data[tetris_no - 1] + 1;
            tetrominoes = tetris.getTetrominoes((int) (Math.random() * 10 + 1), 1, tetris_no);
            dummy_tetris_no = (int) (Math.random() * 7 + 1);
            dummy_tetris = tetris.getTetrominoes(30, 8, dummy_tetris_no);
            if (key_pressed != Key.SPACE)
                play("sound/slow-hit.wav", -0.0f, false);

        }
        keyBoardInput.setKeyBoardKey(Key.NONE);
    }

   /* private void updateBoxMemory() {
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    box[_tetrominoes[i][j].y][_tetrominoes[i][j].x] =_tetrominoes[i][j];
                }
            }
        }
    }*/

    private boolean isSpace_right() {
         return box[tetrominoes[0].y][tetrominoes[0].x + 1] == null &&
                 box[tetrominoes[1].y][tetrominoes[1].x + 1] == null &&
                 box[tetrominoes[2].y][tetrominoes[2].x + 1] == null &&
                 box[tetrominoes[3].y][tetrominoes[3].x + 1] == null;
     }

     private boolean isSpace_left() {
         return box[tetrominoes[0].y][tetrominoes[0].x - 1] == null &&
                 box[tetrominoes[1].y][tetrominoes[1].x - 1] == null &&
                 box[tetrominoes[2].y][tetrominoes[2].x - 1] == null &&
                 box[tetrominoes[3].y][tetrominoes[3].x - 1] == null;
     }
   /* private boolean _isSpace_left() {
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    if (box[_tetrominoes[i][j].y][_tetrominoes[i][j].x - 1] != null)
                        return false;
                }
            }
        }
        return true;
    }
    private boolean _isSpace_right() {
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    if (box[_tetrominoes[i][j].y][_tetrominoes[i][j].x + 1] != null)
                        return false;
                }
            }
        }
        return true;
    }
*/
  private void rotate(Key key) {
        int X = tetrominoes[2].x;
        int Y = tetrominoes[2].y;
        switch (tetris_no) {
            //▓▓▓▓▓▓▓▓▓▓

            case 1 -> {
                if (key == Key.UP) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X, X, X, Y + 2, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X + 2, X + 1, X - 1, Y, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X, X, X, Y - 2, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X - 2, X - 1, X + 1, Y, Y, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X, X, X, Y - 2, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X + 2, X + 1, X - 1, Y, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X, X, X, Y - 2, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X - 2, X - 1, X + 1, Y, Y, Y);
                    }
                }
            }

           // ▓
            //▓▓▓▓▓▓▓▓▓▓

            case 2 -> {
                if (key == Key.UP) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X - 1, X, X, Y + 1, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X + 1, X + 1, X - 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X + 1, X, X, Y - 1, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X - 1, X + 1, Y - 1, Y, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X + 1, X, X, Y - 1, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X + 1, X + 1, X - 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X - 1, X, X, Y + 1, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X - 1, X + 1, Y - 1, Y, Y);
                    }
                }
            }

            //         ▓
            //▓▓▓▓▓▓▓▓▓▓
            case 3 -> {
                if (key == Key.UP) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[1].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X + 1, X, X, Y + 1, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X - 1, X + 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {

                        is_rotatable_rotate(X - 1, X, X, Y - 1, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y
                    ) {
                        is_rotatable_rotate(X + 1, X + 1, X - 1, Y - 1, Y, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[1].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X - 1, X, X, Y - 1, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X - 1, X + 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].y < tetrominoes[0].y) {
                        is_rotatable_rotate(X + 1, X, X, Y + 1, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[0].x > tetrominoes[3].x) {
                        is_rotatable_rotate(X + 1, X + 1, X - 1, Y - 1, Y, Y);
                    }
                }
            }

              //▓▓▓▓
            //▓▓▓▓
            case 5 -> {
                if (key == Key.UP) {
                    if (tetrominoes[1].x == tetrominoes[2].x && tetrominoes[3].x < tetrominoes[0].x) {
                        is_rotatable_rotate(X + 1, X + 1, X, Y + 1, Y, Y - 1);
                    } else if (tetrominoes[1].y == tetrominoes[2].y && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X, X + 1, Y + 1, Y + 1, Y);
                    } else if (tetrominoes[1].x == tetrominoes[2].x && tetrominoes[3].y < tetrominoes[0].y) {
                        is_rotatable_rotate(X - 1, X - 1, X, Y - 1, Y, Y + 1);
                    } else if (tetrominoes[1].y == tetrominoes[2].y && tetrominoes[3].x > tetrominoes[1].x) {
                        is_rotatable_rotate(X + 1, X, X - 1, Y - 1, Y - 1, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[1].x == tetrominoes[2].x && tetrominoes[3].x < tetrominoes[0].x) {
                        is_rotatable_rotate(X + 1, X + 1, X, Y + 1, Y, Y - 1);
                    } else if (tetrominoes[1].y == tetrominoes[2].y && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X - 1, X, X + 1, Y + 1, Y + 1, Y);
                    } else if (tetrominoes[1].x == tetrominoes[2].x && tetrominoes[3].y < tetrominoes[0].y) {
                        is_rotatable_rotate(X - 1, X - 1, X, Y - 1, Y, Y + 1);
                    } else if (tetrominoes[1].y == tetrominoes[2].y && tetrominoes[3].x > tetrominoes[1].x) {
                        is_rotatable_rotate(X + 1, X, X - 1, Y - 1, Y - 1, Y);
                    }
                }
            }

               //▓▓
            //▓▓▓▓▓▓▓▓

            case 6 -> {
                if (key == Key.UP) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X + 1, X, X, Y, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[2].y < tetrominoes[3].y) {
                        is_rotatable_rotate(X, X + 1, X - 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X - 1, X, X, Y, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X, X - 1, X + 1, Y - 1, Y, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x > tetrominoes[2].x) {
                        is_rotatable_rotate(X - 1, X, X, Y, Y + 1, Y - 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y < tetrominoes[2].y) {
                        is_rotatable_rotate(X, X + 1, X - 1, Y + 1, Y, Y);
                    } else if (tetrominoes[2].y == tetrominoes[3].y && tetrominoes[3].x < tetrominoes[2].x) {
                        is_rotatable_rotate(X + 1, X, X, Y, Y - 1, Y + 1);
                    } else if (tetrominoes[2].x == tetrominoes[3].x && tetrominoes[3].y > tetrominoes[2].y) {
                        is_rotatable_rotate(X, X - 1, X + 1, Y - 1, Y, Y);
                    }
                }

            }

              //▓▓▓▓
                //▓▓▓▓

            case 7 -> {
                if (key == Key.UP) {
                    if (tetrominoes[2].x == tetrominoes[1].x && tetrominoes[3].y > tetrominoes[0].y) {
                        is_rotatable_rotate(X + 1, X + 1, X, Y - 1, Y, Y + 1);
                    } else if (tetrominoes[2].y == tetrominoes[1].y && tetrominoes[3].x < tetrominoes[0].x) {
                        is_rotatable_rotate(X + 1, X, X - 1, Y + 1, Y + 1, Y);
                    } else if (tetrominoes[2].x == tetrominoes[1].x && tetrominoes[3].x < tetrominoes[0].x) {
                        is_rotatable_rotate(X - 1, X - 1, X, Y + 1, Y, Y - 1);
                    } else if (tetrominoes[2].y == tetrominoes[1].y && tetrominoes[3].x > tetrominoes[0].x) {
                        is_rotatable_rotate(X - 1, X, X + 1, Y - 1, Y - 1, Y);
                    }
                }
                if (key == Key.DOWN) {
                    if (tetrominoes[2].x == tetrominoes[1].x && tetrominoes[3].x > tetrominoes[0].x) {
                        is_rotatable_rotate(X - 1, X - 1, X, Y - 1, Y, Y + 1);
                    } else if (tetrominoes[2].y == tetrominoes[1].y && tetrominoes[3].x > tetrominoes[0].x) {
                        is_rotatable_rotate(X + 1, X, X - 1, Y + 1, Y + 1, Y);
                    } else if (tetrominoes[2].x == tetrominoes[1].x && tetrominoes[0].y > tetrominoes[3].y) {
                        is_rotatable_rotate(X - 1, X - 1, X, Y + 1, Y, Y - 1);
                    } else if (tetrominoes[2].y == tetrominoes[1].y && tetrominoes[3].x < tetrominoes[0].x) {
                        is_rotatable_rotate(X + 1, X, X - 1, Y + 1, Y + 1, Y);
                    }
                }
            }
        }
    }
    private int getAvailable(int step) {
        int i;
        for (i = 1; i <= step; ) {

            if (box[tetrominoes[0].y + i][tetrominoes[0].x] == null &&
                    box[tetrominoes[1].y + i][tetrominoes[1].x] == null &&
                    box[tetrominoes[2].y + i][tetrominoes[2].x] == null &&
                    box[tetrominoes[3].y + i][tetrominoes[3].x] == null)
                i++;
            else break;
        }
        return i - 1;

    }
   /* private int _getAvailable(int step) {
        int i;
        for (i = 1; i <= step; ) {
            int c=0;
            for (Tile[] tu : _tetrominoes) {
                for (int k = 0; k < _tetrominoes[0].length; k++) {
                    if (tu[k] != null) {
                        if (box[tu[k].y + i][tu[k].x] != null) {
                            c = 1;
                            break;
                        }

                    }
                }
                if (c == 1)
                    break;
            }
            if (c==0)
                i++;
            else break;
        }
        return i - 1;

    }*/
    private String message() {
        return "" + (" ".repeat(21 - "".length() - "Tetris".length() / 2)) + "Tetris";
    }

    private void draw() {
        System.out.println(message() + "\n");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < box.length; i++) {
            Tile[] tiles = box[i];
            for (int j = 0; j < tiles.length; j++) {
                Tile tile = tiles[j];
                if (tile != null) {
                    s.append(tile.getTile());
                } else {
                    if (i < tetrominoes[0].y - 4 || i > tetrominoes[0].y + 4 || !print_tetrominoes(i, j)) {
                        s.append("   ");
                    } else
                        s.append(tetrominoes[0].getTile());
                }
            }
            if (i == 2)
                s.append(" Score : ").append(score);
            if (i == 4)
                s.append(" Lines : ").append(lines);
            if (i == 6)
                s.append(" Next  : ");
            if (i == 8) {
                s.append("    ");
                for (int j = 30; j <= 33; j++) {
                    boolean flag = false;
                    for (var k : dummy_tetris) {
                        if (k.x == j && k.y == 8) {
                            s.append(k.getTile());
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                        s.append("   ");
                }
            }
            if (i == 9) {
                s.append("    ");
                for (int j = 30; j <= 33; j++) {
                    boolean flag = false;
                    for (var k : dummy_tetris) {
                        if (k.x == j && k.y == 9) {
                            s.append(k.getTile());
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                        s.append("   ");
                }
            }
            if (i == 12)
                s.append(" Hold key : ").append(hold_key ? "on" : "off");
            s.append('\n');
        }
        System.out.println(s);
    }
    record Tuples(boolean condition,Tile tiles){}
    private boolean print_tetrominoes(int i, int j) {
        for (var k : tetrominoes) {
            if (k.x == j && k.y == i) {
                return true;
            }
        }
        return false;
    }
       /* Tile tile=null;
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    tile=_tetrominoes[i][j];
                    if (_tetrominoes[i][j].y==a &&_tetrominoes[i][j].x ==b)
                        return new Tuples(true,tile);
                }
            }
        }
        return new Tuples(false,null);
    }*/

   private void is_rotatable_rotate(int x0, int x1, int x3, int y0, int y1, int y3) {
        if (isValidPoints(x0, x1, x3, y0, y1, y3) && box[y0][x0] == null && box[y1][x1] == null && box[y3][x3] == null) {
            tetrominoes[0].x = x0;
            tetrominoes[1].x = x1;
            tetrominoes[3].x = x3;
            tetrominoes[0].y = y0;
            tetrominoes[1].y = y1;
            tetrominoes[3].y = y3;

        }
    }

    private boolean isRotatable(int x, int y) {
        return !(x <= 0 || x >= 14) && !(y <= 0 || y >= 29) && (box[y][x] == null);
    }

    private boolean isValidPoints(int... c) {
        for (int i = 0; i <= 2; i++) {
            if (c[i] <= 0 || c[i] >= 14)
                return false;
        }
        for (int i = 3; i <= 5; i++) {
            if (c[i] <= 0 || c[i] >= 29)
                return false;
        }
        return true;
    }

    private void initialize_tetris(int width, int height) {
        for (int i = 0; i < height; i++) {
            if ((i == 0 || i == height - 1)) {
                for (int j = 0; j < width; j++) {
                    box[i][j] = new Tile(i, j, Color.WHITE);
                }
            } else {
                box[i][0] = new Tile(i, 0, Color.WHITE);
                box[i][width - 1] = new Tile(i, width - 1, Color.WHITE);
            }
        }
    }

    private void game_statistics() {
        String GAME_OVER = """
                █████▀██████████████████████████████████████████████
                █─▄▄▄▄██▀▄─██▄─▀█▀─▄█▄─▄▄─███─▄▄─█▄─█─▄█▄─▄▄─█▄─▄▄▀█
                █─██▄─██─▀─███─█▄█─███─▄█▀███─██─██▄▀▄███─▄█▀██─▄─▄█
                ▀▄▄▄▄▄▀▄▄▀▄▄▀▄▄▄▀▄▄▄▀▄▄▄▄▄▀▀▀▄▄▄▄▀▀▀▄▀▀▀▄▄▄▄▄▀▄▄▀▄▄▀
                """;
        System.out.println(GAME_OVER);
        System.out.println("Tetrominoes statistics" + '\n');
        System.out.println("Score : " + score);
        System.out.println("Lines : " + lines);
        System.out.println();
        String t1 = """
                \033[0;34m████████████\33[0m     %s
                """;
        String t2 = """
                \033[0;31m██
                \033[0;31m████████████\33[0m     %s
                """;
        String t3 = """
                \033[0;36m          ██
                \033[0;36m████████████\33[0m     %s
                """;
        String t4 = """
                \033[0;35m██████
                \033[0;35m██████\33[0m         %s
                """;
        String t5 = """
                  \033[0;33m   ██████
                  \033[0;33m██████\33[0m       %s
                """;
        String t6 = """
                  \033[0;32m   ██
                \033[0;32m ██████████\33[0m     %s
                  """;
        String t7 = """
                 \033[0;96m██████
                  \033[0;96m  ██████\33[0m     %s
                """;
        System.out.printf((t1) + "%n", (int) (tetrominoes_data[0] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t2) + "%n", (int) (tetrominoes_data[1] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t3) + "%n", (int) (tetrominoes_data[2] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t4) + "%n", (int) (tetrominoes_data[3] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t5) + "%n", (int) (tetrominoes_data[4] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t6) + "%n", (int) (tetrominoes_data[5] * 100.0 / no_of_tetrominoes) + "%");
        System.out.printf((t7) + "%n", (int) (tetrominoes_data[6] * 100.0 / no_of_tetrominoes) + "%");
        System.out.println();
        System.out.println("Enter space bar to continue.....");
    }

    private String isFilled(int i) {
        var a = box[i];
        int l = 0, m = 0;
        for (int p = 1; p <= 13; p++) {
            if (a[p] == null)
                l++;
            else
                m++;
        }
        if (l == 13)
            return "Empty";
        if (m == 13)
            return "Filled";
        return "Half_Filled";
    }

    static void play(String name, float volume, boolean isRepeatable) {
        var url = GameLauncher.class.getResource(name);
        Clip audioClip;
        var p=new Properties();
        try (var resourceAsStream = GameLauncher.class.getResourceAsStream("sound/sound.properties")) {
            try {
                p.load(resourceAsStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(url))) {
            //var format = audioInputStream.getFormat();
            //DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioInputStream);
            var level = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            level.setValue(volume);
            if (isRepeatable)
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
/*
    @Override
    public void moveRight() {
        //_tetris.xT=_tetris.xT+1;
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    _tetrominoes[i][j].x= _tetrominoes[i][j].x+1;

                }
            }
        }
    }

    @Override
    public void moveLeft() {
        //_tetris.xT=_tetris.xT-1;
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                    _tetrominoes[i][j].x= _tetrominoes[i][j].x-1;

                }
            }
        }
    }

    @Override
    public void moveUp() {

    }

    @Override
    public void moveDown(int steps) {
        //_tetris.yT=_tetris.yT+steps;
        for (int i = 0; i < _tetrominoes.length; i++) {
            for (int j = 0; j < _tetrominoes[0].length; j++) {
                if (_tetrominoes[i][j]!=null){
                   _tetrominoes[i][j].y= _tetrominoes[i][j].y+steps;

                }
            }
        }
    }

    @Override
    public void rotateRight() {
        var b = new Tile[_tetrominoes[0].length][_tetrominoes.length];
        int c = 0;
        int u,u1;
        int v,v1;
        if (_tetris.orientation.equals(Orientation.HORIZONTAL)) {
            u=(_tetrominoes[0][2]!=null)?_tetrominoes[0][2].y-2:_tetrominoes[1][2].y-2;
            v=(_tetrominoes[0][2]!=null)?_tetrominoes[0][2].x:_tetrominoes[1][2].x;
        }
        else {
            v=(_tetrominoes[2][0]!=null)?_tetrominoes[2][0].x-2:_tetrominoes[2][1].x-2;
            u=(_tetrominoes[2][0]!=null)?_tetrominoes[2][0].y:_tetrominoes[2][1].y;
        }
        v1=v;
        for (int i = _tetrominoes.length - 1; i >= 0; i--) {
            u1=u;
            var k = _tetrominoes[i];
            for (int j = 0; j < k.length; j++) {
                if (k[j] != null) {
                    if (isRotatable(v1,u1)) {
                        //k[j].x = _tetris.xT + i;
                        //k[j].y = _tetris.yT + j;
                        b[j][_tetrominoes.length - 1 - i] = new Tile(v1, u1,k[j].color);
                    } else {
                        c = 1;
                        break;
                    }
                }
                u1++;
            }
            v1++;
            if (c == 1)
                break;
        }
        if (c != 1){
            _tetrominoes = b;
            _tetris.orientation=(_tetris.orientation.equals(Orientation.VERTICAL))?Orientation.HORIZONTAL:Orientation.VERTICAL;
        }
    }

    @Override
    public void rotateLeft() {
        var b = new Tile[_tetrominoes[0].length][_tetrominoes.length];
        int t = 0;
        int c = 0;
        for (int i = _tetrominoes.length - 1; i >= 0; i--) {
            var k = _tetrominoes[i];
            int p = 0;
            for (int j = k.length - 1; j >= 0; j--) {
                if (k[j] != null) {
                    if (isRotatable(_tetris.yT + p, _tetris.xT + t)) {
                        k[j].x = _tetris.xT + t;
                        k[j].y = _tetris.yT + p;
                        b[p][_tetrominoes.length - 1 - t] = k[j];
                        p++;
                    } else {
                        c = 1;
                        break;
                    }
                }
            }
            t++;
            if (c == 1)
                break;
        }
        if (c != 1)
            _tetrominoes = b;
    }*/
}
