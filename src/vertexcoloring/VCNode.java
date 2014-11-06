package vertexcoloring;

import astar.Node;

/**
 * Created by espen on 21/09/14.
 */
public class VCNode extends Node {


    public int assumedVertexId;
    //    public ArrayList<Color> domain;
  //  public Vertex vertex;
    //public Color color;
    public Vertex[] vertices;

    @Override
    protected boolean isSameState(Node other) {
        for (int i = 0; i < vertices.length; i++) {
            if(!isSameDomain(this.vertices[i],((VCNode)other).vertices[i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public void generateId() {

    }

    private boolean isSameDomain(Vertex thisVertex, Vertex otherVertex) {
        if(thisVertex.domain.size()!=otherVertex.domain.size())
            return false;

        for (int i = 0; i < thisVertex.domain.size(); i++) {
                if(!otherVertex.domain.contains(thisVertex.domain.get(i)))
                    return false;
        }
        return true;
    }

}
