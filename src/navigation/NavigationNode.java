package navigation;

import astar.Node;

/**
 * Created by espen on 26/08/14.
 */
public class NavigationNode extends Node {

    public char[][] grid;

    public NavigationNode(char[][] grid){
        this.grid = grid;
    }

    @Override
    protected boolean isSameState(Node other) {
        //if both heads is at the same postion in both grids, this equals the same state.
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == 'h' && ((NavigationNode)other).grid[i][j] == 'h')
                    return true;
            }
        }
        return false;
    }

    @Override
    public void generateId() {

    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                output.append("|" + grid[i][j]);
            }
            output.append("|\n");
        }
        return output.toString();
    }

}
