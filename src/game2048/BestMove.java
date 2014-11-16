package game2048;

/**
 * Created by espen on 11/11/14.
 */
public class BestMove {

    private Direction direction;
    private double score;

    public Direction getDirection() {
        return direction;
    }

    public double getScore() {
        return score;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
