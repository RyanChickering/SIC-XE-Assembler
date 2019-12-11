
public class Literal {
    String name;
    int length;
    String address;

    public Literal(String name, int length, String address) {
        name = name;
        length = length;
        address = address;
    }

    public String name() {
        return name;
    }

    public int length() {
        return length;
    }

    public String address() { return address; }
}