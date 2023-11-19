import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class MyError {

    public static BufferedWriter errorbw;

    static {
        try {
            errorbw = new BufferedWriter(new FileWriter("myerror.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void myerror (Lexical_unit realUnit,String ErrorGrammarSpace) throws IOException {
        System.out.println("IN MyError");
        System.out.println("Error Index: "+ Syntax.UnitIndex);
        System.out.println("Syntax error: line "+ realUnit.linenum);
        System.out.println("Error value: "+ realUnit.value);
        System.out.println("Error token: "+ realUnit.token);
        System.out.println("Error Grammar: "+ ErrorGrammarSpace);
        System.exit(0);

        errorbw.write("Error Index: "+ Syntax.UnitIndex);
        errorbw.newLine();
        errorbw.write("Syntax error: line "+ realUnit.linenum);
        errorbw.newLine();
        errorbw.write("Error value: "+ realUnit.value);
        errorbw.newLine();
        errorbw.write("Error token: "+ realUnit.token);
        errorbw.newLine();
        errorbw.write("Error Grammar: "+ ErrorGrammarSpace);
        errorbw.flush();



    }


}
