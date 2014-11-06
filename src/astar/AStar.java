package astar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by espen on 22/08/14.
 */
public abstract class AStar extends Thread{

    public ArrayList<Node> open, closed;
    public boolean breathFS = false;
    public boolean depthFS = false;
    public int counter = 0;
    public int counter2 = 1;
    public int counter3 = 1;

    public AStar() {

    }

    //handled by subclass for a specific problem
    protected abstract int calculateH(Node node);
    public abstract ArrayList<Node> generateAllSuccessors(Node node);
    protected abstract boolean isSolution(Node node);
    protected abstract int arcCost(Node parent, Node Child);


    //initializing
    public void initialize(Node node){
        open = new ArrayList<Node>();
        closed = new ArrayList<Node>();

        node.g = 0;
        node.h = calculateH(node);
        open.add(node);
    }
    @Override
    public void run(){
        bestFirstSearch();
    }

    public Node bestFirstSearch(){
        while(true){

            if(open.isEmpty())
                return null;

            Node x = pop(open);
            closed.add(x);
            counter++;

            updateUI(x);

            if(isSolution(x)) {
                Node current = x;
                int c = 1;
                //counts number of parents
                while(current.parent != null){
                    c++;
                    current = current.parent;
                }

                return x;
            }

            /*try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            List<Node> successors = generateAllSuccessors(x);
            counter2 += successors.size();
            evalSuccesors(successors,x);



        }
    }

    public void evalSuccesors(List<Node> successors,Node x ){
        for(Node s : successors) {

            if (s == null)
                continue;

            //if s already is made, the old pointer will be pointing to s now.
            s = checkState(s);
            x.children.add(s);

            if (!open.contains(s) && !closed.contains(s)) {
                attachAndEval(s, x);
                open.add(0,s);
                counter3++;
            } else if (x.g + arcCost(x, s) < s.g) {
                attachAndEval(s, x);
                if (closed.contains(s))
                    propagatePathImprovements(s);
            }
            //System.out.println("Evaluation of "+ s.toString());
        }
    }

    protected abstract void updateUI(Node node);

    private void propagatePathImprovements(Node p) {
        for(Node c : p.children){
            if(p.g + arcCost(p,c) < c.g){
                c.parent = p;
                c.g = p.g + arcCost(p,c);
                propagatePathImprovements(c);
            }

        }
    }

    private void attachAndEval(Node c, Node p) {
        c.parent = p;
        c.g = p.g+arcCost(p,c);
        c.h = calculateH(c);
    }

    private Node checkState(Node s) {
        for(Node n : open)
            if(n.isSameState(s)) {
                return n;
            }
        for(Node n : closed){
            if(n.isSameState(s)) {
                return n;
            }
        }
        return s;
    }

    public Node pop(ArrayList<Node> list) {
        if(breathFS)
            return list.remove(list.size() - 1);
        if(depthFS)
            return list.remove(0);



        Node best = list.get(0);
        for(Node node : list){
            if(node.f() < best.f()) {
                best = node;
            }
        }
        list.remove(best);
        return best;
    }

}
