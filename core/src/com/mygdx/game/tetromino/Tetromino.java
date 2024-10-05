package com.mygdx.game.tetromino;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.Field;
import com.mygdx.game.utils.Coordinates;

import java.util.Arrays;
import java.util.Random;

enum TetrominoType {
    CUBE,
    T,
    L_CURSED_1,
    L_CURSED_2;

    private static final Random PRNG = new Random();

    public static TetrominoType randomTetrominoType()  {
        TetrominoType[] types = values();
        return types[PRNG.nextInt(types.length)];
    }
}

public class Tetromino {
    public static final int GRID_WIDTH = 4;
    public boolean[][][] grid = new boolean[GRID_WIDTH][GRID_WIDTH][GRID_WIDTH];
    public boolean[][] bufferedArr = new boolean[GRID_WIDTH][GRID_WIDTH];
    public boolean[][][] bufferedGrid = new boolean[GRID_WIDTH][GRID_WIDTH][GRID_WIDTH];
    public Color color;

    public boolean move(Field field, Coordinates initCoords, Coordinates offsetCoords) {
        initCoords.x += offsetCoords.x;
        initCoords.y += offsetCoords.y;
        initCoords.z += offsetCoords.z;

        if (!posIsValid(field, initCoords)) {
            initCoords.x -= offsetCoords.x;
            initCoords.y -= offsetCoords.y;
            initCoords.z -= offsetCoords.z;

            return false;
        }

        return true;
    }


    public boolean fall(Field field, Coordinates initCoords){
        return move(field, initCoords, new Coordinates(0, 0, -1));
    }

    boolean posIsValid(Field field, Coordinates coords) {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    Coordinates fieldCoords = new Coordinates();

                    fieldCoords.x = coords.x + i;
                    fieldCoords.y = coords.y + j;
                    fieldCoords.z = coords.z - GRID_WIDTH + k;

                    if (grid[i][j][k] && (!field.coordsAreValid(fieldCoords.x, fieldCoords.y, fieldCoords.z) ||
                            field.grid[fieldCoords.x][fieldCoords.y][fieldCoords.z] != Color.CLEAR)) {
                            return false;
                    }
                }
            }
        }

        return true;
    }

    private void clearBufferedArr() {
        for (boolean[] booleans : bufferedArr) {
            Arrays.fill(booleans, false);
        }
    }

    private void clearBufferedGrid() {
        for (boolean[][] booleans : bufferedGrid) {
            for (boolean[] aBoolean : booleans) {
                Arrays.fill(aBoolean, false);
            }
        }
    }

    void copyGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.arraycopy(bufferedGrid[i][j], 0, grid[i][j], 0, grid[i][j].length);
            }
        }
    }

    public boolean rotateX_CW(Field field, Coordinates coordinates) {
        clearBufferedArr();
        clearBufferedGrid();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    bufferedGrid[i][k][GRID_WIDTH - 1 - j] = grid[i][k][GRID_WIDTH - 1 - j];
                    bufferedArr[j][k] = grid[i][k][GRID_WIDTH - 1 - j];
                }
            }

            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    grid[i][j][k] = bufferedArr[j][k];
                }
            }
        }

        if (!posIsValid(field, coordinates)){
            copyGrid();
            return false;
        }

        return true;
    }

    public boolean rotateX_CCW(Field field, Coordinates coordinates) {
        clearBufferedArr();
        clearBufferedGrid();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    bufferedGrid[i][j][k] = grid[i][j][k];
                    bufferedArr[k][GRID_WIDTH - 1 - j] = grid[i][j][k];
                }
            }

            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    grid[i][j][k] = bufferedArr[j][k];
                }
            }
        }

        if (!posIsValid(field, coordinates)){
            copyGrid();
            return false;
        }

        return true;
    }

    public boolean rotateY_CW(Field field, Coordinates coordinates) {
        clearBufferedArr();
        clearBufferedGrid();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    bufferedGrid[GRID_WIDTH - 1 - j][i][k] = grid[GRID_WIDTH - 1 - j][i][k];
                    bufferedArr[j][k] = grid[GRID_WIDTH - 1 - j][i][k];
                }
            }

            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    grid[k][i][j] = bufferedArr[j][k];
                }
            }
        }

        if (!posIsValid(field, coordinates)){
            copyGrid();
            return false;
        }

        return true;
    }

    public boolean rotateY_CCW(Field field, Coordinates coordinates) {
        clearBufferedArr();
        clearBufferedGrid();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    bufferedGrid[k][i][j] = grid[k][i][j];
                    bufferedArr[k][GRID_WIDTH - 1 - j] = grid[k][i][j];
                }
            }

            for (int j = 0; j < GRID_WIDTH; j++) {
                for (int k = 0; k < GRID_WIDTH; k++) {
                    grid[k][i][j] = bufferedArr[j][k];
                }
            }
        }

        if (!posIsValid(field, coordinates)){
            copyGrid();
            return false;
        }

        return true;
    }
}


