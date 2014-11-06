package flowpuzzle;

import navigation.NavigationNode;
import astar.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by espen on 12/10/14.
 */
public class FPNode extends Node {

    public Flow[] flows;
    public Cell[][] grid;
    public Cell assumedCell;

    public FPNode(Cell[][] grid, Flow[] flows) {
        this.flows = flows;
        this.grid = grid;
    }

    @Override
    protected boolean isSameState(Node other) {
        return this.id.equals(other.id);
        /*FPNode o = (FPNode)other;
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length; j++) {
                Cell thisCell = this.grid[i][j];
                Cell otherCell = o.grid[i][j];
                if(!(thisCell.color.equals(otherCell.color)))
                    return false;
                if(thisCell.hasInput() == otherCell.hasInput())
                    return false;
            }
        }
        return true;*/
    }

    @Override
    public void generateId() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length; j++) {
            builder.append(grid[i][j].getFlowId());
            }
        }
        for (Flow flow: flows){
            if(flow.isConnected())
                builder.append(flow.id+"["+1+"]");
            else
                builder.append(flow.id+"["+0+"]");
        }
        id=builder.toString();
    }

    public List<Cell> getNeighbours(Cell cell) {
        List<Cell> neighbours = new ArrayList<Cell>();
        if(cell.x > 0)
            neighbours.add(grid[cell.x-1][cell.y]);
        if(cell.x < grid.length-1)
            neighbours.add(grid[cell.x+1][cell.y]);
        if(cell.y > 0)
            neighbours.add(grid[cell.x][cell.y-1]);
        if(cell.y < grid.length-1)
            neighbours.add(grid[cell.x][cell.y+1]);
        return neighbours;
    }

    public Cell getHead(Flow flow) {
        return getCell(flow.head);
    }
    public Cell getCell(int[] cords){
        return grid[cords[0]][cords[1]];
    }

    public FPNode getDeepCopy() {

        Flow[] newFlows = new Flow[flows.length];
        for (int i = 0; i <flows.length; i++) {
            newFlows[flows[i].id] = flows[i].getDeepCopy();
        }

        Cell[][] newGrid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Cell newCell = new Cell(i,j);
                newCell.color = new String(grid[i][j].color);
                newCell.flow =  (grid[i][j].hasFlow()) ? newFlows[grid[i][j].flow.id] : null;
                newCell.domain = new ArrayList<Cell>();
                newGrid[i][j] = newCell;
            }
        }
        //System.out.println("created new grid");
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[0].length; j++) {
                Cell newCell = newGrid[i][j];

                if(grid[i][j].input != null){
                    newCell.input = newGrid[grid[i][j].input.x][grid[i][j].input.y];
                }
                //add each cell in old cells domain to the domain of the new cell and refering to the new grid
                for (Cell cell: grid[i][j].domain){
                    newCell.domain.add(newGrid[cell.x][cell.y]);
                }
            }
        }
        //System.out.println("copied over pointers to work in new grid");
        FPNode newNode = new FPNode(newGrid,newFlows);
        return newNode;
    }
    @Override
    public String toString(){
        return assumedCell.toString();
    }

    public NavigationNode toNavigationNode() {
        char[][] navGrid = new char[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Cell cell = grid[i][j];
                if(cell.hasFlow())
                    navGrid[i][j] = 'x';
                else
                    navGrid[i][j] = ' ';
            }
        }

        return new NavigationNode(navGrid);
    }
}
