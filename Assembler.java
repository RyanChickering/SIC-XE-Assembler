import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.nio.file.Files;

public class Assembler {
    private static int locctr;
    private static OPTable opTable = new OPTable();
    private static ArrayList<Label> symTable = new ArrayList<>();
    private static List<String> lines;
    private static int lineCnt;
    private static int progLength;

    public static void main(String[]args) throws IOException{
        //Should provide cmd line argument to pass an input file to the assembler
        lineCnt = 0;
        try {
            pass1(args[0]);
        }
        catch(invalidOPException e){
            System.out.println("The opcode is invalid you fuck");
            System.exit(1);
        }
        catch(ErrorDuplicateLabelException e){
            System.out.println("Duplicate labels are found");
            System.exit(1);
        }
        try {

            pass2();
            //Deletes the intermediate file
            File intermediateFile = new File(System.getProperty("user.dir") + "/pass1Intermediate");
            //intermediateFile.delete();
        }
        catch(undefinedSymbolException e){
            System.out.println("Symbol is undefined you fuck");
            System.exit(1);
        }
    }

    //Pass 1 looks through the original input and makes sure that all symbols and operations are legitimate.
    private static void pass1(String filename) throws IOException,invalidOPException, ErrorDuplicateLabelException{
        //gets the lines from the file provided as an argument
        getLines(filename);
        String[] opcode = opcodeParser(nextLine());
        //checks if there is a different start location than 0
        if(opcode[1].equals("START")){
            locctr = hexToDec(opcode[2]);
            writeIntermediate(locctr, opcode);
            opcode = opcodeParser(nextLine());
        } else {
            locctr = 0;
        }
        int startLoc = locctr;
        //analyzes the opcodes in the file to make sure that they are all real
        while (!opcode[1].equals("END")){
            //checks if the line is a comment
            if(opcode[0] != null && opcode[0].equals("comment")){

            } else {
                //checks the labels
                if(opcode[0] != null && searchSYMTABLE(opcode[0]) != null){
                    throw new ErrorDuplicateLabelException();
                } else {
                    Label label = new Label(opcode[0],locctr);
                    symTable.add(label);
                }
                boolean extended = false;
                if(opcode[1].substring(0,1).equals("+")){
                    opcode[1] = opcode[1].substring(1);
                    extended = true;
                }
                writeIntermediate(locctr, opcode);
                Operation opcodeInfo = searchOPTABLE(opcode[1]);
                if(opcodeInfo != null){
                    if(extended){
                        locctr += 12;
                    } else if(opcodeInfo.format().equals("2")){
                        locctr += 6;
                    } else if(opcodeInfo.format().equals("1")){
                        locctr += 3;
                    } else if(opcodeInfo.format().equals("3/4")){
                        locctr += 9;
                    }
                } else if(opcode[1].equals("WORD")){
                    locctr += 3;
                } else if(opcode[1].equals("RESW")){
                    locctr += 3*Integer.parseInt(opcode[3]);
                } else if(opcode[1].equals("RESB")){
                    locctr += Integer.parseInt(opcode[3]);
                } else if(opcode[1].equals("BYTE")){
                    //find length of operand
                    locctr += opcode[3].length();
                } else {
                    throw new invalidOPException();
                    //error not a real thing
                }
                if (opcode[2].contains("=")){
                    if (!LITTAB.search(opcode[2])){
                        
                    }
                }
                opcode = opcodeParser(nextLine());
            }
        }
        progLength = locctr - startLoc;
    }

    private static void pass2() throws IOException, undefinedSymbolException{
        int operandLoc;
        lineCnt = 0;
        getLines(System.getProperty("user.dir") + "/pass1Intermediate");
        String[] opCode = pass2Parser((nextLine()));
        if(opCode[1].equals("START")){
            writeListing(opCode);
            locctr = Integer.parseInt(opCode[2]);
            opCode = opcodeParser(nextLine());
        }
        else{
            locctr = 0;
        }
        while(!(opCode[1].equals("END"))){
            opCode = opcodeParser(nextLine());
            //Checks if comment
            if(searchOPTABLE(opCode[1]) != null){
                if(opCode[2] != null) {
                    if (searchSYMTABLE(opCode[0]) != null) {
                        //store symbol value as operand address
                        //operandLoc = opCode[1];
                    } else {
                        operandLoc = 0;
                        throw new undefinedSymbolException();
                    }
                } else {
                    operandLoc = 0;
                }
                //TODO: Assemble object code instruction method
            }
            else{
                operandLoc = 0;
            }
        }
        /*TODO
        read first input line
        if opcode = start
            write listing line
            read next input line
        write header record to object program
        initialize first text record
        while opcode != end
            if this is not a comment line
                search OPTAB for OPCODE
                if found
                    if there is a symbol in the operand field then
                        search SYMTAB for operand
                        if found
                            store symbol value as operand address
                        else
                            store 0 as operand address
                            set error flag(undefined symbol)
                    else
                        store 0 as operand address
                    assemble object code instruction
                else if OPCODE = byte or word then
                    convert constant to object code
                if object code will not fit into the current text record then
                    write text record to object program
                    initialize new text record
                add object code to text record
            write listing line
            read next input line
        write last text record to object program
        write end record to object program
        write last listing line


         */
    }

    //method that reads in all the lines from a file
    private static void getLines(String filename) throws IOException{
        File infile = new File(filename);
        if(infile.exists()) {
            lines = Files.readAllLines(infile.toPath(), Charset.defaultCharset());
        }
        else{
            System.out.println("No such file found at " + filename);
        }
    }

    //TODO: Method that calculates object codes based on info from intermediate file
    private static void objectCoder(String opcode, String operand){
        String out;
        Operation opInfo = searchOPTABLE(opcode);
        int opValue = Integer.parseInt(opInfo.opcode(), 16);

    }

    //Method that gets the next line of the program
    private static String nextLine(){
        String out = lines.get(lineCnt);
        lineCnt++;
        return out;
    }
    //method that gets each part of a line isolated. Returns an array of strings with
    //label,opcode,value.
    private static String[] opcodeParser(String line){
        String[] out = new String[3];
        out[0] = line; out[1] = line; out[2] = line;
        //if the line starts with a space(not a label) skip spaces until you hit something
        if(out[1].indexOf(' ') == 0){
            out[1] = spaceIterator(line);
            out[0] = null;
        } else {
            int i = 0;
            //see how long the label is, then use that to create the proper substrings
            while(!(out[1].charAt(i) == (' '))){
                i++;
            }
            out[0] = out[0].substring(0,i);
            out[1] = spaceIterator(out[1].substring(i));
        }
        //check if there is more content after the opcode, if there is, assign it to out[2]
        int space = out[1].indexOf(' ');
        if(space != -1) {
            out[2] = spaceIterator(out[1].substring(space));
            out[1] = out[1].substring(0, space);
        } else {
            out[2] = null;
        }
        //check for comments
        for(int i = 0; i < 3; i++) {
            if (out[i] != null) {
                if (out[i].contains(".")) {
                    if(out[i].indexOf(' ') < out[i].indexOf('.')){
                        out[i] = out[i].substring(0, out[i].indexOf(' '));
                    } else {
                        out[i] = out[i].substring(0, out[i].indexOf('.'));
                    }
                }
            }
        }
        return out;
    }

    //Parses opcodes out of the intermediate file.
    //label,opcode,operand,address
    private static String[] pass2Parser(String line){
        String[] out = new String[4];
        out[3] = spaceShaver(line.substring(0,4));
        out[0] = spaceShaver(line.substring(6,14));
        out[1] = spaceShaver(line.substring(14,22));
        out[2] = spaceShaver(line.substring(22));
        return out;
    }
    //Method that removes extra spaces from the end of stuff being read in from the intermediate
    private static String spaceShaver(String string){
        while(string.indexOf(' ') != -1){
            string = string.substring(0,string.length()-1);
        }
        return string;
    }
    //Method to skip spaces and reach the start of actual lines
    private static String spaceIterator(String string){
        int i = 0;
        while(string.substring(i,i+1).equals(" ")){
            i++;
        }
        return string.substring(i);
    }

    //method that writes the intermediate file
    private static void writeIntermediate(int location, String[] opcode) throws IOException{
        String filepath = System.getProperty("user.dir") + "/pass1Intermediate";
        FileWriter fw = new FileWriter(filepath,true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter printer = new PrintWriter(bw);
        if(opcode[0] == null){
            opcode[0] = " ";
        }
        if(opcode[2] == null) {
            opcode[2] = " ";
        }
        String address = decToHex(location);
        StringBuilder string = new StringBuilder("0000");
        if (address.length() < 4){
            string.append(address);
            string.delete(0,address.length());
        }
        printer.append(String.format("%s  %-8s%-8s%-8s\n",string.toString(), opcode[0],opcode[1],opcode[2]));
        printer.close();
    }

    //method to write to the final listing file
    private static boolean writeListing(String[] opcode) throws IOException{
        String filepath = System.getProperty("user.dir") + "/pass2Listing";        //creates ands to an intermediate file
        PrintWriter printer = new PrintWriter(filepath, "UTF-8");
        if (opcode[1].equals("RESW")|| (opcode[1].equals("START")) || (opcode[1].equals("END"))){
            opcode[1] = " ";
        }
        printer.println(String.format("%8s%8s%8s",opcode[0],opcode[1],opcode[2]));
        printer.close();
        return true;
    }

    //method that checks if an op code's mnemonic is in the OPTABLE
    private static Operation searchOPTABLE(String mnemonic){
        Operation tempOp = opTable.getOperation(mnemonic);
        if(mnemonic.equals(tempOp.mnemonic())){
            return tempOp;
        }
        else{
            System.out.println("mnemonic not found");
            return null;
        }
    }

    //method that checks if a label is in the SYMTABLE
    private static Label searchSYMTABLE(String compare){
        if(!(symTable.size() == 0)){
            for (Label label : symTable) {
                if (label.name!= null && label.name.equals(compare)) {
                    return label;
                }
            }
        }
        return null;
    }

    //method to convert a hex number represented by a string to it's integer value
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
}