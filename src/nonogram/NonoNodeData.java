package nonogram;

import astar.Node;

import java.util.ArrayList;

/**
 * Created by espen on 27/10/14.
 */
public class NonoNodeData{

    public ArrayList<Segment[]> columns;
    public ArrayList<Segment[]> rows;
    public ArrayList<ArrayList<boolean[]>> columnPerms;
    public ArrayList<ArrayList<boolean[]>> rowPerms;
}
