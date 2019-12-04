import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;

public class Assembler {
    private static int locctr;
    private static OPTable opTable;
    private static String[] symTABLE;
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
        String opcode = opcodeParser(nextLine());
        if(!opcode.equals("START")){
            System.out.println(opcode);
            //while(opcode.equals("END")) {
            //TODO:
                /* Save #[Operand] as starting address
                initialize loccctr to starting address
                write line to intermediate file
                read next input line
             */
            //}
        } else {
            locctr = 0;
        }
        //while (!opcode.equals("END")){
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
        //}
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
    //method that gets the opcode isolated from spaces/labels that might be preceding it on a line
    private static String opcodeParser(String line){
        String out = line;
        if(out.indexOf(' ') == 0){
            out = spaceIterator(line);
        } else {
            int i = 0;
            while(!out.substring(i,i+1).equals(" ")){
                i++;
            }
            out = spaceIterator(out.substring(i));
        }
        int space = out.indexOf(' ');
        out = out.substring(0,space);
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

    private static boolean writeLine(){
        return false;
    }
    private static boolean searchOPTABLE(){
        return false;
    }
    private static boolean searchSYMTABLE(){
        return false;
    }
}
