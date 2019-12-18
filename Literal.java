
public class Literal {
    String name;
    String value;
    String address;

    public Literal(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String address(){
        return address;
    }
    public String name() {
        return name;
    }
    public String length() {
        return value;
    }
}