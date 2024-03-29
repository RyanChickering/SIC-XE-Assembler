import java.util.HashMap;
import java.util.Map;



public class OPTable {
    private static final Map<String, Operation> _OPTable;

    static{
        _OPTable = new HashMap<>();


        _OPTable.put("ADD",   new Operation("ADD",   "3", "18"));
        _OPTable.put("ADDF",  new Operation("ADDF",  "3", "58"));
        _OPTable.put("ADDR",  new Operation("ADDR",  "2",   "90"));
        _OPTable.put("AND",   new Operation("AND",   "3", "40"));
        _OPTable.put("CLEAR", new Operation("CLEAR", "2",   "B4"));
        _OPTable.put("COMP",  new Operation("COMP",  "3", "28"));
        _OPTable.put("COMPF", new Operation("COMPF", "3", "88"));
        _OPTable.put("COMPR", new Operation("COMPR", "2",   "A0"));
        _OPTable.put("DIV",   new Operation("DIV",   "3", "24"));
        _OPTable.put("DIVF",  new Operation("DIVF",  "3", "64"));
        _OPTable.put("FIX",   new Operation("FIX",   "1",   "C4"));
        _OPTable.put("FLOAT", new Operation("FLOAT", "1",   "C0"));
        _OPTable.put("HIO",   new Operation("HIO",   "1",   "F4"));
        _OPTable.put("J",     new Operation("J",     "3", "3C"));
        _OPTable.put("JEQ",   new Operation("JEQ",   "3", "30"));
        _OPTable.put("JLT",   new Operation("JLT",   "3", "38"));
        _OPTable.put("JSUB",  new Operation("JSUB",  "3", "48"));
        _OPTable.put("LDA",   new Operation("LDA",   "3", "00"));
        _OPTable.put("LDB",   new Operation("LDB",   "3", "68"));
        _OPTable.put("LDCH",  new Operation("LDCH",  "3", "50"));
        _OPTable.put("LDF",   new Operation("LDF",   "3", "70"));
        _OPTable.put("LDL",   new Operation("LDL",   "3", "08"));
        _OPTable.put("LDS",   new Operation("LDS",   "3", "6C"));
        _OPTable.put("LDT",   new Operation("LDT",   "3", "74"));
        _OPTable.put("LDX",   new Operation("LDX",   "3", "04"));
        _OPTable.put("LPS",   new Operation("LPS",   "3", "D0"));
        _OPTable.put("MULF",  new Operation("MULF",  "3", "60"));
        _OPTable.put("MULR",  new Operation("MULR",  "2",   "98"));
        _OPTable.put("NORM",  new Operation("NORM",  "1",   "C8"));
        _OPTable.put("OR",    new Operation("OR",    "3", "44"));
        _OPTable.put("RD",    new Operation("RD",    "3", "D8"));
        _OPTable.put("RMO",   new Operation("RMO",   "2",   "AC"));
        _OPTable.put("RSUB",  new Operation("RSUB",  "3", "4C"));
        _OPTable.put("SHIFTL",new Operation("SHIFTL","2",   "A4"));
        _OPTable.put("SHIFTR",new Operation("SHIFTR","2",   "A8"));
        _OPTable.put("SIO",   new Operation("SIO",   "1",   "F0"));
        _OPTable.put("SSK",   new Operation("SSK",   "3", "EC"));
        _OPTable.put("STA",   new Operation("STA",   "3", "0C"));
        _OPTable.put("STB",   new Operation("STB",   "3", "78"));
        _OPTable.put("STCH",  new Operation("STCH",  "3", "54"));
        _OPTable.put("STF",   new Operation("STF",   "3", "80"));
        _OPTable.put("STI",   new Operation("STI",   "3", "D4"));
        _OPTable.put("STL",   new Operation("STL",   "3", "14"));
        _OPTable.put("STS",   new Operation("STS",   "3", "7C"));
        _OPTable.put("STSW",  new Operation("STSW",  "3", "E8"));
        _OPTable.put("STT",   new Operation("STT",   "3", "84"));
        _OPTable.put("STX",   new Operation("STX",   "3", "10"));
        _OPTable.put("SUB",   new Operation("SUB",   "3", "1C"));
        _OPTable.put("SUBF",  new Operation("SUBF",  "3", "5C"));
        _OPTable.put("SUBR",  new Operation("SUBR",  "2",   "94"));
        _OPTable.put("SVC",   new Operation("SVC",   "2",   "B0"));
        _OPTable.put("TD",    new Operation("TD",    "3", "E0"));
        _OPTable.put("TIO",   new Operation("TIO",   "1",   "F8"));
        _OPTable.put("TIX",   new Operation("TIX",   "3", "2C"));
        _OPTable.put("TIXR",  new Operation("TIXR",  "2",   "B8"));
        _OPTable.put("WD",    new Operation("WD",    "3", "DC"));
    }

    public static Operation getOperation(String mnemonic){
        return _OPTable.get(mnemonic);
    }

    public static String getMnemonic(String mnemonic){
        return _OPTable.get(mnemonic).mnemonic();
    }

    public static String getFormat(String mnemonic){
        return _OPTable.get(mnemonic).format();
    }

    public static String getOpcode(String mnemonic){
        return _OPTable.get(mnemonic).opcode();
    }

    public static Map<String, Operation> getOperaionTable() {
        return _OPTable;
    }
}
