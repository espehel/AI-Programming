package game2048;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.application.Platform;
import javafx.application.ConditionalFeature;




/**
 * Created by espen on 07/11/14.
 */
public class Main extends Application {

    private GameManager gameManager;
    private Bounds gameBounds;
    private final static int MARGIN = 36;
    private Game2048Expectimax expectimax;
    public int counter = 0;

    @Override
    public void init() {

        Font.loadFont(Main.class.getResource("ClearSans-Bold.ttf").toExternalForm(), 10.0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gameManager = new GameManager();
        gameBounds = gameManager.getLayoutBounds();
        expectimax = new Game2048Expectimax(6,gameManager);

        StackPane root = new StackPane(gameManager);
        root.getStyleClass().addAll("game-root");
        ChangeListener<Number> resize = (ov, v, v1) -> {
            double scale = Math.min((root.getWidth() - MARGIN) / gameBounds.getWidth(), (root.getHeight() - MARGIN) / gameBounds.getHeight());
            gameManager.setScale(scale);
            gameManager.setLayoutX((root.getWidth() - gameBounds.getWidth()) / 2d);
            gameManager.setLayoutY((root.getHeight() - gameBounds.getHeight()) / 2d);
        };
        root.widthProperty().addListener(resize);
        root.heightProperty().addListener(resize);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("game2048/game.css");
        addKeyHandler(scene);
        addSwipeHandlers(scene);



        if (isARMDevice()) {
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
        }

        if (Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            scene.setCursor(Cursor.NONE);
        }

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double factor = Math.min(visualBounds.getWidth() / (gameBounds.getWidth() + MARGIN),
                visualBounds.getHeight() / (gameBounds.getHeight() + MARGIN));
        primaryStage.setTitle("2048 Expectimax");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(gameBounds.getWidth() / 2d);
        primaryStage.setMinHeight(gameBounds.getHeight() / 2d);
        primaryStage.setWidth((gameBounds.getWidth() + MARGIN) * factor);
        primaryStage.setHeight((gameBounds.getHeight() + MARGIN) * factor);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
    private boolean isARMDevice() {
        return System.getProperty("os.arch").toUpperCase().contains("ARM");
    }
    private void addKeyHandler(Scene scene) {
        scene.setOnKeyPressed(key -> {
            KeyCode keyCode = key.getCode();

            if (keyCode.equals(KeyCode.P)) {
                gameManager.pauseGame();
                return;
            }
            if (keyCode.equals(KeyCode.Q) || keyCode.equals(KeyCode.ESCAPE)) {
                gameManager.quitGame();
                return;
            }
            if (keyCode.isArrowKey()) {
                Direction direction = Direction.valueFor(keyCode);
                gameManager.move(direction);
            }
            //expectimax does one step
            if(keyCode.equals(KeyCode.S)){
                Direction direction = expectimax.getNextMove();
                //System.out.println(expectimax.counter);
                //expectimax.counter = 0;
                gameManager.move(direction);
            }
            //expectimax tries to finish the game
            if(keyCode.equals(KeyCode.R)){
                /*counter++;
                Event.fireEvent(scene,new KeyEvent(new EventType<>(""+counter),"s","",KeyCode.S,false,false,false,false));
                */
                runExpectimax();
            }
        });
    }

    private void runExpectimax() {
        if(gameManager.isGameOver())
            return;
        //System.out.println(gameManager.counter + " == " + expectimax.counter);
        //do{
            Task task = new Task<Direction>(){

                @Override
                protected Direction call() throws Exception {
                    Direction direction = null;
                    try {

                        //Thread.sleep(1000);
                        direction = expectimax.getNextMove();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return direction;
                }
            };
            task.setOnSucceeded(event -> {
                gameManager.move((Direction) task.getValue());
                runExpectimax();
            });
            new Thread(task).start();
            //Platform.runLater(() -> gameManager.move(direction));
        //}while(!gameManager.isGameOver());
    }

    private void addSwipeHandlers(Scene scene) {
        scene.setOnSwipeUp(e -> gameManager.move(Direction.UP));
        scene.setOnSwipeRight(e -> gameManager.move(Direction.RIGHT));
        scene.setOnSwipeLeft(e -> gameManager.move(Direction.LEFT));
        scene.setOnSwipeDown(e -> gameManager.move(Direction.DOWN));
    }
}
