package flowpuzzle;

import tools.Calculate;

import java.util.List;

/**
 * Created by espen on 13/10/14.
 */
public class Cell {

    public Cell input;
    public Cell output;
    //list of cells that this variable can flow into
    public List<Cell> domain;

    public int x;
    public int y;
    public String color;
    public Flow flow;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
                output.append("[(" + x + "," + y + ") " + domain.size() + ":");
        if(hasFlow())
            output.append(Calculate.manhattenDistance(new int[]{x, y}, flow.end) + "]");
        else
            output.append("-]");
        return output.toString();
    }
    public boolean hasInput(){
        return input != null;
    }

    public boolean sameFlow(Cell other) {
        if(other.flow == null || this.flow == null)
            return false;
        return this.flow.id == other.flow.id;
    }

    public boolean hasFlow() {
        return this.flow != null;
    }


    public boolean isEndPoint() {
        if(hasFlow())
            return x == flow.end[0] && y == flow.end[1];
        else
            return false;
    }

    public boolean reduceDomain(Cell x) {
        return domain.remove(x);
    }

    public boolean isSingleton() {
        return domain.size() == 1;
    }

    public boolean isHead() {
        if(hasFlow())
            return x == flow.head[0] && y == flow.head[1];
        else
            return false;
    }

    public boolean propageteFlow() {
        if(isSingleton() && isHead() && !isEndPoint()){
            Cell toCell = domain.get(0);
            if(toCell.hasFlow())
                return false;
            toCell.input = this;
            toCell.flow = flow;
            toCell.color = color;
            flow.head = new int[]{toCell.x,toCell.y};
            toCell.propageteFlow();
            return true;
        }
        return false;
    }

    public int getFlowId() {
        if(hasFlow())
            return flow.id;
        else
            return -1;
    }
}
