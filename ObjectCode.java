public class ObjectCode {

    private int opCode;
    private int location;
    private int format;
    private boolean simple;
    private boolean indirect;
    private boolean immediate;
    private boolean extended;
    private boolean pc;


    public ObjectCode(){
        opCode = 0;
        location = 0;
        format = 0;
        simple = false;
        indirect = false;
        immediate = false;
        extended = false;
        pc = false;

    }
}
