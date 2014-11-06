package flowpuzzle;

import astar.AStar;
import astar.Node;
import org.omg.CORBA.Current;
import tools.Calculate;
import tools.FlowPuzzleCellComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by espen on 12/10/14.
 */
public class FlowPuzzleAStar extends AStar {
    @Override
    protected int calculateH(Node node) {
        FPNode x = (FPNode) node;
        int totalDistance = 0;
        for (int i = 0; i <x.flows.length; i++) {
            totalDistance += Calculate.manhattenDistance(x.flows[i].head, x.flows[i].end);
        }
        return totalDistance;

    }

    @Override
    public ArrayList<Node> generateAllSuccessors(Node node) {

        FPNode p = (FPNode) node;

        List<Cell> sortedCells = getHeadsSorted(p);
        Cell bestHead = getBestCell(sortedCells);
        ArrayList<Node> successors = generateNodeFromDomain(bestHead,p);

        return successors;
    }

    private ArrayList<Node> generateNodeFromDomain(Cell cell, FPNode p) {
        ArrayList<Node> neigbours = new ArrayList<Node>();
        if(cell == null){
            return neigbours;
        }

        for(Cell assumee : cell.domain) {

            FPNode newNode = p.getDeepCopy();

            Cell newAssume = newNode.grid[assumee.x][assumee.y];
            Cell newSource = newNode.grid[cell.x][cell.y];
            //setts the assumees input to cell in the new grid
            newAssume.input = newSource;

            //sets newSource's domain to the assumee
            newSource.domain = new ArrayList<Cell>();
            newSource.domain.add(newAssume);

            //removes newSource from newAssumes domain
            newAssume.reduceDomain(newSource);

            //sets assumee's colors and flow to cells color and flow.
            newAssume.flow = newSource.flow;
            newAssume.color = newSource.color;


            newAssume.flow.head = new int[]{assumee.x,assumee.y};

            newNode.assumedCell = newAssume;

            neigbours.add(newNode);
        }
        return neigbours;
    }

    private Cell getBestCell(List<Cell> sortedCells) {
        for (Cell cell : sortedCells) {
            return cell;
        }
        return null;
    }

    private List<Cell> getHeadsSorted(FPNode node) {
        List<Cell> sortedCells = new ArrayList<Cell>();

        for (Flow flow: node.flows){
            Cell head = node.getHead(flow);

            head.propageteFlow();

            if(head.isEndPoint())
                continue;
            else{
                sortedCells.add(head);
            }
        }
        Collections.sort(sortedCells,new FlowPuzzleCellComparator());
        return sortedCells;
    }

    @Override
    protected boolean isSolution(Node node) {
        return false;
    }

    @Override
    protected int arcCost(Node parent, Node Child) {
        return 3;
    }

    @Override
    protected void updateUI(Node node) {

    }
}
