package navigation;

import astar.AStar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import navigation.NavigationAStar;
import navigation.NavigationNode;


public class Main extends Application {

    Pane[][] panes;
    GridPane grid = new GridPane();
    final AStar bfs = new NavigationAStar(this);

    @Override
    public void start(Stage primaryStage) throws Exception{

        EventHandler<KeyEvent> listener = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                NavigationNode node;
                try {
                    switch (event.getCode()){
                    case DIGIT0:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav0.txt")));
                        break;
                    case DIGIT1:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav1.txt")));
                        break;
                    case DIGIT2:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav2.txt")));
                        break;
                    case DIGIT3:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav3.txt")));
                        break;
                    case DIGIT4:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav4.txt")));
                        break;
                    case DIGIT5:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav5.txt")));
                        break;
                    case DIGIT6:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav6.txt")));
                        break;
                    case DIGIT7:
                        node = new NavigationNode(createGrid(readFromFile("/Users/espen/Dropbox/IntelliJ/2014H/resources/nav7.txt")));
                        break;
                    case B:
                        bfs.breathFS = !bfs.breathFS;
                        bfs.depthFS = false;
                        System.out.println("Toogled breath First to " + bfs.breathFS);
                        return;
                    case D:
                        bfs.depthFS = !bfs.depthFS;
                        bfs.breathFS = false;
                        System.out.println("Toogled depth First to " + bfs.depthFS);
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
                        Pane pane;
                        if(node.grid[i][j] == ' ')
                            pane = get("white");
                        else if(node.grid[i][j] == 'h')
                            pane = get("green");
                        else if(node.grid[i][j] == 'g')
                            pane = get("gold");
                        else
                            pane = get("black");

                        panes[i][j] = pane;
                        grid.add(pane,j,i);

                    }

                }
                updateUI(node.grid);
                bfs.initialize(node);
                Task task = new Task<Void>(){

                    @Override
                    protected Void call() throws Exception {
                        bfs.run();
                        return null;
                    }

                };
                new Thread(task).start();

                event.consume();
            }
        };
        grid.setOnKeyPressed(listener);

        //Parent root = FXMLLoader.load(getClass().getResource("graph.fxml"));
        primaryStage.setTitle("Navigation");
        primaryStage.setScene(new Scene(grid, 300, 275));
        grid.requestFocus();
        primaryStage.show();

    }
    public static Pane get(String type){
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: "+type+";");
        pane.setPrefSize(50,50);
        return pane;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private static String[] readFromFile(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        ArrayList<String> input = new ArrayList<String>();
        String line;
        while((line = reader.readLine()) != null){
            input.add(line);
        }

        return input.toArray(new String[input.size()]);
    }

    static char[][] createGrid(String[] input){

        int[] dim = toIntArray(input[0].substring(1, input[0].length() - 1).split(","));
        String[] toFrom = input[1].split(" ");
        int[] start = toIntArray(toFrom[0].substring(1, toFrom[0].length() - 1).split(","));
        int[] goal = toIntArray(toFrom[1].substring(1, toFrom[1].length() - 1).split(","));
        int[][] barriers = new int[input.length-2][4];
        for (int i = 2; i < input.length; i++) {
            barriers[i-2] = toIntArray(input[i].substring(1, input[i].length() - 1).split(","));
        }

        char[][] grid = new char[dim[0]][dim[1]];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {

                if(i == start[0] && j == start[1])
                    grid[i][j] = 'h';
                else if (i == goal[0] && j == goal[1])
                    grid[i][j] = 'g';
                else
                    grid[i][j] = ' ';

            }
        }

        for (int i = 0; i < barriers.length; i++) {
            insertBarrier(grid, barriers[i]);
        }
        return grid;
    }

    private static void insertBarrier(char[][] grid, int[] barrier) {

        for (int i = barrier[0]; i < barrier[0]+barrier[2]; i++) {
            for (int j = barrier[1]; j < barrier[1]+barrier[3]; j++) {
                grid[i][j] = 'x';
            }
                  
        }
    }

    static int[] toIntArray(String[] strings){
        int[] ints = new int[strings.length];
        for (int i = 0; i <strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    public void updateUI(char[][] board) {


        //System.out.println(new NavigationNode(board));
        //System.out.println("-------------------------------------------");

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                String color = "";
                if(board[i][j] == ' ')
                    color = "white";
                else if(board[i][j] == 'h')
                    color = "green";
                else if(board[i][j] == 'g')
                    color = "gold";
                else if(board[i][j] == 'f')
                    color = "green";
                else if(board[i][j] == 'x')
                    color = "black";
                else if(board[i][j] == 'b')
                    color = "green";


                final String c = color;
                final int x = i;
                final int y = j;

                Platform.runLater(new Runnable(){

                    @Override
                    public void run() {
                        panes[x][y].setStyle("-fx-background-color: " + c + ";");
                        panes[x][y].applyCss();
                    }
                });

            }
        }


    }
}
