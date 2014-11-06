package nonogram;

import astar.Node;

import java.util.ArrayList;

/**
 * Created by espen on 27/10/14.
 */
public class NonoNode extends Node{

    public ArrayList<Line> columns;
    public ArrayList<Line> rows;
    public int[] columnLimits;
    public Line assumedRow;
    public ArrayList<Segment[]> columnSegments;

    @Override
    protected boolean isSameState(Node other) {

        if(this.id.equals(other.id)) {
            return true;
        }
        else {
            return false;
        }
        /*
        for (int i = 0; i < rows.size(); i++) {
            Line thisLine = this.rows.get(i);
            Line otherLine = ((NonoNode2)other).rows.get(i);
            if(thisLine.isSingleton()){
                if(otherLine.isSingleton())
                    if(!isSameDomain(thisLine.getLine(), otherLine.getLine()))
                        return false;
                else
                    return false;
            }
            else
                if(otherLine.isSingleton())
                    return false;
        }
        return true;*/
    }
    @Override
    public void generateId(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Line row: rows){
            for (boolean filledCell: row.getLine()){
                if(filledCell){
                    stringBuilder.append(1);
                }
                else
                    stringBuilder.append(0);
            }
        }
        this.id=stringBuilder.toString();
    }

    private boolean isSameDomain(boolean[] thisLine, boolean[] otherLine) {
        for (int i = 0; i < thisLine.length; i++) {
            if(thisLine[i] != otherLine[i])
                return false;
        }
        return true;
    }


    public NonoNode getDeepCopy() {
        NonoNode copy = new NonoNode();

        copy.columns = deepCopyList(this.columns);
        copy.rows = deepCopyList(this.rows);
        copy.columnLimits = this.columnLimits.clone();
        copy.columnSegments = (ArrayList<Segment[]>) this.columnSegments.clone();

        return copy;
    }

    private ArrayList<Line> deepCopyList(ArrayList<Line> list){
        ArrayList<Line> copy = new ArrayList<Line>();
        for ( Line line: list) {
            copy.add(line.getDeepCopy());
        }
        return copy;
    }

    public boolean[][] toGrid(){
        boolean[][] grid = new boolean[rows.size()][columns.size()];

        for (int i = 0; i < rows.size(); i++) {
            grid[i] = rows.get(i).getLine();
        }
        return grid;
    }

    @Override
    public String toString() {
        return "NonoNode2{" +
                "columnsSize=" + columns.size() +
                ", rowsSize=" + rows.size() +
                ", assumedRow=" + assumedRow +
                ", h=" + h +
                '}';
    }
}
