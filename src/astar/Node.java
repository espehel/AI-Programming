package astar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by espen on 22/08/14.
 */
public abstract class Node {

    public int g;
    public int h;
    public List<Node> children = new ArrayList<Node>();
    public Node parent;
    public String id;

    public int f(){
        return g+h;
    }
    protected abstract boolean isSameState(Node other);
    public abstract void generateId();


}
