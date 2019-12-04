import java.util.HashMap;
import java.util.Map;



public class OPTable {
    private static final Map<String, Operation> _OPTable;

    static{
        _OPTable = new HashMap<>();

        _OPTable.put("CLEAR", new Operation("CLEAR", "2",   "B4"));
        _OPTable.put("COMP",  new Operation("COMP",  "3/4", "28"));
        _OPTable.put("COMPR", new Operation("COMPR", "2",   "A0"));
        _OPTable.put("J",     new Operation("J",     "3/4", "3C"));
        _OPTable.put("JEQ",   new Operation("JEQ",   "3/4", "30"));
        _OPTable.put("JLT",   new Operation("JLT",   "3/4", "38"));
        _OPTable.put("JSUB",  new Operation("JSUB",  "3/4", "48"));
        _OPTable.put("LDA",   new Operation("LDA",   "3/4", "00"));
        _OPTable.put("LDB",   new Operation("LDB",   "3/4", "68"));
        _OPTable.put("LDCH",  new Operation("LDCH",  "3/4", "50"));
        _OPTable.put("LDT",   new Operation("LDT",   "3/4", "74"));
        _OPTable.put("RD",    new Operation("RD",    "3/4", "D8"));
        _OPTable.put("RSUB",  new Operation("RSUB",  "3/4", "4C"));
        _OPTable.put("STA",   new Operation("STA",   "3/4", "0C"));
        _OPTable.put("STCH",  new Operation("STCH",  "3/4", "54"));
        _OPTable.put("STL",   new Operation("STL",   "3/4", "14"));
        _OPTable.put("STX",   new Operation("STX",   "3/4", "10"));
        _OPTable.put("TD",    new Operation("TD",    "3/4", "E0"));
        _OPTable.put("TIXR",  new Operation("TIXR",  "2",   "B8"));
        _OPTable.put("WD",    new Operation("WD",    "3/4", "DC"));
    }

    public static Map<String, Operation> getOperaionTable() {
        return _OPTable;
    }

}