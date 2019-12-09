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
        if(format.equals("3")){
            e = false;
            // checks if it is indirect
            if(operand.charAt(0) == '@') {
                n = true;
                i = false;
                x = false;
                // checks if it is a constant
                if (Character.isDigit(operand.charAt(1))) {
                    b = false;
                    p = false;
                }
                else {
                    // checks for base or pc relative
                    if(TA - PC > 2048){
                        PC = base;
                        b = true;
                        p = false;
                    }
                    else{
                        b = false;
                        p = true;
                    }
                }
            }
            // checks if immediate
            else if(operand.charAt(0) == '#'){
                n = false;
                i = true;
                x = false;
                // checks if it is a constant
                if(Character.isDigit(operand.charAt(1))){
                    b = false;
                    p = false;
                }
                else {
                    // checks for base or pc relative
                    if(TA - PC > 2048){
                        PC = base;
                        b = true;
                        p = false;
                    }
                    else{
                        b = false;
                        p = true;
                    }
                }
            }
            // simple otherwise
            else{
                n = true;
                i = true;
                // checks if c,X or m,X
                if(operand.charAt(operand.length()-1) == 'X'){
                    x = true;
                }
                else{
                    x = false;
                    // checks if it is a constant
                    if(Character.isDigit(operand.charAt(0))){
                        b = false;
                        p = false;
                    }
                    else{
                        // checks for base or pc relative
                        if(TA - PC > 2048){
                            PC = base;
                            b = true;
                            p = false;
                        }
                        else{
                            b = false;
                            p = true;
                        }
                    }
                }
            }
        }
        else if(format.equals("4")){
            e = true;
            b = false;
            p = false;
            // checks if it is indirect
            if(operand.charAt(0) == '@') {
                n = true;
                i = false;
                x = false;
            }
            // checks if it is immediate
            else if(operand.charAt(0) == '#'){
                n = false;
                i = true;
                x = false;
            }
            // simple otherwise
            else{
                n = true;
                i = true;
                // checks if c,X or m,X
                if(operand.charAt(operand.length()-1) == 'X'){
                    x = true;
                }
                else{
                    x = false;
                }
            }
        }
    }

    public static String printObjectCode(){
        setFlags();
        int intDisplay;
        String stringDisplay;
        if(format.equals("1")) {
            return decToHex(opCode);
        }
        else if(format.equals("2")){
            return decToHex(opCode) + " " + " ";
            //TODO: add r1 and r2 to the end
        }
        else if(format.equals("3")){
            // n = 0, i = 0
            if(n == false && i == false){
                //do nothing
            }
            // n = 0, i = 1
            else if(n == false && i == true){
                opCode = opCode + 1;
            }
            // n = 1, i = 0
            else if(n == true && i == false){
                opCode = opCode + 2;
            }
            // n = 1, i = 1
            else if(n == true && i == true) {
                opCode = opCode + 3;
            }
            intDisplay = TA - PC;
            stringDisplay = decToHex(intDisplay).substring(1);
            return decToHex(opCode) + binToHex(flagConverter()) + stringDisplay;
        }
        else if(format.equals("4")){
            // n = 0, i = 0
            if(n == false && i == false){
                //do nothing
            }
            // n = 0, i = 1
            else if(n == false && i == true){
                opCode = opCode + 1;
            }
            // n = 1, i = 0
            else if(n == true && i == false){
                opCode = opCode + 2;
            }
            // n = 1, i = 1
            else if(n == true && i == true) {
                opCode = opCode + 3;
            }
            intDisplay = TA - PC;
            stringDisplay = decToHex(intDisplay);
            return decToHex(opCode) + binToHex(flagConverter()) + "0" + stringDisplay;
        }
        return null;
    }

    private static String flagConverter(){
        String binary = "";
        if(x = false) { binary += "0"; }
        else if (x = true) { binary += "1"; }
        if(b = false) { binary += "0"; }
        else if (b = true) { binary += "1"; }
        if(p = false) { binary += "0"; }
        else if (p = true) { binary += "1"; }
        if(e = false) { binary += "0"; }
        else if (e = true) { binary += "1"; }

        return binary;
    }

    private static int hexToDec(String hex){
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        int decimal = 0;
        for(int i = hex.length()-1; i >= 0; i--){
            for(int j = 0; j < hexChars.length; j++){
                if(hex.charAt(i) == hexChars[j]){
                    decimal += j*Math.pow(16,hex.length()-i-1);
                    break;
                }
            }
        }
        return decimal;
    }

    //method to convert a decimal integer into a hex string
    private static String decToHex(int decimal){
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        int remainder;
        StringBuilder hexNum = new StringBuilder();
        while(decimal > 0){
            remainder = decimal%16;
            hexNum.append(hexChars[remainder]);
            decimal /= 16;
        }
        return hexNum.toString();
    }

    private static String binToHex(String binary){
        int decimal = 0;
        for(int i = binary.length()-1; i >= 0; i--){
            if(binary.charAt(i) == '1'){
                decimal += Math.pow(2,binary.length()-i-1);
            }
        }
        return decToHex(decimal);
    }
}
