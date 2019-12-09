public class RegisterTable {
    public static int getRegisterNum(String register) {
        if (register.equals("A")) {
            return 0;
        }
        if (register.equals("X")) {
            return 1;
        }
        if (register.equals("L")) {
            return 2;
        }
        if (register.equals("PC")) {
            return 8;
        }
        if (register.equals("SW")) {
            return 9;
        }
        if (register.equals("B")) {
            return 3;
        }
        if (register.equals("S")) {
            return 4;
        }
        if (register.equals("T")) {
            return 5;
        }
        if (register.equals("F")) {
            return 6;
        }
        return -1;
    }

}
