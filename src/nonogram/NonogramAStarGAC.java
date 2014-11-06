package nonogram;

import astar.AStar;
import astar.Node;
import csp.gac.AStarGAC;

import java.util.ArrayList;

/**
 * Created by espen on 27/10/14.
 */
public class NonogramAStarGAC extends AStarGAC{

    Main main;
    NonoNode node;

    public NonogramAStarGAC(Main main) {
        super(new NonogramAStar());
        this.main = main;
    }

    @Override
    protected void initialize(Node x) {
        NonoNode node = (NonoNode)x;
        for(Line row: node.rows){
            addNeighboursForRevising(row,node);
        }

    }

    @Override
    public Node generateInitialNode() {
        return node;
    }

    @Override
    protected void domainFilteringLoop(Node x) {
        while(!queue.isEmpty()){

            //fetces next revisee to be revised
            NonoRevise revisee = (NonoRevise)queue.poll();

            boolean reduced = generalRevise(revisee);


            //if domain is reduced, all columns are added back, and will check towards the reduced row
            if(reduced){
                if(isContradictary(x))
                    throw new IllegalStateException("Empty domain");
                else
                    addNeighboursForRevising(revisee.row, (NonoNode) x);
            }

        }
    }

    private boolean generalRevise(NonoRevise revisee) {

        Cell shared = new Cell(revisee.column.pos,revisee.row.pos);

        if(alwaysFilled(revisee.column.domain,shared)){
            return reduceDomain(revisee.row.domain,true,shared);
        }
        if(alwaysEmpty(revisee.column.domain,shared)){
            return reduceDomain(revisee.row.domain,false,shared);
        }
        return false;
    }
    public boolean alwaysFilled(ArrayList<boolean[]> columnDomain, Cell shared){
        for (boolean[] element: columnDomain){
            if(!element[shared.y])
                return false;
        }
        return true;
    }
    public boolean alwaysEmpty(ArrayList<boolean[]> columnDomain, Cell shared){
        for (boolean[] element: columnDomain){
            if(element[shared.y])
                return false;
        }
        return true;
    }

    private boolean reduceDomain(ArrayList<boolean[]> domain, boolean cellStatus, Cell shared) {
        ArrayList<boolean[]> reduce = new ArrayList<boolean[]>();
        for (boolean[] elements: domain){
            if(elements[shared.x] != cellStatus){
                reduce.add(elements);
            }
        }
        return domain.removeAll(reduce);
    }

    @Override
    protected boolean isSolution(Node node) {
        return node.h == 0;//TODO this may be fixed
    }

    @Override
    protected boolean isContradictary(Node node) {
        NonoNode x = (NonoNode) node;

        for (Line line: x.columns){
            if(line.domain.isEmpty())
                return true;
        }
        for (Line line: x.rows){
            if(line.domain.isEmpty())
                return true;
        }
        return false;
    }

    @Override
    protected void GACRerun(Node node) {
        NonoNode x = (NonoNode) node;

        if(overfilledColumn(x))
            throw new IllegalStateException("overfilled column");

        if(invalidAssumption(x.columns, x.assumedRow))
            throw new IllegalStateException("invalid column");

        if(invalidColumn(x))
            throw new IllegalStateException("invalid column");

        addNeighboursForRevising(x.assumedRow,x);
        domainFilteringLoop(x);
    }

    private boolean invalidAssumption(ArrayList<Line> columns, Line assumedRow) {
        for (Line column: columns){
            if(column.hasDomain(assumedRow.pos,assumedRow.getLine()[column.pos]))
                return false;
        }
        return true;
    }

    private boolean invalidColumn(NonoNode x) {
        for (Line column: x.columns){
            //System.out.println("checking column: " + column);
            boolean[] rCol = new boolean[column.length];
            for (int i = 0; i < column.length; i++) {
                rCol[i] = x.rows.get(i).getLine()[column.pos];
            }
            if(!column.hasDomain(rCol)) {
                return true;
            }
        }
        return false;
    }

    private boolean overfilledColumn(NonoNode x) {
        int sum = 0;
        for (int i = 0; i < x.columns.size(); i++) {
            for (Line row: x.rows){
                if(row.isSingleton()){
                    if(row.getLine()[i])
                        sum++;
                }
            }
            if(sum>x.columnLimits[i])
                return true;
            else
                sum = 0;
        }
        return false;
    }

    private void addNeighboursForRevising(Line assumedRow, NonoNode x) {

        for (Line line: x.columns){
            queue.add(new NonoRevise(line,assumedRow,"!="));
        }

    }

    @Override
    protected void printStats(Node x, AStar search) {

    }

    @Override
    protected void updateUI(Node node) {
        main.updateUI((NonoNode) node);
    }

    public void setup(NonoNode node) {
        this.node = node;

    }

}
