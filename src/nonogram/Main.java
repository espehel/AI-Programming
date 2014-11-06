package nonogram;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tools.*;

import java.util.ArrayList;


public class Main extends Application {

    Pane[][] panes;
    GridPane grid = new GridPane();
    final NonogramAStarGAC gac = new NonogramAStarGAC(this);
    @Override
    public void start(Stage primaryStage) throws Exception{


        EventHandler<KeyEvent> listener = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                NonoNode node;
                try {
                    switch (event.getCode()){
                    case DIGIT0:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-camel.txt"));
                        break;
                    case DIGIT1:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-cat.txt"));
                        break;
                    case DIGIT2:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-chick.txt"));
                        break;
                    case DIGIT3:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-heart-1.txt"));
                        break;
                    case DIGIT4:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-rabbit.txt"));
                        break;
                    case DIGIT5:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-sailboat.txt"));
                        break;
                    case DIGIT6:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-telephone.txt"));
                        break;
                    case DIGIT7:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-test.txt"));
                        break;
                    case DIGIT8:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-d1.txt"));
                        break;
                    case DIGIT9:
                        node = createNonoNode(("/Users/espen/Dropbox/IntelliJ/2014H/resources/nono-d2.txt"));
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
                panes = new Pane[node.rows.size()][node.columns.size()];
                for (int i = 0; i < panes.length; i++) {
                    for (int j = 0; j < panes[0].length; j++) {
                        Pane pane = getPane(getColor(false));

                        panes[i][j] = pane;
                        grid.add(pane,j,i);

                    }

                }


                updateUI(node);
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

        primaryStage.setTitle("Nonograms");
        primaryStage.setScene(new Scene(grid, 500, 500));
        grid.requestFocus();
        primaryStage.show();

    }

    private String getColor(boolean filled) {
        return filled ? "blue" : "white";
    }

    public static Pane getPane(String type){
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: "+type+";");
        pane.setStyle("-fx-border-color: black;");
        pane.setPrefSize(500, 500);

        return pane;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public NonoNode createNonoNode(String file){

        String[] input = InputReader.readLines(file);

        int columnCount = Integer.parseInt(input[0].split(" ")[0]);
        int rowCount = Integer.parseInt(input[0].split(" ")[1]);

        NonoNodeData node = new NonoNodeData();
        node.columns = new ArrayList<Segment[]>();
        node.rows = new ArrayList<Segment[]>();

        String[] inputRows = MyArrays.reverse(input,1,rowCount);

        populateList(rowCount,node.rows,inputRows,0, Constants.ROW);
        populateList(columnCount,node.columns,input,1+rowCount,Constants.COLUMN);

        calculateDomain(node.rows,columnCount);
        calculateDomain(node.columns, rowCount);

        node.rowPerms = new ArrayList<ArrayList<boolean[]>>();
        node.columnPerms = new ArrayList<ArrayList<boolean[]>>();
        generatePermutations(columnCount, node.rowPerms, node.rows);
        generatePermutations(rowCount, node.columnPerms, node.columns);




        NonoNode node2 = new NonoNode();
        node2.columns = new ArrayList<Line>();
        node2.rows = new ArrayList<Line>();
        node2.columnLimits = new int[columnCount];
        node2.columnSegments = node.columns;


        for (int i = 0; i < rowCount; i++) {
            Line line = new Line();
            line.domain = node.rowPerms.get(i);
            line.length = columnCount;
            line.pos = i;
            line.type = Constants.ROW;
            node2.rows.add(line);
        }

        for (int i = 0; i < columnCount; i++) {
            Line line = new Line();
            line.domain = node.columnPerms.get(i);
            line.length = rowCount;
            line.pos = i;
            line.type = Constants.COLUMN;
            node2.columnLimits[i] = Calculate.sum(node.columns.get(i));
            node2.columns.add(line);
        }

        return node2;
    }

    private void generatePermutations(int count, ArrayList<ArrayList<boolean[]>> perms, ArrayList<Segment[]> list) {
        for (Segment[] segments:list){
            perms.add(generateColumnPossibilities(segments,0,0,new ArrayList<boolean[]>(),new boolean[count]));
        }
    }

    private void calculateDomain(ArrayList<Segment[]> list, int length) {
        for (Segment[] segments : list){
            int i = 0;
            for (Segment segment : segments) {
                int first = calculateFirst(segments, i);
                int last = length - calculateLast(segments, i);
                for (int j = first; j <= last; j++) {
                    segment.domain.add(j);
                }
                i++;
            }
        }
    }

    private int calculateLast(Segment[] segments, int i) {
        int last = 0;
        for (int j = i+1; j < segments.length; j++) {
            last += segments[j].size + 1;
        }
        last+=segments[i].size;
        return last;
    }

    private int calculateFirst(Segment[] segments, int i) {
        int first = 0;
        for (int j = 0; j < i; j++) {
            first += segments[j].size + 1;
        }
        return first;
    }

    public void populateList(int count, ArrayList<Segment[]> list, String[] input, int offset, String type){
        for (int i = 0; i < count; i++) {
            String[] segmentStrings = input[i+offset].split(" ");
            Segment[] segments = new Segment[segmentStrings.length];

            for (int j = 0; j < segmentStrings.length; j++) {
                Segment segment = new Segment();
                segment.size = Integer.parseInt(segmentStrings[j]);
                segment.line = i;
                segment.type = type;
                segment.domain = new ArrayList<Integer>();
                segment.pos = j;
                segments[j] = segment;
            }
            list.add(segments);
        }
    }

    public void updateUI(NonoNode node) {

        boolean[][] grid = node.toGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {

                final String color = getColor(grid[i][j]);
                final int x = i;
                final int y = j;
                Platform.runLater(new Runnable(){

                    @Override
                    public void run() {
                        panes[x][y].setStyle("-fx-background-color: " + color + "; -fx-border-color: black;");
                        panes[x][y].applyCss();
                    }
                });

            }
        }
    }
    public static ArrayList<boolean[]> generateColumnPossibilities(Segment[] columnSegments,int i,int lowerBound, ArrayList<boolean[]> possibilities, boolean[] current){
        if(i==columnSegments.length) {
            possibilities.add(current.clone());
            return possibilities;
        }

        for (int n: columnSegments[i].domain){
            if(n<lowerBound)
                continue;

            boolean[] clone = current.clone();
            for (int j = n; j < n+columnSegments[i].size; j++) {
                clone[j] = true;
            }
            generateColumnPossibilities(columnSegments,i+1,n+columnSegments[i].size+1,possibilities,clone);
        }

        return possibilities;
    }
}
