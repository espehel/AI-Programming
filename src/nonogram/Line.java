package nonogram;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import jdk.nashorn.internal.ir.ContinueNode;
import org.omg.PortableServer.THREAD_POLICY_ID;
import sun.jvm.hotspot.debugger.posix.elf.ELFSectionHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by espen on 28/10/14.
 */
public class Line {

    public int pos;
    public int length;
    public ArrayList<boolean[]> domain;
    public String type;

    public boolean isSingleton() {
        return domain.size() == 1;
    }


    public Line getDeepCopy() {
        Line line = new Line();
        line.pos = this.pos;
        line.length = this.length;
        line.domain = new ArrayList<boolean[]>();
        line.type = new String(this.type);
        for (boolean[] bools: this.domain)
            line.domain.add(bools.clone());

        return line;
    }

    public boolean[] getLine(){
        if(isSingleton())
            return domain.get(0);
        else
            return new boolean[domain.get(0).length];
    }
    @Override
    public String toString() {
        return "Line{" +
                "pos=" + pos +
                ", length=" + length +
                ", domainSize=" + domain.size() +
                ", type='" + type + '\'' +
                '}';
    }

    public boolean hasDomain(boolean[] rCol) {
        for (boolean[] element: domain){
            boolean equal = egualDomain(element,rCol);
            if(equal)
                return true;
        }
        return false;
    }

    private boolean egualDomain(boolean[] element, boolean[] rCol){
        for (int i = 0; i < element.length; i++) {
            if(rCol[i]){
                if(element[i])
                    continue;
                else
                    return false;
            }
        }
        return true;
    }

    public boolean hasDomain(int row, boolean value) {
        for (boolean[] bools: domain)
            if(bools[row] == value)
                return true;
        return false;
    }
}
