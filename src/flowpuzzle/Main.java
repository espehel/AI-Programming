package flowpuzzle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tools.Converter;
import tools.InputReader;


public class Main extends Application {

    Pane[][] panes;
    GridPane grid = new GridPane();
    final FlowPuzzleAstarGAC gac = new FlowPuzzleAstarGAC(this);
    @Override
    public void start(Stage primaryStage) throws Exception{


        EventHandler<KeyEvent> listener = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                FPNode node;
                try {
                    switch (event.getCode()){
                    case DIGIT0:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow0.txt"));
                        break;
                    case DIGIT1:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow1.txt"));
                        break;
                    case DIGIT2:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow2.txt"));
                        break;
                    case DIGIT3:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow3.txt"));
                        break;
                    case DIGIT4:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow4.txt"));
                        break;
                    case DIGIT5:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow5.txt"));
                        break;
                    case DIGIT6:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow6.txt"));
                        break;
                    case DIGIT7:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow104.txt"));
                        break;
                    case DIGIT8:
                        node = createFPNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/flow107.txt"));
                        break;
                    case B:

                        return;
                    case D:

                        return;
                    default:return;
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                panes = new Pane[node.grid.length][node.grid[0].length];
                for (int i = 0; i < node.grid.length; i++) {
                    for (int j = 0; j < node.grid[0].length; j++) {
                        Pane pane = getPane(node.grid[i][j].color);

                        panes[i][j] = pane;
                        grid.add(pane,j,i);

                    }

                }
                //updateUI(node.grid);
                gac.setup(node);
                Task task = new Task<Void>(){

                    @Override
                    protected Void call() throws Exception {
                        System.out.println("starting algorithm");
                        try {
                            gac.run();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        System.out.println("finished algorithm");
                        return null;
                    }
                };
                new Thread(task).start();

                event.consume();
            }
        };
        grid.setOnKeyPressed(listener);

        //Parent root = FXMLLoader.load(getClass().getResource("graph.fxml"));
        primaryStage.setTitle("Flow puzzle");
        primaryStage.setScene(new Scene(grid, 500, 500));
        grid.requestFocus();
        primaryStage.show();

    }
    public static Pane getPane(String type){
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: "+type+";");
        pane.setStyle("-fx-border-color: black;");
        pane.setPrefSize(500,500);

        return pane;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    static FPNode createFPNode(String file){

        String[] input = InputReader.readLines(file);

        int dim = Integer.parseInt(input[0].split(" ")[0]);
        int flowCount = Integer.parseInt(input[0].split(" ")[1]);
        //System.out.println(flowCount);
        //char[][] grid = new char[dim][dim];
        Cell[][] grid = new Cell[dim][dim];


        //sets whole board to whitespace
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Cell(i,j);
                grid[i][j].color = "white";
            }
        }

        Flow[] flows = new Flow[flowCount];
        for (int i = 1; i < flowCount+1; i++) {
            int[] data = Converter.StringsToIntArray(input[i].split(" "));
            //instantiates a new flow object
            Flow flow = new Flow();
            flow.id = data[0];
            flow.color = getColor(Character.forDigit(flow.id,16));
            flow.start[0] = data[1];
            flow.start[1] = data[2];
            flow.head[0] = data[1];
            flow.head[1] = data[2];
            flow.end[0] = data[3];
            flow.end[1] = data[4];
            //adds it to list of flows
            flows[flow.id] = flow;
            if(flow.id > 15) {
                System.out.println("To many colors");
                continue;
            }
            //sets the flows endpoints in the grid
            //grid[flow.start[0]][flow.start[1]] = Character.forDigit(flow.id,16);
            //grid[flow.end[0]][flow.end[1]] = Character.forDigit(flow.id,16);
            grid[flow.start[0]][flow.start[1]].color = flow.color;//getColor(Character.forDigit(flow.id,16));
            grid[flow.end[0]][flow.end[1]].color = flow.color;//getColor(Character.forDigit(flow.id,16));
            grid[flow.start[0]][flow.start[1]].flow = flow;
            grid[flow.end[0]][flow.end[1]].flow = flow;
            //the endpoint's input is themselves
            grid[flow.start[0]][flow.start[1]].input = grid[flow.start[0]][flow.start[1]];
            //grid[flow.end[0]][flow.end[1]].input = grid[flow.end[0]][flow.end[1]];
        }
        return new FPNode(grid,flows);
    }

    public void updateUI(final Cell[][] board) {

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                final int x = i;
                final int y = j;
                Platform.runLater(new Runnable(){

                    @Override
                    public void run() {
                        panes[x][y].setStyle("-fx-background-color: " + board[x][y].color + "; -fx-border-color: black;");
                        panes[x][y].applyCss();
                    }
                });

            }
        }
    }
    public static String getColor(char c){
        switch (c) {
            case '0':
                return "green";
            case '1':
                return "blue";
            case '2':
                return "yellow";
            case '3':
                return "red";
            case '4':
                return "purple";
            case '5':
                return "pink";
            case '6':
                return "brown";
            case '7':
                return "orange";
            case '8':
                return "coral";
            case '9':
                return "dimgray";
            case 'a':
                return "lime";
            case 'b':
                return "navy";
            case 'c':
                return "olive";
            case 'd':
                return "silver";
            case 'e':
                return "tomato";
            case 'f':
                return "gold";
            case ' ':
                return "white";
            default:
                return "black";
        }
    }
}
