package tools;

import nonogram.Line;
import nonogram.Segment;

import java.util.Comparator;

/**
 * Created by espen on 27/10/14.
 */
public class NonogramLineComparator implements Comparator<Line>{

    @Override
    public int compare(Line o1, Line o2) {
            return o1.domain.size()-o2.domain.size();
    }
}
