import Lex.Lexer;
import Syntax.SyntaxAnalyzer;

import java.io.IOException;

public class Compiler {
    public static boolean isDebug = true;
    public static void main(String[] args) throws IOException {
        Lexer.main(args);
        SyntaxAnalyzer.main(args);
    }
}