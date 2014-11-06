package nonogram;

import astar.AStar;
import astar.Node;
import tools.NonogramLineComparator;

import java.util.*;

/**
 * Created by espen on 27/10/14.
 */
public class NonogramAStar extends AStar {
    @Override
    protected int calculateH(Node x) {

        NonoNode node = (NonoNode)x;
        int remainingSize = 0;

        for (Line line: node.rows){
            remainingSize += line.domain.size()-1;
        }
        return remainingSize;
    }

    @Override
    public ArrayList<Node> generateAllSuccessors(Node node) {
        NonoNode p = (NonoNode) node;

        List<Line> sortedLines = getLinesSorted(p);
        Line bestLine = getBestLine(sortedLines);
        ArrayList<Node> successors = generateNodeFromDomain(bestLine,p);

        return successors;
    }

    private ArrayList<Node> generateNodeFromDomain(Line line, NonoNode p) {
        ArrayList<Node> neigbours = new ArrayList<Node>();
        if(line == null){
            return neigbours;
        }
        for (boolean[] assumedElement: line.domain){
            NonoNode newNode = p.getDeepCopy();

            Line newLine = newNode.rows.get(line.pos);

            newLine.domain = new ArrayList<boolean[]>();
            newLine.domain.add(assumedElement.clone());

            newNode.assumedRow = newLine;
            neigbours.add(newNode);
        }

        return neigbours;
    }

    private Line getBestLine(List<Line> sortedCells) {
        if(sortedCells.isEmpty())
            return null;
        else
            return sortedCells.get(0);
    }



    private List<Line> getLinesSorted(NonoNode node) {
        List<Line> sortedLines = new ArrayList<Line>();

        for (Line line: node.rows){
            if(!line.isSingleton()) {
                sortedLines.add(line);
            }
        }

        Collections.sort(sortedLines, new NonogramLineComparator());
        return sortedLines;
    }

    @Override
    protected boolean isSolution(Node node) {
        return false;
    }

    @Override
    protected int arcCost(Node parent, Node Child) {
        return 1;
    }

    @Override
    protected void updateUI(Node node) {

    }
}
