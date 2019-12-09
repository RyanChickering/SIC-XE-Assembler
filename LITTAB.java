import java.util.HashMap;
import java.util.Map;

public class LITTAB {

    private static final Map<String, Literal> LITTAB;
    static {
        LITTAB = new HashMap<>();

    }

    public static void add(String name, int value, int length){
        LITTAB.put(name, new Literal(value, length,  ""));
    }
    public static boolean search(String name){
        return (getLITTAB(name) != null);
    }

    public static void setAddress(String name, String address){
        LITTAB.get(name).address = address;
    }

    public static Literal getLITTAB(String name){
        return LITTAB.get(name);
    }

    public static int getValue(String name){
        return LITTAB.get(name).value();
    }

    public static int getLength(String name){
        return LITTAB.get(name).length();
    }

    public static String getAddress(String name){
        return LITTAB.get(name).address();
    }

    public static Map<String, Literal> getLittab() {
        return LITTAB;
    }
}
