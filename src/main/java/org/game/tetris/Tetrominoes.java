package org.game.tetris;



public class Tetrominoes {
    //int xT,yT;
    private final Tile[][] tetrominoes=new Tile[2][4];
   /*
    Orientation orientation=Orientation.HORIZONTAL;

    public Tile[][] _getTetrominoes(int x,int y,int ch){
             xT=x;
             yT=y;
             switch (ch){

            ▓▓▓▓▓▓▓▓▓▓

                 case 1 -> {
                     tetrominoes[0][0] = new Tile(x, y, Color.BLUE);
                     tetrominoes[0][1] = new Tile(++x, y, Color.BLUE);
                     tetrominoes[0][2] = new Tile(++x, y, Color.BLUE);
                     tetrominoes[0][3] = new Tile(++x, y, Color.BLUE);
                 }

            ▓
            ▓▓▓▓▓▓▓▓▓▓

                 case 2 -> {
                     tetrominoes[0][0] = new Tile(x, y, Color.RED);
                     tetrominoes[1][0] = new Tile(x, y+1, Color.RED);
                     tetrominoes[1][1] = new Tile(++x, y+1, Color.RED);
                     tetrominoes[1][2] = new Tile(++x, y+1, Color.RED);
                 }

                     ▓
            ▓▓▓▓▓▓▓▓▓▓

                 case 3 -> {
                     tetrominoes[0][3] = new Tile(x + 3, y, Color.CYAN);
                     tetrominoes[1][3] = new Tile(x + 3, y+1, Color.CYAN);
                     tetrominoes[1][2] = new Tile(x + 2, y+1, Color.CYAN);
                     tetrominoes[1][1] = new Tile(x + 1, y+1, Color.CYAN);
                 }

            ▓▓▓▓▓▓
            ▓▓▓▓▓▓

                 case 4 -> {
                     tetrominoes[0][0] = new Tile(x, y, Color.PURPLE);
                     tetrominoes[0][1] = new Tile(x + 1, y, Color.PURPLE);
                     tetrominoes[1][0] = new Tile(x, y+1, Color.PURPLE);
                     tetrominoes[1][1] = new Tile(x + 1, y+1, Color.PURPLE);
                 }

              ▓▓▓▓
            ▓▓▓▓

                 case 5 -> {
                     x=x+1;
                     tetrominoes[0][1] = new Tile(x + 1, y, Color.YELLOW);
                     tetrominoes[0][2] = new Tile(x , y, Color.YELLOW);
                     tetrominoes[1][0] = new Tile(x, y+1, Color.YELLOW);
                     tetrominoes[1][1] = new Tile(x - 1, y+1, Color.YELLOW);
                 }

               ▓▓
            ▓▓▓▓▓▓▓▓

                 case 6 -> {
                     tetrominoes[0][1] = new Tile(x + 1, y, Color.GREEN);
                     tetrominoes[1][0] = new Tile(x, y+1, Color.GREEN);
                     tetrominoes[1][1] = new Tile(x + 1, y+1, Color.GREEN);
                     tetrominoes[1][2] = new Tile(x + 2, y+1, Color.GREEN);
                 }
            /*
              ▓▓▓▓
                ▓▓▓▓

                 case 7 -> {
                     tetrominoes[0][0] = new Tile(x, y, Color.CYAN_BRIGHT);
                     tetrominoes[0][1] = new Tile(x + 1, y, Color.CYAN_BRIGHT);
                     tetrominoes[1][1] = new Tile(x + 1, y+1, Color.CYAN_BRIGHT);
                     tetrominoes[1][2] = new Tile(x + 2, y+1, Color.CYAN_BRIGHT);
                 }
             }
        return tetrominoes;
             }*/
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