package org.game.tetris;

import org.jline.utils.InfoCmp;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;


public class Game {
    private final int WIDTH = 12;
    private final int HEIGHT = 30;
    private Tile[][] box = new Tile[HEIGHT][WIDTH];
    private Tile[] tetrominoes;
    private boolean hold_key = false;
    private int game_object_frame = 0;
    private final Tetrominoes tetris = new Tetrominoes();
    private int score = 0;
    private int lines = 0;
    private int tetris_no;
    private int dummy_tetris_no;
    private final int[] tetrominoes_data = new int[7];
    private int no_of_tetrominoes = 1;
    private Tile[] dummy_tetris;
    private boolean game_status = true;
    private final KeyBoardInput keyBoardInput;
    private final Display display;
    private int _tetris_timer = 0;

    public Game(KeyBoardInput keyBoardInput, Display display) {
        this.keyBoardInput = keyBoardInput;
        this.display = display;
    }

    void start() throws InterruptedException {
        initialize_tetris();
        tetris_no = 3;
        tetrominoes = tetris.getTetrominoes((int) (Math.random() * (WIDTH-5) + 1), 1, tetris_no);
        dummy_tetris_no = (int) (Math.random() * 7 + 1);
        dummy_tetris = tetris.getTetrominoes(0, 8, dummy_tetris_no);
        while (true) {
            if (game_status) {
                draw();
                check_for_tetris();
                move();
            } else {
                if (keyBoardInput.getKeyBoardKey() == Key.ESC)
                    System.exit(-1);
                if (keyBoardInput.getKeyBoardKey() == Key.RESTART) {
                    box = new Tile[HEIGHT][WIDTH];
                    initialize_tetris();
                    game_status = true;

                }
                game_statistics();
            }
            Thread.sleep(60);
            double tetrominoes_speed = 0.001;
            game_object_frame = game_object_frame > tetrominoes_speed * 1000 ? 0 : game_object_frame + 30;
            display.getTerminal().puts(InfoCmp.Capability.clear_screen);
        }

    }

    private void check_for_tetris() {
        int c = 0;
        int k = 0;
        for (int i = HEIGHT - 2; i >= 1; i--) {
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
                        var t = new Tile[WIDTH];
                        t[0] = new Tile(0, i, Color.WHITE);
                        t[WIDTH - 1] = new Tile(WIDTH - 1, i, Color.WHITE);
                        box[i] = t;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void clean_this_box(int start, int end) {
        int p = (WIDTH - 2) / 2 + 3, q = p + 1;
        while (p >= 1 && q <= WIDTH - 2) {
            for (int i = start; i <= end; i++) {
                box[i][q] = null;
                box[i][p] = null;
            }
            p--;
            q++;
            display.getTerminal().
                    puts(InfoCmp.Capability.clear_screen);
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
                if (key_pressed == Key.LEFT && condition)
                    t.x = t.x - 1;
                else if (key_pressed == Key.RIGHT && condition)
                    t.x = t.x + 1;
                else if (key_pressed != Key.LEFT && key_pressed != Key.RIGHT &&
                        key_pressed != Key.DOWN && key_pressed != Key.UP && !hold_key &&
                        (game_object_frame == 0 || key_pressed == Key.SPACE))
                    t.y = t.y + step;

            }
        } else if (_tetris_timer == 5) {
            for (var t : tetrominoes) {
                box[t.y][t.x] = t;
            }
            no_of_tetrominoes++;
            tetris_no = dummy_tetris_no;
            tetrominoes_data[tetris_no - 1] = tetrominoes_data[tetris_no - 1] + 1;
            tetrominoes = tetris.getTetrominoes((int) (Math.random() * (WIDTH-5) + 1), 1, tetris_no);
            dummy_tetris_no = (int) (Math.random() * 7 + 1);
            dummy_tetris = tetris.getTetrominoes(0, 8, dummy_tetris_no);
            if (key_pressed != Key.SPACE)
                play("sound/slow-hit.wav", -0.0f, false);
            _tetris_timer = 0;
        } else _tetris_timer++;
        keyBoardInput.setKeyBoardKey(Key.NONE);
    }

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

    private String message() {
        return "" + (" ".repeat(WIDTH*3/2 - "Tetris".length() / 2)) + "Tetris";
    }

    private void draw() {
        StringBuilder s = new StringBuilder();
        s.append("\n".repeat(5)).append(message()).append("\n");
        for (int i = 0; i < box.length; i++) {
            Tile[] tiles = box[i];
            for (int j = 0; j < tiles.length; j++) {
                Tile tile = tiles[j];
                if (tile != null)
                    s.append(tile.getTile());
                else {
                    if (i < tetrominoes[0].y - 4 ||
                            i > tetrominoes[0].y + 4 ||
                            !print_tetrominoes(i, j)) {
                        s.append("   ");
                    } else
                        s.append(tetrominoes[0].getTile());
                }
            }
            switch (i) {
                case 2 -> s.append(" Score : ").append(score);
                case 4 -> s.append(" Lines : ").append(lines);
                case 6 -> s.append(" Next  : ");
                case 8, 9 -> {
                    s.append("    ");
                    draw_dummy_tetrominoes(s, i);
                }
                case 12 -> s.append(" Hold key : ").append(hold_key ? "on" : "off");
            }
            s.append('\n');
        }
        System.out.println(s.toString().indent(display.getSize().getColumns()/2-(WIDTH*3+8)/2));
    }

    private void draw_dummy_tetrominoes(StringBuilder s, int i) {
        for (int j = 0; j <= 4; j++) {
            boolean flag = false;
            for (var k : dummy_tetris) {
                if (k.x == j && ((k.y == 8 && i == 8) || (k.y == 9 && i == 9))) {
                    s.append(k.getTile());
                    flag = true;
                    break;
                }
            }
            if (!flag)
                s.append("   ");
        }
    }
    private boolean print_tetrominoes(int i, int j) {
        for (var k : tetrominoes) {
            if (k.x == j && k.y == i) {
                return true;
            }
        }
        return false;
    }

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

    private void initialize_tetris() {
        for (int i = 0; i < HEIGHT; i++) {
            if ((i == 0 || i == HEIGHT - 1)) {
                for (int j = 0; j < WIDTH; j++) {
                    box[i][j] = new Tile(i, j, Color.WHITE);
                }
            } else {
                box[i][0] = new Tile(i, 0, Color.WHITE);
                box[i][WIDTH - 1] = new Tile(i, WIDTH - 1, Color.WHITE);
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
        System.out.println("Enter the r key  to restart.....");
    }

    private String isFilled(int i) {
        var a = box[i];
        int l = 0, m = 0;
        for (int p = 1; p <= WIDTH-2; p++) {
            if (a[p] == null)
                l++;
            else
                m++;
        }
        if (l == WIDTH-2)
            return "Empty";
        if (m == WIDTH-2)
            return "Filled";
        return "Half_Filled";
    }

    static void play(String name, float volume, boolean isRepeatable) {
        var url = GameLauncher.class.getResource(name);
        Clip audioClip;
        try (var audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(url))) {
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
}
