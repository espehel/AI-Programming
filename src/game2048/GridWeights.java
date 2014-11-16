package game2048;

import tools.Printer;

import javax.print.event.PrintJobAttributeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by espen on 12/11/14.
 */
public class GridWeights {

    private int[][] topRight;
    private int[][] topLeft;
    private int[][] bottomRight;
    private int[][] bottomLeft;
    public static final int[][] TR = new int[][]{
            { 0, 1, 2, 3},
            {-1, 0, 1, 2},
            {-2,-1, 0, 1},
            {-3,-2,-1, 0}
    };

    public GridWeights() {
        generateGridWeights();
    }

    private void generateGridWeights() {

        topRight = new int[GridOperator.DEFAULT_GRID_SIZE][GridOperator.DEFAULT_GRID_SIZE];
        for (int i = 0; i < topRight.length; i++) {
            int offset = i*-1;
            for (int j = 0; j < topRight[0].length; j++) {
                topRight[i][j] = j+offset;
            }
        }

        bottomRight = new int[GridOperator.DEFAULT_GRID_SIZE][GridOperator.DEFAULT_GRID_SIZE];
        for (int i = 0; i < bottomRight.length; i++) {
            int offset = i+-3;
            for (int j = 0; j < bottomRight[0].length; j++) {
                bottomRight[i][j] = j+offset;
            }
        }


        topLeft = new int[GridOperator.DEFAULT_GRID_SIZE][GridOperator.DEFAULT_GRID_SIZE];
        for (int i = 0; i < topLeft.length; i++) {
            int offset = 3-i;
            for (int j = 0; j < topLeft[0].length; j++) {
                topLeft[i][j] = offset-j;
            }
        }

        bottomLeft = new int[GridOperator.DEFAULT_GRID_SIZE][GridOperator.DEFAULT_GRID_SIZE];
        for (int i = 0; i < bottomLeft.length; i++) {
            int offset = i;
            for (int j = 0; j < bottomLeft[0].length; j++) {
                bottomLeft[i][j] = offset-j;
            }
        }
    }


    public int[][] getTopRight() {
        return topRight;
    }

    public int[][] getTopLeft() {
        return topLeft;
    }

    public int[][] getBottomRight() {
        return bottomRight;
    }

    public int[][] getBottomLeft() {
        return bottomLeft;
    }

    public int[][][] values(){
        return new int[][][]{
                //topRight,
                //topLeft,
                bottomRight,
                //bottomLeft
        };
    }

    public static void main(String[] args) {
        GridWeights gridWeights = new GridWeights();
        Printer.print(TR);
        System.out.println();
        Printer.print2(TR);

    }
 }
