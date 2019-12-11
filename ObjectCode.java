public class ObjectCode {

    private static int opCode;
    private static int TA;
    private static int PC;
    private static int base;
    private static String format;
    private static String operand;
    private static boolean n;
    private static boolean i;
    private static boolean x;
    private static boolean b;
    private static boolean p;
    private static boolean e;


    public ObjectCode(){
        opCode = 0;
        TA = 0;
        PC = 0;
        base = 0;
        format = "";
        operand = "";
        n = false;
        i = false;
        x = false;
        b = false;
        p = false;
        e = false;
    }

    public ObjectCode(int opCode, int TA, int PC, int base, String format, String operand){
        this.opCode = opCode;
        this.TA = TA;
        this.PC = PC;
        this.base = base;
        this.format = format;
        this.operand = operand;
        n = false;
        i = false;
        x = false;
        b = false;
        p = false;
        e = false;
    }

    public static void setFlags(){
        if(format.equals("3")) {
            e = false;
            // checks if it is indirect
            if (operand.length() > 0) {
                if (operand.charAt(0) == '@') {
                    n = true;
                    i = false;
                    x = false;
                    // checks if it is a constant
                    if (Character.isDigit(operand.charAt(1))) {
                        b = false;
                        p = false;
                    } else {
                        // check if it is base or pc relative
                        if (TA - PC > 2048) {
                            PC = base;
                            b = true;
                            p = false;
                        } else {
                            b = false;
                            p = true;
                        }
                    }
                }
                // checks if immediate
                else if (operand.charAt(0) == '#') {
                    n = false;
                    i = true;
                    x = false;
                    // checks if it is a constant
                    if (Character.isDigit(operand.charAt(1))) {
                        b = false;
                        p = false;
                    } else {
                        // check if it is base or pc relative
                        if (TA - PC > 2048) {
                            PC = base;
                            b = true;
                            p = false;
                        } else {
                            b = false;
                            p = true;
                        }
                    }
                }
                // simple otherwise
                else {
                    n = true;
                    i = true;
                    // checks if c,X or m,X
                    if (operand.charAt(operand.length() - 1) == 'X') {
                        x = true;
                    } else {
                        x = false;
                    }
                    // checks if it is a constant
                    if (Character.isDigit(operand.charAt(0))) {
                        b = false;
                        p = false;
                    } else {
                        // check if it is base or pc relative
                        if (TA - PC > 2048) {
                            PC = base;
                            b = true;
                            p = false;
                        } else {
                            b = false;
                            p = true;
                        }
                    }
                }
            } else if (format.equals("4")) {
                e = true;
                b = false;
                p = false;
                // checks if it is indirect
                if (operand.charAt(0) == '@') {
                    n = true;
                    i = false;
                    x = false;
                }
                // checks if it is immediate
                else if (operand.charAt(0) == '#') {
                    n = false;
                    i = true;
                    x = false;
                }
                // simple otherwise
                else {
                    n = true;
                    i = true;
                    // checks if c,X or m,X
                    if (operand.charAt(operand.length() - 1) == 'X') {
                        x = true;
                    } else {
                        x = false;
                    }
                }
            }
        }
    }

    public static String printObjectCode(){
        setFlags();
        int intDisplay;
        String stringDisplay;
        String opCodeString;
        // if format 2
        if(format.equals("1")) {
            // converts opCode to hex and capitalizes
            opCodeString = Integer.toHexString(opCode).toUpperCase();
            // pads opCode to 2 digit
            return padWith2_0s(opCodeString);
        }
        else if(format.equals("2")){
            // address is just
            String hexDisplay = TA + "";
            String temp = padWith2_0s(hexDisplay);
            stringDisplay = temp.toUpperCase();
            opCodeString = Integer.toHexString(opCode).toUpperCase();
            return padWith2_0s(opCodeString) + stringDisplay;
        }
        else if(format.equals("3")){
            // n = 0, i = 1
            if(!n && i){
                opCode = opCode + 1;
            }
            // n = 1, i = 0
            else if(n && !i){
                opCode = opCode + 2;
            }
            // n = 1, i = 1
            else if(n && i) {
                opCode = opCode + 3;
            }
            intDisplay = TA - PC;
            if(!n && i && !p && !b){
                intDisplay = TA;
            }
            String hexDisplay = Integer.toHexString(intDisplay);
            if(hexDisplay.charAt(0) == 'f'){
                hexDisplay = until6Shaver(hexDisplay);
            }
            String temp = padWith4_0s(hexDisplay);
            stringDisplay = temp.substring(1).toUpperCase();
            String binaryNumber = flagConverter();
            opCodeString = Integer.toHexString(opCode).toUpperCase();
            return padWith2_0s(opCodeString) + binToHex(binaryNumber).toUpperCase() + stringDisplay;
        }
        else if(format.equals("4")){
            // n = 0, i = 0
            if(!n && !i){
                //do nothing
            }
            // n = 0, i = 1
            else if(!n && i){
                opCode = opCode + 1;
            }
            // n = 1, i = 0
            else if(n && !i){
                opCode = opCode + 2;
            }
            // n = 1, i = 1
            else {
                opCode = opCode + 3;
            }
            intDisplay = TA - PC;
            if(!n && i && !p && !b){
                intDisplay = TA;
            }
            String hexDisplay = Integer.toHexString(intDisplay);
            String temp = padWith4_0s(hexDisplay);
            stringDisplay = temp.toUpperCase();
            opCodeString = Integer.toHexString(opCode).toUpperCase();
            return padWith2_0s(opCodeString) + binToHex(flagConverter()).toUpperCase() + "0" + stringDisplay;
        }
        return null;
    }

    private static String flagConverter(){
        String binary = "";
        if(!x) { binary += "0"; }
        else if (x) { binary += "1"; }
        if(!b) { binary += "0"; }
        else if (b) { binary += "1"; }
        if(!p) { binary += "0"; }
        else if (p) { binary += "1"; }
        if(!e) { binary += "0"; }
        else if (e) { binary += "1"; }

        return binary;
    }

    private static String padWith2_0s(String input){
        StringBuilder string = new StringBuilder("00");
        if (input.length() < 2){
            string.append(input);
            string.delete(0,input.length());
            return string.toString();
        }
        return input;
    }

    private static String padWith4_0s(String input){
        StringBuilder string = new StringBuilder("0000");
        if (input.length() < 4){
            string.append(input);
            string.delete(0,input.length());
            return string.toString();
        }
        return input;
    }

    private static String until6Shaver(String string){
        string = string.substring(string.length() - 4,string.length());
        return string;
    }

    private static String binToHex(String binary){
        int decimal = Integer.parseInt(binary,2);
        return Integer.toHexString(decimal);
    }
}
