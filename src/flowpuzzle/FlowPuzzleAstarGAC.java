package flowpuzzle;

import astar.AStar;
import navigation.NavigationAStar;
import navigation.NavigationNode;
import astar.Node;
import csp.gac.AStarGAC;


/**
 * Created by espen on 12/10/14.
 */
public class FlowPuzzleAstarGAC extends AStarGAC {
    public FPNode node;
    public Main main;

    public FlowPuzzleAstarGAC(Main main) {
        super(new FlowPuzzleAStar());
        this.main = main;
    }

    public void setup(FPNode node) {
        this.node = node;
    }

    @Override
    protected void initialize(Node node) {
        FPNode x = (FPNode) node;

        for (int i = 0; i < x.grid.length; i++) {
            for (int j = 0; j < x.grid[0].length; j++) {
                addNeighboursForRevising(x.grid[i][j],x);
            }
        }
    }

    private void addNeighboursForRevising(Cell changedCell, FPNode node){
        for (Cell x : node.getNeighbours(changedCell)){
            queue.add(new FPRevise(x,changedCell,"x != y"));
        }
    }

    @Override
    public Node generateInitialNode() {
        for (int i = 0; i < this.node.grid.length; i++) {
            for (int j = 0; j < this.node.grid[0].length; j++) {
                this.node.grid[i][j].domain = this.node.getNeighbours(node.grid[i][j]);
            }
        }
        return this.node;
    }

    @Override
    protected void domainFilteringLoop(Node x) throws IllegalStateException{

        while(!queue.isEmpty()){

            //fetces next revisee to be revised
            FPRevise revisee = (FPRevise)queue.poll();
            //revises the revisee, if domain is changed, it returns true
            //boolean reduced = revise(revisee);

            //boolean reduced = generalRevise(revisee);
            boolean reduced = revise(revisee);

            /*if(!revisee.Y.hasFlow() && ! revisee.Y.hasInput() && reduced) {

                for (Cell z : ((FPNode) x).getNeighbours(revisee.Y)) {
                    if (z != revisee.X)
                        queue.add(new FPRevise(z, revisee.Y, "x != y"));
                }
                reduced = false;
            }*/

            if(reduced)
                queue.add(new FPRevise(revisee.Y,revisee.X,"x != y"));

            if(revisee.Y.isSingleton() && reduced){
                Cell deducee = deduceFrom(revisee.Y);
                //if the deducee was not valid, e.g. it is a connected flow
                if(deducee == null){
                    continue;
                }
                /*updateUI(x);
                try {
                    Thread.sleep(Constants.DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                for (Cell deduceeNeighbour : ((FPNode)x).getNeighbours(deducee)) {
                    //does not add Y
                    if (deduceeNeighbour != revisee.Y) {
                        queue.add(new FPRevise(deduceeNeighbour, deducee, "x != y"));
                    }

                    for (Cell z : ((FPNode) x).getNeighbours(deduceeNeighbour)) {
                        if (z != deduceeNeighbour)
                            queue.add(new FPRevise(z, deduceeNeighbour, "x != y"));
                    }
                }
            }
        }
        //System.out.println("------------------------------------------------");
    }


    private Cell deduceFrom(Cell y) {
        Cell deducee = y.domain.get(0);

        if(y.flow.isConnected() || (deducee.hasFlow() && !deducee.sameFlow(y))){
            return null;
        }

        if(y.isEndPoint()){
            deducee.color = y.color;
            y.input = deducee;
            deducee.flow = y.flow;
            deducee.flow.end = new int[]{deducee.x,deducee.y};
            deducee.reduceDomain(y);
            //System.out.println("expanded endpoint to: " + deducee);
        }else {
            deducee.color = y.color;
            deducee.input = y;
            deducee.flow = y.flow;
            deducee.flow.head = new int[]{deducee.x, deducee.y};
            deducee.reduceDomain(y);
            //System.out.println("flowed to: " + deducee);
        }

        return deducee;
    }

    public boolean revise(FPRevise revisee){
        Cell y = revisee.Y;
        Cell x = revisee.X;
        boolean reduced = false;

        //y is an unexplored cell
        /*if(!y.hasInput() && !y.hasFlow()){
            if((x.hasFlow() && !x.flow.isConnected()) && (x.isHead() || x.isEndPoint() || !x.hasFlow()))
                ;//do nothing
            else {
                boolean b = y.reduceDomain(x);

                if(y.domain.size() == 0)
                    throw new IllegalStateException();
                return b;
            }
        }*/


        if(x.input == y || !y.hasFlow() || y.isSingleton() || y.flow.isConnected()){
            return false;
        }

        //System.out.println("------------------------------------------------");
        //System.out.println("revising: " + y + ", with flow: " + y.flow);

        if(x.hasInput()) {
            if(x.sameFlow(y)){
                ;//do nothing
            }else {

                reduced = y.reduceDomain(x);
                //System.out.print("removed: " + x);
                //System.out.println(" with input: " + x.input);
            }
        } else {
            if (x.isEndPoint()) {
                if (x.sameFlow(y)) {
                    ;//do nothing
                } else {
                    reduced = y.reduceDomain(x);
                    /*System.out.print("removed endpoint: " + x);
                    System.out.print(" with input: " + x.input);
                    System.out.println(" having flow: " + x.flow);*/
                }

            } else {
                ;//do nothing
            }
        }
        //if y's domain is empty this node is an dead end
        if(y.domain.isEmpty()){
            //System.out.println("Empty domain!");
            throw new IllegalStateException();
        }
        return reduced;
    }

    @Override
    protected boolean isSolution(Node node) {
        FPNode x = (FPNode) node;
        //checks that whole board is covered
        for (int i = 0; i <x.grid.length; i++) {
            for (int j = 0; j <x.grid[0].length; j++) {
                if(x.grid[i][j].color.equals("white"))
                    return false;
            }
        }
        //checks that all flows are connected
        for (Flow flow : x.flows){
            if(!flow.isConnected()){
                //System.out.println("connected" + flow);
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isContradictary(Node node) {
        FPNode x = (FPNode) node;
        for (int i = 0; i <x.grid.length; i++) {
            for (int j = 0; j < x.grid[0].length; j++) {
                if (x.grid[i][j].domain.size() == 0)
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void GACRerun(Node node) {
        FPNode x = (FPNode) node;
        addNeighboursForRevising(x.assumedCell,x);
        domainFilteringLoop(x);
        if(pruneTree(x))
            throw new IllegalStateException("invalid domain");

    }

    private boolean pruneTree(FPNode x) {
        for (Flow flow : x.flows) {
            if (!flow.isConnected()) {
                if (noPathForFlow(flow, x))
                    return true;
            }
        }
        /*
        for (int i = 0; i < x.grid.length; i++) {
            for (int j = 0; j < x.grid[0].length; j++) {
                Cell cell = x.grid[i][j];
                if(!cell.hasInput()){
                    if(cell.hasFlow()){//this is an endpoint
                        if(endPointHasInvalidDomain(cell)){
                            return true;
                        }
                    }
                }
            }
        }*/
        return false;
    }

    private boolean noPathForFlow(Flow flow, FPNode x) {
        NavigationNode navNode = x.toNavigationNode();
        navNode.grid[flow.head[0]][flow.head[1]] = 'h';
        navNode.grid[flow.end[0]][flow.end[1]] = 'g';
        //System.out.println(navNode);

        NavigationAStar nav = new NavigationAStar(null);
        nav.initialize(navNode);

        return nav.bestFirstSearch() == null;

    }

    //returns true if all  neigbours has flow, but none of the flows are equal to the endpoints.
    private boolean endPointHasInvalidDomain(Cell cell) {
        for (Cell neighbour: cell.domain){
            if(!neighbour.hasFlow())
                return false;
            if(neighbour == cell.input)//in cases where the endpoint is extended
                continue;
            if(neighbour.sameFlow(cell))
                return false;
        }
        return true;
    }

    @Override
    protected void printStats(Node x, AStar search) {
        int c = 0;
        //total number of nodes in search tree
        c = search.closed.size()+search.open.size();
        System.out.println("Total nodes in search tree: " + c);

        //expanded nodes
        c = search.counter;
        System.out.println("Total expanded nodes: " + c);

        //assumptions needed
        Node current = x;
        c = 1;
        while(current.parent != null){
            c++;
            current = current.parent;
        }
        System.out.println("Assumptions needed: " + c);
    }

    @Override
    protected void updateUI(Node node) {
        main.updateUI(((FPNode)node).grid);
    }
}
