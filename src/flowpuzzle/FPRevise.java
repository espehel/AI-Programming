package flowpuzzle;

import csp.gac.Revise;

/**
 * Created by espen on 16/10/14.
 */
public class FPRevise extends Revise {

    Cell X;
    Cell Y;

    public FPRevise(Cell x, Cell y, String constraint) {
        super(constraint);
        this.X = x;
        this.Y = y;
    }
}
