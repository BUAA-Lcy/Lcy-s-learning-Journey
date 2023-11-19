//author:Lcy
import java.io.IOException;
public class Compiler {

    public static void main(String[] args) throws IOException {
        Lexer.main(args);
        Syntax.main(args);
    }
}