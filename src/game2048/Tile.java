package game2048;

import com.sun.tools.classfile.StackMapTable_attribute;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.Optional;
import java.util.Random;

/**
 * Created by espen on 07/11/14.
 */
public class Tile extends Label {

    private Integer value;
    private Location location;
    private Boolean merged;

    public static Tile newRandomTile() {
        int value = new Random().nextDouble() < 0.9 ? 2 : 4;
        return new Tile(value);
    }
    public static Tile newTile(int value) {
        return new Tile(value);
    }
    private Tile(Integer value) {
        final int squareSize = Board.CELL_SIZE - 13;
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);
        setAlignment(Pos.CENTER);

        this.value = value;
        this.merged = false;
        setText(value.toString());
        getStyleClass().addAll("game-label", "game-tile-" + value);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getValue() {
        return value;
    }
    public void merge(Tile another) {
        getStyleClass().remove("game-tile-" + value);
        this.value += another.getValue();
        setText(value.toString());
        merged = true;
        getStyleClass().add("game-tile-" + value);
    }
    public boolean isMerged() {
        return merged;
    }

    public void clearMerge() {
        merged = false;
    }

    public boolean isMergeable(Optional<Tile> anotherTile) {
        return anotherTile.filter(t->t.getValue().equals(getValue())).isPresent();
    }

    @Override
    public String toString() {
        return "Tile{" +
                "value=" + value +
                '}';
    }
}
