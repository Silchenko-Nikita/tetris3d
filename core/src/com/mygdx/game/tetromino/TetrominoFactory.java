package com.mygdx.game.tetromino;

import com.badlogic.gdx.graphics.Color;

public class TetrominoFactory {
    public TetrominoFactory() {

    }

    public Tetromino createTetromino(TetrominoType type) {
        Tetromino tetromino = new Tetromino();

        switch(type) {
            case CUBE:
                tetromino.color = Color.YELLOW;

                tetromino.grid[1][1][1] = true;
                tetromino.grid[1][1][2] = true;
                tetromino.grid[1][2][1] = true;
                tetromino.grid[1][2][2] = true;
                tetromino.grid[2][1][1] = true;
                tetromino.grid[2][1][2] = true;
                tetromino.grid[2][2][1] = true;
                tetromino.grid[2][2][2] = true;
                break;
            case T:
                tetromino.color = Color.PURPLE;

                tetromino.grid[0][2][1] = true;
                tetromino.grid[1][2][1] = true;
                tetromino.grid[2][2][1] = true;
                tetromino.grid[1][2][2] = true;
                break;
            case L_CURSED_1:
                tetromino.color = Color.GREEN;

                tetromino.grid[0][2][1] = true;
                tetromino.grid[1][2][1] = true;
                tetromino.grid[2][2][1] = true;
                tetromino.grid[2][1][1] = true;
                tetromino.grid[2][1][2] = true;
                break;
            case L_CURSED_2:
                tetromino.color = Color.BLUE;

                tetromino.grid[0][2][1] = true;
                tetromino.grid[1][2][1] = true;
                tetromino.grid[2][2][1] = true;
                tetromino.grid[2][2][2] = true;
                tetromino.grid[2][1][2] = true;
                break;
        }
        return tetromino;
    }

    public Tetromino createRandomTetromino() {
        return createTetromino(TetrominoType.randomTetrominoType());
    }
}