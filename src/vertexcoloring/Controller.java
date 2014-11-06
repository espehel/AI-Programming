package vertexcoloring;

import com.yworks.yfiles.canvas.GraphControl;
import com.yworks.yfiles.drawing.ArcEdgeStyle;
import com.yworks.yfiles.drawing.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Controller {
    public GraphControl graphControl;
    public Vertex[] vertices = null;
    public ArrayList<Edge> edges = null;

    public void initialize() {
        //Creates and configures a node style.
        ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle(Color.ORANGE); nodeStyle.setDrawingShadow(true);
        //Creates and configures a edge style.
        ArcEdgeStyle edgeStyle = new ArcEdgeStyle();
        // Sets a default style.
        graphControl.getGraph().getNodeDefaults().setStyle(new ShinyPlateNodeStyle(Color.GRAY));
        graphControl.getGraph().getEdgeDefaults().setStyle(edgeStyle);

    }
    //default values that work best on most graphs
    public double scale = 7;
    public int centeringX = 600;
    public int centeringY = 200;
    public void drawGraph(){
        IGraph graph = graphControl.getGraph();

        for (Vertex vertex : vertices){
            vertex.GUINode = graph.createNode(new RectD(centeringX + vertex.x*scale,centeringY + vertex.y*scale,10,10));
        }

        for(Edge edge : edges){
            graph.createEdge(vertices[edge.from].GUINode,vertices[edge.to].GUINode);
        }
    }

    public void colorNode(int id, Color color) {
        IGraph graph = graphControl.getGraph();
        if(color == null)
            graph.setStyle(vertices[id].GUINode,new ShinyPlateNodeStyle(Color.GRAY));
        else
            graph.setStyle(vertices[id].GUINode,new ShinyPlateNodeStyle(color));
    }
}
