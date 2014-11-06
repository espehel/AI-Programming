package tools;

/**
 * Created by espen on 12/10/14.
 */
public class Converter{
    public static int[] StringsToIntArray(String[] strings){
        int[] ints = new int[strings.length];
        for (int i = 0; i <strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }
}
