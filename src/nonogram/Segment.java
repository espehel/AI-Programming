package nonogram;


import java.util.ArrayList;

/**
 * Created by espen on 27/10/14.
 */
public class Segment {

    public int line;
    public int pos;
    public int start = -1;
    public ArrayList<Integer> domain;
    public int size;
    public String type;

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder( "Segment{" +
        "line=" + line +
        ", start=" + start + ",domain=[");

        for (Integer integer : domain){
           output.append(integer + ", ");
       }
        output.append("], size=" + size +'}');

        return output.toString();
    }
}
