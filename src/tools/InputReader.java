package tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by espen on 20/09/14.
 */
public class InputReader {

    public static String[] readLines(String file) {
        ArrayList<String> input;
        try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        input = new ArrayList<String>();
        String line;
        while((line = reader.readLine()) != null){
            input.add(line);
        }
        }catch (Exception e){
            System.out.println("File reading exception");
            return null;
        }
        return input.toArray(new String[input.size()]);
    }
}
