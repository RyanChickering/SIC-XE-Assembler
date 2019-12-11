import java.util.HashMap;
import java.util.Map;

public class LITTAB {

    private static final Map<Integer, Literal> LITTAB;
    static {
        LITTAB = new HashMap<>();

    }

    public static void add(int value, String name, int length){
        LITTAB.put(value, new Literal(name, length,  ""));
    }
    public static boolean search(int value){
        return (getLITTAB(value) != null);
    }

    public static void setAddress(int value, String address){
        LITTAB.get(value).address = address;
    }

    public static Literal getLITTAB(int value){
        return LITTAB.get(value);
    }

    public static String getName(int value){
        return LITTAB.get(value).name();
    }

    public static int getLength(int value){
        return LITTAB.get(value).length();
    }

    public static String getAddress(int value){
        return LITTAB.get(value).address();
    }

    public static Map<Integer, Literal> getLittab() {
        return LITTAB;
    }
}
