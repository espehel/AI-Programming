package vertexcoloring;

import astar.AStar;
import astar.Node;
import csp.gac.AStarGAC;
import javafx.scene.paint.Color;
import tools.Interpreter;
import vertexcoloring.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by espen on 20/09/14.
 */
public class VertexColoringAStarGAC extends AStarGAC {

    Main main;
    List<Color> domain;
    Vertex[] vertices;
    ArrayList<Edge> edges;

    public VertexColoringAStarGAC(Main main) {
        super(new VertexColoringAStar());
        this.main = main;

    }
    public void setup(List<Color> domain, Vertex[] vertices, ArrayList<Edge> edges){
        this.domain = domain;
        this.vertices = vertices;
        this.edges = edges;
    }

    @Override
    protected void printStats(Node x, AStar bfs) {
        VCNode node = (VCNode) x;
        int c =0;
        //unsatisfied constriants
        for(Vertex vertex : node.vertices){
            if(vertex.domain.size()>1){
                c++;
            }
        }
        System.out.println("Unsatisfied constraints: " + c);

        //uncolored nodes
        c = 0;
        for(Vertex vertex : node.vertices){
            if(vertex.color == null){
                c++;
            }
        }
        System.out.println("Uncolored nodes: " + c);

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
    protected void initialize(Node node) {

        VCNode vcNode = (VCNode) node;

        for (Vertex x : vcNode.vertices){
            addNeighboursForRevising(x);
        }

    }

    @Override
    public Node generateInitialNode() {
        VCNode node = new VCNode();
        node.vertices = vertices;

        for (int i = 0; i < node.vertices.length; i++) {
            node.vertices[i].domain = domain;
            node.vertices[i].color = null;
        }

        for(Edge edge : edges){
            vertices[edge.to].neighbours.add(vertices[edge.from]);
            vertices[edge.from].neighbours.add(vertices[edge.to]);
        }
        return node;
    }

    @Override
    protected void domainFilteringLoop(Node x) throws IllegalStateException{

        while(!queue.isEmpty()){

            //fetces next revisee to be revised
            VCRevise revisee = (VCRevise)queue.poll();
            //revises the revisee, if domain is changed, it returns true
            //boolean reduced = revise(revisee);

            boolean reduced = generalRevise(revisee);

            //if domain is reduced, the neighbours of X is added to the queue, checking towards x
            if(reduced){
                for (Vertex vertex : revisee.X.neighbours)
                    //does not add the vertex that made x change its domain
                    if(vertex.id != revisee.Y.id){
                        queue.add(new VCRevise(vertex,revisee.X, "x != y"));
                    }
            }

        }

    }
    private void addNeighboursForRevising(Vertex assumed){
        for (Vertex x : assumed.neighbours){
            queue.add(new VCRevise(x,assumed,"x != y"));
        }
    }

    @Override
    protected boolean isSolution(Node node) {
        VCNode x = (VCNode) node;
        for (Vertex vertex : x.vertices){
            if(vertex.domain.size() != 1)
                return false;
        }
        return true;
    }

    @Override
    protected boolean isContradictary(Node node) {
        VCNode x = (VCNode) node;
        for (Vertex vertex : x.vertices){
            if(vertex.domain.size() == 0)
                return true;
        }
        return false;
    }

    @Override
    protected void GACRerun(Node node) {
        VCNode x = (VCNode) node;
        addNeighboursForRevising(x.vertices[x.assumedVertexId]);
        domainFilteringLoop(x);

    }

    @Override
    protected void updateUI(Node node) {

        main.updateUI(((VCNode)node).vertices);
    }

    public boolean revise(VCRevise revisee){
        //Vertex x = revisee.X;
        //Vertex y = revisee.Y;
        String c = revisee.constraint;
        boolean retained = false;
        Set<Color> inCosistentVariabels = new HashSet<Color>();
        for (Color x : revisee.X.domain){
            boolean consistent = false;
            for(Color y : revisee.Y.domain){
                if(!x.equals(y)){
                    consistent = true;
                    break;
                }
            }
            if(!consistent){
                inCosistentVariabels.add(x);
                retained = true;
            }

        }
        revisee.X.domain.removeAll(inCosistentVariabels);
        if(revisee.X.domain.size() == 1)
            revisee.X.color = revisee.X.domain.get(0);
        if(revisee.X.domain.isEmpty()){
            System.out.println("Empty domain!");
            throw new IllegalStateException();
        }
        return retained;
    }
    public boolean generalRevise(VCRevise revisee){
        String c = revisee.constraint;
        //instantiets the interpreter with the constraint for these variables
        Interpreter interpreter = new Interpreter(c);
        boolean retained = false;
        Set<Color> inCosistentVariabels = new HashSet<Color>();
        //checks each of X's color against Y's colors.
        for (Color x : revisee.X.domain){
            //will assume this is not consistent
            boolean consistent = false;
            for(Color y : revisee.Y.domain){
                //if this is found consistent it will move on to the next element in X's domain without do anything
                if(interpreter.interpret(x, "x", y, "y")){
                    consistent = true;
                    break;
                }
            }
            //if this element is not consistent it will be removed from X's domain
            if(!consistent){
                inCosistentVariabels.add(x);
                retained = true;
            }

        }
        //removes the elements from the domains that are incosistent
        revisee.X.domain.removeAll(inCosistentVariabels);
        //if there is only one color left the graphic will draw this color.
        if(revisee.X.domain.size() == 1)
            revisee.X.color = revisee.X.domain.get(0);
        //an empty domain means that this state is an dead end, an exception will eb thrown wich AStarGAC will handle
        if(revisee.X.domain.isEmpty()){
            throw new IllegalStateException();
        }
        return retained;
    }


}
