package flowpuzzle;


import java.util.Arrays;

/**
 * Created by espen on 12/10/14.
 */
public class Flow {

    public String color;
    public int id;
    public int[] start;
    public int[] end;
    public int[] head;

        public Flow() {
            this.start = new int[2];
            this.end = new int[2];
            this.head = new int[2];
        }
    @Override
    public String toString(){
        return "["+color +": ("+ start[0]+"," + start[1] + ") -> ("+ end[0]+"," + end[1] + "), head: ("+head[0]+","+head[1]+")]";
    }
    public Flow getDeepCopy(){
        Flow flow = new Flow();
        flow.id = id;
        flow.start = copyArray(this.start);
        flow.end = copyArray(this.end);
        flow.head = copyArray(this.head);
        flow.color = new String(this.color);
        return flow;
    }
    public int[] copyArray(int[] array){
        return new int[]{array[0],array[1]};
    }

    public boolean isConnected(){
        return head[0] == end[0] && head[1] == end[1];
    }
}
