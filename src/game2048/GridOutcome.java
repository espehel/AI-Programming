package game2048;

import java.util.Map;

/**
 * Created by espen on 11/11/14.
 */
public class GridOutcome {
    private Map<Location,Tile> grid;
    private double probability;

    public Map<Location, Tile> getGrid() {
        return grid;
    }

    public void setGrid(Map<Location, Tile> grid) {
        this.grid = grid;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
