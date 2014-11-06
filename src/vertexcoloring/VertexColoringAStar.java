package vertexcoloring;

import astar.AStar;
import astar.Node;
import javafx.scene.paint.Color;
import vertexcoloring.VCNode;
import vertexcoloring.Vertex;

import java.util.*;

/**
 * Created by espen on 21/09/14.
 */
public class VertexColoringAStar extends AStar {

    public VertexColoringAStar() {
        super();
    }

    @Override
    protected int calculateH(Node node) {
        int domainOptions = 0;
        VCNode x = (VCNode) node;
        for (Vertex vertex : x.vertices){
            domainOptions += vertex.domain.size()-1;
        }
        return domainOptions;
    }

    @Override
    public ArrayList<Node> generateAllSuccessors(Node node) {
        VCNode x = (VCNode) node;
        ArrayList<Node> successors = new ArrayList<Node>();
        List<Vertex> sortedVertices = new ArrayList<Vertex>();
        //sorts the list so the vertexes with the smallest domain is first
        for (Vertex vertex : x.vertices){
            //skips singleton domains
            if(vertex.domain.size()<=1)
                continue;
            //adds the first vertex
            else if(sortedVertices.size()==0)
                sortedVertices.add(vertex);
            else{
                //iterates thru the list of sorted vertices
                for (int i = 0; i < sortedVertices.size(); i++) {
                    //if the current domain is bigger than the vertex's one, the vertexe's one is placed before it
                    if(sortedVertices.get(i).domain.size()>vertex.domain.size()) {
                        sortedVertices.add(i, vertex);
                        break;
                    }
                    //if it reaches the end of the list then vertex is placed at the end
                    if(i==sortedVertices.size()-1) {
                        sortedVertices.add(vertex);
                        break;
                    }
                }

            }
        }

        int i = 0;
        //creates new successr nodes for the 4 first elements in the sorted list
        for (Vertex vertex : sortedVertices){
            //this should never be true
            if(vertex.domain.size()==1)
                continue;
            VCNode successor = new VCNode();
            //creates a deepcopy of the vertex and assumes a color
            successor.vertices = deepCopyVertices(x.vertices, vertex.id);
            successors.add(successor);
            successor.assumedVertexId = vertex.id;
            i++;
            //only takes the four first states
            if(i>=4)
                break;

        }

        return successors;
    }

    private Vertex[] deepCopyVertices(Vertex[] vertices, int vertexId) {
        Vertex[] newVertices = new Vertex[vertices.length];
        //creates a new vertex list and assumes a color
        for (Vertex vertex : vertices){
            //copies basic values
            Vertex newVertex = new Vertex(vertex.id,vertex.x,vertex.y);
            //copies the GUINode, so it still point to the same object
            newVertex.GUINode = vertex.GUINode;
            //sets the new vertexs color to one of the color in the old ones domain
            if(newVertex.id == vertexId) {
                newVertex.color = vertex.domain.get(new Random().nextInt(vertex.domain.size()));
                newVertex.domain = new ArrayList<Color>();
                newVertex.domain.add(newVertex.color);
            }
            //this vertex should be identical to the one in the parent node
            else{
                newVertex.domain = copyDomain(vertex.domain);
                if(newVertex.domain.size()==1)
                    newVertex.color = newVertex.domain.get(0);
                else
                    newVertex.color = null;
            }
            newVertex.neighbours = new HashSet<Vertex>();
            //adds the new vertex to the new list
            newVertices[vertex.id] = newVertex;
        }

        //adding neighbours to the new vertices
        for (int i = 0; i <vertices.length; i++) {
            for (Vertex vertex : vertices[i].neighbours){
                newVertices[i].neighbours.add(newVertices[vertex.id]);
            }
        }
        return newVertices;
    }

    private List<Color> copyDomain(List<Color> domain) {
        List<Color> newDomain = new ArrayList<Color>();
        for (Color color : domain){
            newDomain.add(color);
        }
        return newDomain;
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
    @Override
    public void run(){

    }
}
