
public class Literal {
    private int value;
    private int length;
    String address;

    public Literal(int value, int length, String address) {
        value = value;
        length = length;
        address = address;
    }

    public int value() {
        return value;
    }

    public int length() {
        return length;
    }

    public String address() { return address; }
}