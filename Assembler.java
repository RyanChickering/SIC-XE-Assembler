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

    public static void main(String[]args) throws IOException, invalidOPException, undefinedSymbolException{
        //Should provide cmd line argument to pass an input file to the assembler
        lineCnt = 0;
        try {
            pass1(args[0]);
        }
        catch(invalidOPException e){
            System.out.println("The opcode is invalid");
            System.exit(1);
        }
        catch(ErrorDuplicateLabelException e){
            System.out.println("Duplicate labels are found");
            System.exit(1);
        }
        pass2();
        //Deletes the intermediate file
        File intermediateFile = new File(System.getProperty("user.dir") + "/pass1Intermediate");
        intermediateFile.delete();
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
                    if(!opcode[0].equals(" ")){
                        throw new ErrorDuplicateLabelException();
                    }
                } else {
                    Label label = new Label(opcode[0],locctr);
                    symTable.add(label);
                }
                boolean extended = false;
                if(opcode[1].substring(0,1).equals("+")){
                    extended = true;
                }
                writeIntermediate(locctr, opcode);
                if(searchOPTABLE(opcode[1]) != null){
                    Operation opcodeInfo = searchOPTABLE(opcode[1]);
                    if(extended){
                        locctr += 4;
                    } else if(opcodeInfo.format().equals("2")){
                        locctr += 2;
                    } else if(opcodeInfo.format().equals("1")){
                        locctr += 1;
                    } else if(opcodeInfo.format().equals("3")){
                        locctr += 3;
                    }
                } else if(extended){
                    searchOPTABLE(opcode[1].substring(1));
                    locctr += 4;
                } else if(opcode[1].equals("WORD")){
                    locctr += 3;
                } else if(opcode[1].equals("RESW")){
                    locctr += 3*Integer.parseInt(opcode[2]);
                } else if(opcode[1].equals("RESB")){
                    locctr += Integer.parseInt(opcode[2]);
                    //TODO: Figure out how this works (also how word works)
                } else if(opcode[1].equals("BYTE")){
                    //find length of operand
                    int oplength;
                    String s = opcode[2].substring(opcode[2].indexOf("'") + 1, opcode[2].indexOf("''"));
                    oplength = s.length();
                    locctr += oplength;
                } else {
                    throw new invalidOPException();
                    //error not a real thing
                }
                int index;
                String name;
                String value;
                int length;
                if (opcode[2].contains("=")){
                    if (!LITTAB.search(opcode[2])){
                        index = opcode[2].indexOf('\'');
                        name = opcode[2].substring(1,index);
                        value = opcode[2].substring(index, opcode[2].lastIndexOf('\''));
                        length = value.length();
                        int i = Integer.parseInt(value);
                        LITTAB.add(name,i,length);
                    }
                }

                opcode = opcodeParser(nextLine());
            }
        }
        
        writeIntermediate(locctr, opcode);
        progLength = locctr - startLoc;
    }

    private static void pass2() throws IOException, undefinedSymbolException{
        lineCnt = 0;
        getLines(System.getProperty("user.dir") + "/pass1Intermediate");
        createListing();
        lineCnt = 0;
        String[] opCode = pass2Parser((nextLine()));
        if(opCode[1].equals("START")){
            locctr = Integer.parseInt(opCode[2]);
            opCode = pass2Parser(nextLine());
        }
        else{
            locctr = 0;
        }
        StringBuilder textRecord = new StringBuilder();
        int opNum = 0;
        int base = 0;
        int location = 0;
        int start = 0;
        while(!(opCode[1].equals("END"))){
            //Checks if comment
            //check to see if the opcode is in the optable
            if(searchOPTABLE(opCode[1]) != null){
                //check if the opcode if for a register to register function
                if(searchOPTABLE(opCode[1]).format().equals("2")){
                    //if it is, then convert the two registers into a single integer that
                    if(opCode[2].contains(",")) {
                        String part1 = opCode[2].substring(0,opCode[2].indexOf(","));
                        if(searchSYMTABLE(part1) != null){
                            location = searchSYMTABLE(part1).location;
                        } else if(getRegisterNum(part1) != -1){
                            location = getRegisterNum(part1)*10;
                        }
                        location += getRegisterNum(opCode[2].substring(opCode[2].indexOf(",")));
                    } else {
                        location = getRegisterNum(opCode[2]);
                    }
                }
                //check if there is a symbol in the operand field
                if(!opCode[2].equals("")) {
                    //check if there is an immediate, if there is convert it to an int
                    if(opCode[2].charAt(0) == '#'){
                        opCode[2] = opCode[2].substring(1);
                        location = hexToDec(opCode[2]);
                        //check if there is an indirect
                    } else if(opCode[2].charAt(0) == '='){

                    } else if(opCode[2].charAt(0) == '@'){

                        //if the thing is not an immediate or indirect, check the symtable to see if that symbol exists.
                    } else if(opCode[2].contains(",")){
                        String part1 = opCode[2].substring(0,opCode[2].indexOf(","));
                        if(searchSYMTABLE(part1) != null){
                            location = searchSYMTABLE(part1).location;
                        } else if(getRegisterNum(part1) != -1){
                            location = getRegisterNum(part1)*10;
                        }
                    } else if (searchSYMTABLE(opCode[2]) != null) {
                        location = searchSYMTABLE(opCode[2]).location;
                        //if the operand is not an immediate, register, indirect, or valid symbol, throw an exception
                    } else if(getRegisterNum(opCode[2])!= -1){
                      location = getRegisterNum(opCode[2]);
                    } else {
                        throw new undefinedSymbolException();
                    }
                    //if there is no operand, set the location to 0
                } else {
                    location = 0;
                }
                //conver the opcode and format of the opcode into their integer forms
                int opVal = hexToDec(opCode[1]);
                int format = Integer.parseInt(searchOPTABLE(opCode[1]).format());
                int programCount = hexToDec(opCode[3])+Integer.parseInt(searchOPTABLE(opCode[1]).format());
                //create a new object code based on the opcode, the operand value, the format, and the base
                ObjectCode objectCode = new ObjectCode(opVal,location,programCount, base, searchOPTABLE(opCode[1]).format(),opCode[2]);
                //as long as the text record hasn't exceeded it's length
                if(textRecord.length() < (59+opNum)){
                    if(textRecord.length() == 0){
                        start = hexToDec(opCode[3]);
                    }
                    //add the object code to the text record
                    textRecord.append("^");
                    textRecord.append(objectCode.printObjectCode());
                    //keep track of how many object codes have been added to the current record because the ^ creates a new character
                    opNum++;
                } else {
                    //if the text record will get too big, print out the line and start a new one
                    writeListing(textRecord.toString(), start, hexToDec(opCode[3]));
                    textRecord = new StringBuilder();
                    textRecord.append(objectCode.printObjectCode());
                    start = 0;
                    opNum = 0;
                }
                //check the base, if you have the base, update the base.
            } else if(opCode[1].equals("BASE")){
                if(searchSYMTABLE(opCode[3])!= null) {
                    base = (searchSYMTABLE(opCode[3]).location);
                }
                //TODO: Find out how this is supposed to work and do it
            } else if(opCode[1].equals("WORD")){
                opCode[2] = opCode[2].substring(opCode[2].indexOf("'"));
                opCode[2] += opCode[2].substring(0, opCode[2].indexOf("'"));
                textRecord.append(opCode[2]);
            } else if(opCode[2].equals("BYTE")){
                opCode[2] = opCode[2].substring(opCode[2].indexOf("'"));
                opCode[2] += opCode[2].substring(0, opCode[2].indexOf("'"));
                textRecord.append(opCode[2]);
            }
            opCode = pass2Parser(nextLine());
        }
        if(textRecord.length() > 0) {
            System.out.println(textRecord);
            writeListing(textRecord.toString(), start, hexToDec(opCode[3]));
        }
        writeEndRecord();
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
        if(out[0].indexOf(' ') == 0){
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
    //Method to create the listing file and initialize it with the header record
    private static void createListing() throws IOException {
        String filepath = System.getProperty("user.dir") + "/out.sic";
        PrintWriter printer = new PrintWriter(filepath, "UTF-8");
        StringBuilder string = new StringBuilder();
        String[] opcode = pass2Parser(nextLine());
        String out = String.format("%s%6s%s%s%s%s","H^", (opcode[0]),"^",padWith0s(opcode[3]),"^",padWith0s(decToHex(progLength)));
        printer.println(out);
        printer.close();
    }
    //Method that fills the front of a string with 0s because apparently string.format can't do that
    private static String padWith0s(String input){
        StringBuilder string = new StringBuilder("000000");
        if (input.length() < 6){
            string.append(input);
            string.delete(0,input.length());
        }
        return string.toString();
    }
    //method to write to the final listing file
    private static void writeListing(String opcodes, int start, int end) throws IOException{
        String filepath = System.getProperty("user.dir") + "/out.sic";
        FileWriter fw = new FileWriter(filepath,true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter printer = new PrintWriter(bw);
        StringBuilder string = new StringBuilder();
        string.append("T^");
        string.append(padWith0s(decToHex(start)));
        string.append("^");
        String hexLength = decToHex(end-start);
        string.append(hexLength);
        string.append(opcodes);
        string.append("\n");
        printer.append(string);
        printer.close();
    }
    //Method that finishes off the listing file by printing the ending record
    private static void writeEndRecord() throws IOException{
        String filepath = System.getProperty("user.dir") + "/out.sic";
        FileWriter fw = new FileWriter(filepath,true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter printer = new PrintWriter(bw);
        StringBuilder string = new StringBuilder();
        lineCnt = 0;
        String[] opcode = pass2Parser(nextLine());
        string.append("E^");
        string.append(padWith0s(opcode[0]));
        printer.append(string);
        printer.close();
    }
    //method that checks if an op code's mnemonic is in the OPTABLE
    private static Operation searchOPTABLE(String mnemonic){
        Operation tempOp = opTable.getOperation(mnemonic);
        if(tempOp != null) {
            if (mnemonic.equals(tempOp.mnemonic())) {
                return tempOp;
            } else {
                System.out.println("mnemonic not found");
                return null;
            }
        }
        return null;
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
            hexNum.insert(0,hexChars[remainder]);
            decimal /= 16;
        }
        return hexNum.toString();
    }
    //method to convert a register's name to it's value.
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