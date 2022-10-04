package org.game.tetris;

public interface Effects {
   void moveRight();
    void moveLeft();
    void moveUp();
    void moveDown(int steps);
    void rotateRight();
    void rotateLeft();
}
