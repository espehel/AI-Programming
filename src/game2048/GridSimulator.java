package game2048;


import tools.Printer;

import java.util.Map;

/**
 * Created by espen on 14/11/14.
 */
public class GridSimulator {
    private int _emptyTiles;

    public void shift(int[][] grid, Direction direction){

        //TODO: return true if nothing changed
        switch (direction){
            case UP:
                shiftUp(grid);
                break;
            case DOWN:
                shiftDown(grid);
                break;
            case LEFT:
                shiftLeft(grid);
                break;
            case RIGHT:
                shiftRight(grid);
                break;
        }
    }

    private void shiftRight(int[][] grid) {
        //boolean moved = false;
        for(int row = 0; row <4;row++){
            int[] line = new int[4];
            for (int x = 0; x < 4;x++ ){
                line[x] = grid[row][3 - x];
            }
            move(line);
            for (int x = 0; x < 4;x++ ){
                grid[row][3 - x] = line[x];
            }
        }
    }

    private void shiftLeft(int[][] grid) {
        //boolean moved = false;
        for(int row = 0; row <4;row++){
            int[] line = new int[4];
            for (int x = 0; x < 4;x++ ){
                line[x] = grid[row][x];
            }
            move(line);
            for (int x = 0; x < 4;x++ ){
                grid[row][x] = line[x];
            }
        }
    }

    private void shiftDown(int[][] grid) {
        //boolean moved = false;

        for(int column = 0; column <4;column++){
            int[] line = new int[4];
            for (int y = 0; y < 4;y++ ){
                line[y] = grid[3 - y][column];
            }
            move(line);
            for (int y = 0; y < 4;y++ ){
                grid[3-y][column] = line[y];
            }
        }
    }

    private void shiftUp(int[][] grid) {
        //boolean moved = false;

        for(int column = 0; column <4;column++){
            int[] line = new int[4];
            for (int y = 0; y < 4;y++ ){
                line[y] = grid[y][column];
            }
            move(line);
            for (int y = 0; y < 4;y++ ){
                grid[y][column] = line[y];
            }
        }
    }

    private void move(int[] line) {
        //boolean moved = false;
        int target = 0;

        for(int i = 1; i < 4; i++){
            int targetValue = line[target];
            int focalValue = line[i];

            if(focalValue != 0){

                if(targetValue == 0){
                    line[target] = focalValue;
                    line[i] = 0;
                }
                else{
                    if(targetValue == focalValue){
                        line[i] = 0;
                        line[target] <<= 1;
                    }
                    else{
                        line[i] = 0;
                        line[target+1] = focalValue;
                    }
                    target++;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] a = new int[][]{{2,0,4,0},{0,2,2,0},{0,0,8,0},{0,2,0,0}};
        Printer.print(a);
        System.out.println();
        GridSimulator mng = new GridSimulator();

        mng.shift(a, Direction.LEFT);
        Printer.print(a);
        System.out.println();

    }

    public int[][] toIntGrid(Map<Location, Tile> gameGrid) {
        int[][] intGrid = new int[4][4];
        int emptyTiles = 0;
        for (Tile tile: gameGrid.values()){
            if(tile == null) {
                emptyTiles++;
                continue;
            }
            intGrid[tile.getLocation().getY()][tile.getLocation().getX()] = tile.getValue();
        }
        _emptyTiles=emptyTiles;
        return intGrid;
    }

    public boolean availableMove(int[][] grid) {
        //checks if there is two similar cells next to each other or one empty
        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                if (grid[y][x] == 0)
                    return true;
                if (x < 3 && grid[y][x] == grid[y][x + 1])
                    return true;
                if (y < 3 && grid[y][x] == grid[y + 1][x])
                    return true;
            }
        }
        return false;
    }

    public int[][] copy(int[][] grid) {
        int[][] newGrid = new int[4][4];
        for (int i = 0; i < 4; i++) {
            newGrid[i] = grid[i].clone();
        }
        return newGrid;
    }
    public boolean equals(int[][] a, int[][] b){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }

    public int getEmptyTiles() {
        return _emptyTiles;
    }
}
