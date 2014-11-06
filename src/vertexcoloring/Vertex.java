package vertexcoloring;

import com.yworks.yfiles.graph.INode;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by espen on 20/09/14.
 */
public class Vertex {
    public int id;
    public double x;
    public double y;
    public INode GUINode;
    public List<Color> domain;
    public Color color;
    public Set<Vertex> neighbours;

    public Vertex(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        neighbours = new HashSet<Vertex>();
    }
}
