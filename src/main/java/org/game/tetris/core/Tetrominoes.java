package org.game.tetris.core;



public class Tetrominoes {
    public Tile[] getTetrominoes(int x, int y,int ch) {
        var tiles = new Tile[4];
        switch (ch) {
            /*
            ▓▓▓▓▓▓▓▓▓▓
             */
            case 1 -> {
                tiles[0] = new Tile(x, y, Color.BLUE);
                tiles[1] = new Tile(++x, y, Color.BLUE);
                tiles[2] = new Tile(++x, y, Color.BLUE);
                tiles[3] = new Tile(++x, y, Color.BLUE);
            }
            /*
            ▓
            ▓▓▓▓▓▓▓▓▓▓
             */
            case 2 -> {
                tiles[0] = new Tile(x, y, Color.RED);
                tiles[1] = new Tile(x, y+1, Color.RED);
                tiles[2] = new Tile(++x, y+1, Color.RED);
                tiles[3] = new Tile(++x, y+1, Color.RED);
            }
             /*
                     ▓
            ▓▓▓▓▓▓▓▓▓▓
             */
            case 3 -> {
                tiles[0] = new Tile(x + 3, y, Color.CYAN);
                tiles[1] = new Tile(x + 3, y+1, Color.CYAN);
                tiles[2] = new Tile(x + 2, y+1, Color.CYAN);
                tiles[3] = new Tile(x + 1, y+1, Color.CYAN);
            }
             /*
            ▓▓▓▓▓▓
            ▓▓▓▓▓▓
             */
            case 4 -> {
                tiles[0] = new Tile(x, y, Color.PURPLE);
                tiles[1] = new Tile(x + 1, y, Color.PURPLE);
                tiles[2] = new Tile(x, y+1, Color.PURPLE);
                tiles[3] = new Tile(x + 1, y+1, Color.PURPLE);
            }
             /*
              ▓▓▓▓
            ▓▓▓▓
             */
            case 5 -> {
                x=x+1;
                tiles[0] = new Tile(x + 1, y, Color.YELLOW);
                tiles[1] = new Tile(x , y, Color.YELLOW);
                tiles[2] = new Tile(x, y+1, Color.YELLOW);
                tiles[3] = new Tile(x - 1, y+1, Color.YELLOW);
            }
            /*
               ▓▓
            ▓▓▓▓▓▓▓▓
             */
            case 6 -> {
                tiles[0] = new Tile(x + 1, y, Color.GREEN);
                tiles[1] = new Tile(x, y+1, Color.GREEN);
                tiles[2] = new Tile(x + 1, y+1, Color.GREEN);
                tiles[3] = new Tile(x + 2, y+1, Color.GREEN);
            }
            /*
              ▓▓▓▓
                ▓▓▓▓
             */
            case 7 -> {
                tiles[0] = new Tile(x, y, Color.CYAN_BRIGHT);
                tiles[1] = new Tile(x + 1, y, Color.CYAN_BRIGHT);
                tiles[2] = new Tile(x + 1, y+1, Color.CYAN_BRIGHT);
                tiles[3] = new Tile(x + 2, y+1, Color.CYAN_BRIGHT);
            }
        }
        return tiles;
    }
}

enum Color {
    RED("\033[0;31m"),   // RED
    GREEN("\033[0;32m"),   // GREEN
    YELLOW("\033[0;33m"),  // YELLOW
    BLUE("\033[0;34m"),    // BLUE
    PURPLE("\033[0;35m"),  // PURPLE
    CYAN("\033[0;36m"),  // CYAN
    WHITE("\033[0;97m"),  // WHITE
    CYAN_BRIGHT("\033[0;96m");
    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

}