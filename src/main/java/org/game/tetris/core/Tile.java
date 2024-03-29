package org.game.tetris.core;

public class Tile {
    public int x, y;
    public final Color color;
    public Tile(Color color){
        this.color=color;
    }
    public Tile(int x, int y,Color color) {
        this.x = x;
        this.y = y;
        this.color=color;
    }
    public String getTile(){
        return color.getColor()+"███"+"\33[0m";
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + color +
                '}';
    }
}