package tools;

import flowpuzzle.Cell;

import java.util.Comparator;

/**
 * Created by espen on 16/10/14.
 */
public class FlowPuzzleCellComparator implements Comparator<Cell> {
    @Override
    public int compare(Cell o1, Cell o2) {
        if(o1.domain.size()-o2.domain.size() != 0)
            return o1.domain.size()-o2.domain.size();
        else
            return Calculate.manhattenDistance(new int[]{o1.x,o1.y},o1.flow.end) - Calculate.manhattenDistance(new int[]{o2.x,o2.y},o2.flow.end);
    }
}
