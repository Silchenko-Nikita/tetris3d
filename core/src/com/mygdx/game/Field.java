package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.tetromino.Tetromino;

public class Field {
    final int BLOCK_WIDTH = 1;

    final int WIDTH = 10;
    final int HEIGHT = 10;
    final int ALTITUDE = 20;

    public float scale = 2.5f;

    public Color grid[][][] = new Color[WIDTH][HEIGHT][ALTITUDE];


    public Field() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                for (int k = 0; k < ALTITUDE; k++) {
                    grid[i][j][k] = Color.CLEAR;
                }
            }
        }
    }

    public void addTetromino(Tetromino tetromino, int x, int y, int z) {
        for (int i = 0; i < Tetromino.GRID_WIDTH; i++) {
            for (int j = 0; j < Tetromino.GRID_WIDTH; j++) {
                for (int k = 0; k < Tetromino.GRID_WIDTH; k++) {
                    if (tetromino.grid[i][j][k]) {
                        grid[x + i][y + j][z + k - Tetromino.GRID_WIDTH] = tetromino.color;
                    }
                }
            }
        }
    }

    public boolean coordsAreValid(int x, int y, int z) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0 && z < ALTITUDE;
    }

    public void update() {
        boolean isFilled = true;

        for (int i = ALTITUDE - 1; i >= 0; i--) {
            a: for (int j = 0; j < WIDTH; j++) {
                for (int k = 0; k < HEIGHT; k++) {
                    if (grid[j][k][i] == Color.CLEAR) {
                        isFilled = false;
                        break a;
                    }
                }
            }


            if (isFilled) {
                for (int t = ALTITUDE - 1; t >= 0; t--) {
                    for (int j = 0; j < WIDTH; j++) {
                        for (int k = 0; k < HEIGHT; k++) {
                            grid[j][k][t] = grid[j][k][t + 1];
                        }
                    }

                    for (int j = 0; j < WIDTH; j++) {
                        for (int k = 0; k < HEIGHT; k++) {
                            grid[j][k][ALTITUDE - 1] = Color.CLEAR;
                        }
                    }
                }
            }
        }
    }
}
