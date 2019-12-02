import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.nio.file.Files;

public class Assembler {
    private static int locctr;
    private static String[] opTABLE;
    private static String[] symTABLE;

    public static void main(String[]args){
        //Should provide cmd line argument to pass an input file to the assembler
        pass1(args[0]);
        pass2();

    }

    private static void pass1(String filename) {
        //TODO: read first input line
        String opcode = "";
        if(!opcode.equals("START")){
            while(opcode.equals("END")) {
                //TODO:
                /* Save #[Operand] as starting address
                initialize loccctr to starting address
                write line to intermediate file
                read next input line
             */
            }
        } else {
            locctr = 0;
        }
        while (!opcode.equals("END")){
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

    private static String getInLine(){
        return "WambdaLamdba";
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
