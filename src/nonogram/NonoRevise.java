package nonogram;

import csp.gac.Revise;

/**
 * Created by espen on 27/10/14.
 */
public class NonoRevise extends Revise {
    public Line column;
    public Line row;

    public NonoRevise(Line column, Line row, String constraint) {
        super(constraint);
        this.column = column;
        this.row = row;
    }
}
