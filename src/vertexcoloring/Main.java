package vertexcoloring;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tools.InputReader;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../vertexcoloring/graph.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        final VertexColoringAStarGAC vcGAC = new VertexColoringAStarGAC(this);

        EventHandler<KeyEvent> listener = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String[] input;
                try {
                    switch (event.getCode()){
                        case DIGIT1:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph1.txt");
                            controller.scale = 15;
                            domainSize = 4;
                            break;
                        case DIGIT2:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph2.txt");
                            controller.scale = 7;
                            domainSize = 4;
                            break;
                        case DIGIT3:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph3.txt");
                            domainSize = 4;
                            break;
                        case DIGIT4:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph4.txt");
                            domainSize = 4;
                            break;
                        case DIGIT5:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph5.txt");
                            domainSize = 6;
                            break;
                        case DIGIT6:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph6.txt");
                            domainSize = 4;
                            controller.scale = 0.04;
                            controller.centeringX = 840;
                            controller.centeringY = 500;
                            break;
                        case DIGIT7:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph7.txt");
                            domainSize = 8;
                            break;
                        case DIGIT8:
                            input = InputReader.readLines("/Users/espen/Dropbox/IntelliJ/2014H/resources/graph8.txt");
                            domainSize = 4;
                            break;
                        /*case K:
                            System.out.println("old value: " + domainSize);
                            if(domainSize == 6)
                                domainSize = 0;
                            else
                                domainSize++;
                            System.out.println("domainsize updated to " + domainSize);
                            return;*/
                        default:return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                String[] count = input[0].split(" ");
                int nv = Integer.parseInt(count[0]);
                int ne = Integer.parseInt(count[1]);
                controller.vertices = getVertices(input,1,nv+1);
                controller.edges = getEdges(input, nv + 1, nv + 2 + ne - 1);
                controller.drawGraph();

                Task task = new Task<Void>(){

                    @Override
                    protected Void call() throws Exception {
                        vcGAC.setup(getFullDomain(),controller.vertices,controller.edges);
                        VCNode x = (VCNode) vcGAC.run();
                        updateUI((x).vertices);
                        return null;
                    }

                };
                new Thread(task).start();
                event.consume();
            }
        };

        root.setOnKeyPressed(listener);
        primaryStage.setTitle("Hello, yFiles for JavaFX");
        primaryStage.setScene(new Scene(root, 1680, 1050));
        primaryStage.show();
    }

    private ArrayList<Edge> getEdges(String[] input, int start, int to) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (int i = start; i < to; i++) {
            String[] edge = input[i].split(" ");
            edges.add(new Edge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1])));
        }
        return edges;
    }

    private Vertex[] getVertices(String[] input, int start, int to) {

        Vertex[] vertices = new Vertex[to-start];
        for (int i = start; i < to; i++) {
            String[] data = input[i].split(" ");
            Vertex vertex = new Vertex(Integer.parseInt(data[0]),Double.parseDouble(data[1]),Double.parseDouble(data[2]));
            vertices[vertex.id] = vertex;
        }
        return vertices;
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void updateUI(final Vertex[] vertices) {
        Platform.runLater(new Runnable(){

            @Override
            public void run() {
                for (Vertex vertex : vertices)
                    controller.colorNode(vertex.id,vertex.color);
            }
        });
    }
    public static int domainSize = 4;
    public static List<Color> getFullDomain(){
        ArrayList<Color> domain = new ArrayList<Color>();
        domain.add(Color.BLUE);
        domain.add(Color.GREEN);
        domain.add(Color.RED);
        domain.add(Color.YELLOW);
        domain.add(Color.ORANGE);
        domain.add(Color.PURPLE);
        domain.add(Color.BROWN);
        domain.add(Color.PINK);
        return domain.subList(0,domainSize);
    }
}
