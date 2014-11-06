package navigation;

import astar.AStar;
import astar.Node;

import java.util.ArrayList;


/**
 * Created by espen on 26/08/14.
 */
public class NavigationAStar extends AStar {

    Main main;

    public NavigationAStar(Main main) {
        this.main = main;
    }

    @Override
    protected int calculateH(Node node) {
        //finds the head of the path
        int[] head = findCell(((NavigationNode)node).grid,'h');
        //if(head == null)
            //System.out.println(node);

        //finds the goal
        int[] goal = findCell(((NavigationNode)node).grid,'g');

        //this happens when a solution is found
        if(head == null && goal == null)
            return 0;

        //returns the manhatten distance
        return Math.abs(head[0]-goal[0]) + Math.abs(head[1]-goal[1]);
    }

    @Override
    public ArrayList<Node> generateAllSuccessors(Node node)
    {
        ArrayList<Node> successors = new ArrayList<Node>();
        //up
        successors.add(getOneChild(((NavigationNode) node).grid, new int[]{-1, 0}));

        //right
        successors.add(getOneChild(((NavigationNode)node).grid,new int[]{0,1}));

        //left
        successors.add(getOneChild(((NavigationNode)node).grid,new int[]{0,-1}));

        //down
        successors.add(getOneChild(((NavigationNode)node).grid,new int[]{1,0}));

        return successors;

    }

    private Node getOneChild(char[][] grid, int[] direction) {
        char[][] newGrid = new char[grid.length][grid[0].length];
        int[] oldHead = new int[2];
        for (int i = 0; i <grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                newGrid[i][j] = grid[i][j];
                if(grid[i][j] == 'h'){
                    oldHead[0] = i;
                    oldHead[1] = j;
                    newGrid[i][j] = 'b';
                }

            }
        }

        //sets the coordinates for the new head of the path
        int x = direction[0]+oldHead[0], y = direction[1]+oldHead[1];

        //checks if the new head is valid, if not null is returned
        if((x >= grid.length || x < 0) || (y >= grid[0].length || y < 0))
            return null;
        if(newGrid[x][y] == ' ')
            newGrid[x][y] = 'h';
        else if (newGrid[x][y] == 'g') //if the new cell is the same as the goal coordinate the char is substituted with an 'f'
            newGrid[x][y] = 'f';
        else return null;

        return new NavigationNode(newGrid);
    }

    @Override
    protected boolean isSolution(Node node) {
        //if there is a cell marked with an 'f' the path has reached the goal.
        return findCell(((NavigationNode)node).grid,'f') != null;

    }

    @Override
    protected void updateUI(Node node) {
        if(main != null) {
            NavigationNode x = (NavigationNode) node;
            clearBody(x.grid);
            if (x.parent != null)
                updateGrid((NavigationNode) x.parent, x.grid);

            main.updateUI(x.grid);
        }
    }

    private void clearBody(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == 'b')
                    grid[i][j] = ' ';
            }
        }
    }

    private void updateGrid(NavigationNode parent, char[][] grid){
        int[] head = findCell(parent.grid,'h');
        grid[head[0]][head[1]] = 'b';
        if(parent.parent == null)
            return;
        else
            updateGrid((NavigationNode)parent.parent, grid);
    }

    @Override
    protected int arcCost(Node parent, Node Child)
    {
        return 1;
    }

    private int[] findCell(char[][] grid,char c){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == c)
                    return new int[]{i,j};
            }
        }
        return null;
    }
}
