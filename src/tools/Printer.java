package tools;

import java.util.ArrayList;

/**
 * Created by espen on 28/10/14.
 */
public class Printer {

    public static void print(ArrayList<boolean[]> list) {
        for (boolean[] elements: list) {
            System.out.print("[");
            for (boolean element : elements) {
                System.out.print(element + ",");
            }
            System.out.println("]");
        }
    }
    public static void print(String[] list){
        System.out.print("[");
        for (String element : list) {
            System.out.print(element + ",");
        }
        System.out.println("]");
    }

    public static void print(boolean[] list) {
        System.out.print("[");
        for (Boolean element : list) {
            System.out.print(element + ",");
        }
        System.out.println("]");
    }
    public static void print(int[][] grid) {
        for (int[] elements: grid) {
            System.out.print("[");
            for (int element : elements) {
                if(element<0)
                    System.out.print(element + ",");
                else
                    System.out.print(" " + element + ",");
            }
            System.out.println("]");
        }
    }
    public static void print2(int[][] grid){
        for (int x = 0; x < 4; x++) {
            System.out.print("[");
            for (int y = 0; y < 4; y++) {
                if(grid[y][x]<0)
                    System.out.print(grid[y][x] + ",");
                else
                    System.out.print(" " + grid[y][x] + ",");
            }
            System.out.println("]");
        }
    }
}
