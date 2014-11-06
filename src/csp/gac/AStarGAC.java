package csp.gac;

import astar.AStar;
import astar.Node;
import javafx.scene.paint.Color;
import jdk.nashorn.internal.ir.IfNode;
import tools.Constants;
import tools.Interpreter;

import java.util.*;

/**
 * Created by espen on 20/09/14.
 */
public abstract class AStarGAC {


    protected abstract void initialize(Node x);
    public abstract Node generateInitialNode();
    protected abstract void domainFilteringLoop(Node x);
    protected abstract boolean isSolution(Node node);
    protected abstract boolean isContradictary(Node node);
    protected abstract void GACRerun(Node node);
    protected AStar search;
    public Queue<Revise> queue;

    public AStarGAC(AStar search){
        this.search = search;
        queue = new ArrayDeque<Revise>();
    }


    public Node run(){

        //initial phase
        Node x = generateInitialNode();
        search.initialize(x);
        initialize(x);
        try {
            domainFilteringLoop(x);
        }catch(IllegalStateException e){
            return null;
        }
        updateUI(x);
        if(isSolution(x)){
            return x;
        }

        //do assumptions (searching)
        while(!search.open.isEmpty()) {
            try {
                Thread.sleep(Constants.DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x = search.pop(search.open);
            search.counter++;
            x.generateId();
            search.closed.add(x);
            updateUI(x);


            if(isSolution(x)) {
                printStats(x,search);

                return x;
            }

            ArrayList<Node> successors = search.generateAllSuccessors(x);
            ArrayList<Node> badSuccesssors = new ArrayList<Node>();
            for (Node node : successors){
                node.generateId();
                queue.clear();
                try {
                    //doing domain filtering on all the successors
                    GACRerun(node);
                    node.generateId();
                //cathes an exception if a successors ends up with a variable that has an empty domain
                }catch (IllegalStateException e){
                    badSuccesssors.add(node);
                    //sets the h value to max since it will never be completed and adds it to the closed list
                    node.h = Integer.MAX_VALUE;
                    node.generateId();
                    search.closed.add(node);
                }
            }
            //removes successors that has hit a dead end.
            successors.removeAll(badSuccesssors);
            //evaluates the successors so the best sucessors will be popped.
            search.evalSuccesors(successors, x);

            System.out.println("evaluated successors, there is now " + search.open.size() + " elements in the open list");
            if(search.open.isEmpty()){
                updateUI(x);
            }
        }
        return x;
    }

    protected abstract void printStats(Node x, AStar search);

    protected abstract void updateUI(Node node);

}
