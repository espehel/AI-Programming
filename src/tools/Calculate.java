package tools;

import nonogram.Segment;

/**
 * Created by espen on 22/10/14.
 */
public class Calculate {
    public static int manhattenDistance(int[] head, int[] goal) {
        return Math.abs(head[0]-goal[0]) + Math.abs(head[1]-goal[1]);
    }

    public static int sum(Segment[] segments) {
        int sum = 0;
        for(Segment segment: segments){
            sum += segment.size;
        }
        return sum;
    }
}
