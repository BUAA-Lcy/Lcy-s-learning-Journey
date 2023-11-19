package CodeGeneration;
import front.FuncTable;
import front.SymbolTable;
import front.Syntax;
import front.SyntaxFunction;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.HashMap;

public class LLVMGeneration {
    TreeNode AsTree ;
    HashMap<String,FuncTable> GlobalFuncMap;
    SymbolTable GlobalSymbolTable;
    public LLVMGeneration() {
        AsTree = new TreeNode("AST"); // 创建新的 TreeNode 对象并赋值给 AsTree
        AsTree.addChild(CreateTree.Root); // 添加 CreateTree.Root 作为 AsTree 的子节点
        GlobalSymbolTable = CreateTree.GlobalSymbolTable;
        GlobalFuncMap = SyntaxFunction.FuncTableMap;
    }
    public static void main(String[] args) throws IOException {
        System.out.println("LLVMGeneration analysis has begun.");
        LLVMGeneration llvmGeneration = new LLVMGeneration();
        SymbolTable.printToFile(llvmGeneration.GlobalSymbolTable);
        llvmGeneration.AsTree.printNode();
        //打印GlobalFuncMap到控制台上
        for (String key : llvmGeneration.GlobalFuncMap.keySet()) {
            System.out.println(llvmGeneration.GlobalFuncMap.get(key));
        }
        System.out.println("LLVMGeneration analysis is over.");
    }
}
