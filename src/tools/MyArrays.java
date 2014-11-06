package tools;

/**
 * Created by espen on 28/10/14.
 */
public class MyArrays {

    public static String[] reverse(String[] input, int i, int rowCount) {
        String[] reversed = new String[rowCount];
        for (int j = 0; j <reversed.length; j++) {
            reversed[reversed.length-1-j] = input[j+i];
        }
        return reversed;
    }
}
