package tools;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by espen on 26/09/14.
 */
public class Interpreter {

    HashMap<String,Object> variables;

    public Interpreter(String constraint) {
        this.variables  = new HashMap<String, Object>();
        String[] symbols = constraint.split(" ");
        variables.put(symbols[0], null);
        variables.put(symbols[2], null);
        variables.put("OP", symbols[1]);
    }
    public boolean interpret(Object o1, String v1, Object o2, String v2){
        variables.put(v1,o1);
        variables.put(v2,o2);

        if(variables.get("OP").equals("!=")) {
            return variables.get(v1) != variables.get(v2);
        }
        throw new IllegalArgumentException();
    }
}
