import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;

public class Assembler {
    private static int locctr;
    private static OPTable opTable = new OPTable();
    private static ArrayList<Label> symTable = new ArrayList<>();
    private static List<String> lines;
    private static int lineCnt;

    public static void main(String[]args) throws IOException{
        //Should provide cmd line argument to pass an input file to the assembler
        lineCnt = 0;
        pass1(args[0]);
        pass2();
    }

    private static void pass1(String filename) throws IOException{
        //TODO: read first input line
        getLines(filename);
        String[] opcode = opcodeParser(nextLine());
        if(opcode[1].equals("START")){
            System.out.println(opcode[0]+ " " + opcode[1] + " " + opcode[2]);
            locctr = Integer.parseInt(opcode[2]);
            writeIntermediate(opcode);
            opcode = opcodeParser(nextLine());
        } else {
            locctr = 0;
        }
        while (!opcode[1].equals("END")){
            opcode = opcodeParser(nextLine());
            /*TODO:
            if not a comment line
                if there is a symbol in the label field
                    search SYMTABLE for label
                    if found
                        set error flag(duplicate symbol)
                    else
                        insert (Label,locctr) into SYMTAB
                search OPTAB for OPCODE
                if found then
                    add 3 {instruction length} to locctr
                else if OPCODE = WORD
                    add 3 to locctr
                else if OPCODE = RESW
                    add 3*#[OPERAND] to LOCCTR
                else if OPCODE = RESB
                    add #[Operand] to locctr
                else if OPCODE = BYTE
                    find length of constant in bytes
                    add length to locctr
                else
                    set error flag(invalid opcode)
            write line to intermediate file
            read next input line
            */
        }
        //write last line to intermediate file
        //save locctr - starting address as program length
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
        String filepath = System.getProperty("user.dir") + "/pass1Intermediate";        //creates ands to an intermediate file
        PrintWriter printer = new PrintWriter(filepath, "UTF-8");                   //doesn't write if the op code is null
        if (opcode[0] == (null)){
            return false;
        }
        printer.println(String.format("%8s%8s%8s",opcode[0],opcode[1],opcode[2]));
        printer.close();
        return true;
    }
    private static Operation searchOPTABLE(String mnemonic){
        Operation tempOp = opTable.get(mnemonic);
        if(mnemonic == tempOp.mnemonic()){
            return tempOp;
        }
        else{
            System.out.println("mnemonic not found");
            return null;
        }
    }
    private static Label searchSYMTABLE(String compare){
        for (Label label: symTable){                        //searches the SYM table and returns the label if
            if(label.name.equals(compare)){                 // the  label name is equal to the search string
              return label;
            }
        }
        return null;
    }
}
// This is a test comment