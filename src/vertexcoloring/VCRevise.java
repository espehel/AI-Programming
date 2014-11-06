package vertexcoloring;

import csp.gac.Revise;

/**
 * Created by espen on 21/09/14.
 */
public class VCRevise extends Revise {


    public Vertex X;
    public Vertex Y;
    public String constraint;

    public VCRevise(Vertex x, Vertex y, String constraint) {
        super(constraint);
        X = x;
        Y = y;
        this.constraint = constraint;
    }
}
