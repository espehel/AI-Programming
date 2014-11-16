package game2048;

import sun.text.resources.cldr.ia.FormatData_ia;
import tools.Printer;

import java.util.Map;

/**
 * Created by espen on 10/11/14.
 */
public class Game2048Expectimax {
    public int counter = 0;
    private int depth_limit;
    private final GameManager gameManager;
    private final GridWeights gridWeights;
    private final GridManager gridManager;

    public Game2048Expectimax(int depthLimit, GameManager gameManager) {
        depth_limit = depthLimit;
        this.gameManager = gameManager;
        this.gridWeights = new GridWeights();
        this.gridManager = new GridManager();
    }

    public Game2048Expectimax() {
        gameManager = null;
        this.gridWeights = new GridWeights();
        this.gridManager = new GridManager();
        depth_limit = 0;

    }

    public Direction getNextMove() {
        //counter++;
        double bestScore = Double.MIN_VALUE;
        Direction bestDirection = Direction.DOWN;
        int[][] grid = gridManager.toIntGrid(gameManager.getGameGrid());
        /*if(gridManager.getEmptyTiles() < 8)
            depth_limit = 8;
        else
            depth_limit = 6;
        */
        for (Direction direction: Direction.values()){
            /*Thread thread = new Thread();
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            int[][] newGrid = gridManager.copy(grid);
            gridManager.shift(newGrid, direction);

            if(gridManager.equals(newGrid,grid))
                continue;

            double score = getAverageScore(newGrid, 1);

            if(score >= bestScore){
                bestScore = score;
                bestDirection = direction;
            }
        }
        //System.out.print("found move " + counter + ":");
        return bestDirection;
    }

    public double getBestMove(int[][] grid, int depth){
        /*BestMove bestMove = new BestMove();
        bestMove.setScore(Integer.MIN_VALUE);*/
        double bestScore = 0;//TODO: maybe this should be 0 instead of min value

        if(depth >= depth_limit){
            //if there is an available move the heuristic will be calculated for bestMove, else bestMove will be returned with minimum score.
            if(gridManager.availableMove(grid))
                return getHeuristicScore(grid);
            return bestScore;
        }

        //for each possible direction
        for (Direction direction: Direction.values()){
            //does an move that produces a new grid, but does not change the GameManager object in any way.
            //Map<Location,Tile> newGrid = gameManager.abstractMove(direction, grid);
            int[][] newGrid = gridManager.copy(grid);
            gridManager.shift(newGrid, direction);
            //if the newGrid is equal to the original grid, now movement has been done, and this can be pruned
            if(gridManager.equals(newGrid,grid))
                continue;
            double score = getAverageScore(newGrid, depth+1);

            if(score >= bestScore){
                bestScore = score;
            }
        }
        return bestScore;
    }

    private double getAverageScore(int[][] grid, int depth) {
        double totalScore = 0;
        double totalOutcomeProbability = 0;

        //for each possible outcome
        for(int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (grid[y][x] == 0) {
                    //for 2
                    int[][] newGrid2 = gridManager.copy(grid);
                    newGrid2[y][x] = 2;
                    double score2 = getBestMove(newGrid2,depth + 1);
                    //adds the score for this outcome
                    totalScore += score2 * 0.9;
                    //adds the probability for this outcome
                    totalOutcomeProbability += 0.9;//TODO possibly count for how many tiles there is available

                    //for 4
                    int[][] newGrid4 = gridManager.copy(grid);
                    newGrid4[y][x] = 4;
                    double score4 = getBestMove(newGrid4,depth + 1);
                    //adds the score for this outcome
                    totalScore += score4 * 0.1;
                    //adds the probability for this outcome
                    totalOutcomeProbability += 0.1;
                }
            }
        }
        //returns the avarage score of all outcomes from the specified grid
        return totalScore / totalOutcomeProbability;
    }

    public int getHeuristicScore(int[][] grid) {
        int score = 0;
        int bestScore = Integer.MIN_VALUE;
        for(int[][] gridWeight: gridWeights.values()) {
            for (int x = 0; x < 4; x++) {
                //System.out.print("[");
                for (int y = 0; y < 4; y++) {
                    score += grid[x][y] * gridWeight[x][y];
                    //System.out.print(grid[x][y] * gridWeight[x][y]+",");
                }
                //System.out.println("]");
            }
            if (score > bestScore) {
                bestScore = score;
                score = 0;
            }
        }
        //counter++;
        return bestScore;
    }


    public static void main(String[] args) {
//        getGridWeights();
        int[][] a = new int[][]{
                {8,0,4,8},
                {0,2,2,0},
                {0,0,8,0},
                {8,2,0,8}
        };
        Game2048Expectimax game = new Game2048Expectimax();
        Printer.print(a);
        System.out.println();
        Printer.print(GridWeights.TR);
        System.out.println();
        game.getHeuristicScore(a);
    }
}
