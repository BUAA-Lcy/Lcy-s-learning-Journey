import java.io.IOException;
import CodeGeneration.*;
import front.*;
public class Compiler {

    public static void main(String[] args) throws IOException {
        Lexer.main(args);
        Syntax.main(args);
        CreateTree.main(args);
        LLVMGeneration.main(args);
        System.exit(0);
    }
}