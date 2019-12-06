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
        pass1(args[0]);
        pass2();
    }

    //Pass 1 looks through the original input and makes sure that all symbols and operations are legitimate.
    private static void pass1(String filename) throws IOException{
        //gets the lines fromt he file provided as an argument
        getLines(filename);
        String[] opcode = opcodeParser(nextLine());
        //checks if there is a different start location than 0
        if(opcode[1].equals("START")){
            System.out.println(opcode[0]+ " " + opcode[1] + " " + opcode[2]);
            locctr = Integer.parseInt(opcode[2]);
            writeIntermediate(opcode);
            opcode = opcodeParser(nextLine());
        } else {
            locctr = 0;
        }
        int startLoc = locctr;
        //analyzes the opcodes in the file to make sure that they are all real
        while (!opcode[1].equals("END")){
            opcode = opcodeParser(nextLine());
            //checks if the line is a comment
            if(opcode[0].equals("comment")){

            } else {
                //checks the labels
                if(searchSYMTABLE(opcode[0]) != null){
                    //TODO: et error duplicate label
                } else {
                    Label label = new Label(opcode[0],locctr);
                    symTable.add(label);
                }
                boolean extended = false;
                if(opcode[1].substring(0,1).equals("+")){
                    opcode[1] = opcode[1].substring(1);
                    extended = true;
                }
                Operation opcodeInfo = searchOPTABLE(opcode[1]);
                if(opcodeInfo != null){
                    if(extended){
                        locctr += 12;
                    } else {
                        if(true);
                        //set up some way to differentiate the different formats and add them to the locctr.
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
                    //error not a real thing
                }
                writeIntermediate(opcode);
            }
        }
        progLength = locctr - startLoc;
    }

    private static void pass2(){
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
        return out;
    }
    //Method to skip spaces and reach the start of actual lines
    private static String spaceIterator(String string){
        int i = 0;
        while(string.substring(i,i+1).equals(" ")){
            i++;
        }
        return string.substring(i);
    }

    private static boolean writeIntermediate(String[] opcode) throws IOException{
        String filepath = System.getProperty("user.dir") + "/pass1Intermediate";
        PrintWriter printer = new PrintWriter(filepath, "UTF-8");
        printer.println(String.format("%8s%8s%8s",opcode[0],opcode[1],opcode[2]));
        return true;
    }

    //Method to write out object program onto new file
    private static boolean writeOutput(String[] opcode) throws FileNotFoundException, UnsupportedEncodingException {
        String filepath = System.getProperty("user.dir") + "/objectProgram";
        PrintWriter printer = new PrintWriter(filepath, "UTF-8");
        printer.println(String.format("H%5s   00%4s",opcode[0], opcode[2]));
        printer.println(String.format("T%6s   00"));

        return true;
    }

    private static Operation searchOPTABLE(String mnemonic){
        Operation tempOp = opTable.getOperation(mnemonic);       //creates new operation object using mnemonic as key
        if(mnemonic == tempOp.mnemonic()){              //compares mnemonic with operation mnemonic
            return tempOp;
        }
        else{
            System.out.println("mnemonic not found");
            return null;
        }
    }
    private static Label searchSYMTABLE(String compare){
        for (Label label: symTable){
            if(label.name.equals(compare)){
              return label;
            }
        }
        return null;
    }
}
// This is a test comment