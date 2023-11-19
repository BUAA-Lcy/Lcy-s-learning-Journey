package CodeGeneration;
import front.FuncTable;
import front.SymbolTable;
import front.Syntax;
import front.SyntaxFunction;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.HashMap;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class LLVMGeneration {
    TreeNode AsTree ;
    HashMap<String,FuncTable> GlobalFuncMap;
    SymbolTable GlobalSymbolTable;
    //遍历过的终结符的数目
    public static int TerminalIndex = 0;
    public static BufferedWriter  LLVMDebugbw;

    static {
        try {
            LLVMDebugbw = new BufferedWriter(new FileWriter("LLVMDebug.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LLVMGeneration() {
        AsTree = new TreeNode("AST"); // 创建新的 TreeNode 对象并赋值给 AsTree
        AsTree.addChild(CreateTree.Root); // 添加 CreateTree.Root 作为 AsTree 的子节点
        GlobalSymbolTable = CreateTree.GlobalSymbolTable;
        GlobalFuncMap = SyntaxFunction.FuncTableMap;
    }
    public static void main(String[] args) throws IOException {
        System.out.println("LLVMGeneration analysis has begun.");
        LLVMGeneration llvmGeneration = new LLVMGeneration();
        //输出到LevelTable.txt
        SymbolTable.printToFile(llvmGeneration.GlobalSymbolTable);
        //输出到Tree.txt
        llvmGeneration.AsTree.printNode();
        //打印GlobalFuncMap到控制台上
//        for (String key : llvmGeneration.GlobalFuncMap.keySet()) {
//            System.out.println(llvmGeneration.GlobalFuncMap.get(key));
//        }

        // 生成LLVM代码并写入文件
        StringBuilder llvmCode = new StringBuilder();
        llvmGeneration.generateLLVMCode(llvmGeneration.AsTree, llvmCode);
        LLVMwriteToFile(llvmCode.toString(), "llvm_ir.txt");

        System.out.println("TerminalIndex: "+TerminalIndex+"\n");

        System.out.println("LLVMGeneration analysis is over.");
    }
    private void generateLLVMCode(TreeNode node, StringBuilder llvmCode) throws IOException{
        switch (node.type) {
            // 根据节点类型生成LLVM代码
            case "CompUnit":
                // 生成全局变量的LLVM代码
                llvmCode.append("declare i32 @getint()\n" );
                llvmCode.append("declare void @putint(i32)\n" );
                llvmCode.append("declare void @putch(i32)\n" );
                llvmCode.append("declare void @putstr(i8*)\n" );
                break;
            case "FuncDef":
                // 生成函数定义的LLVM代码
                llvmCode.append(generateFunctionDefinition(node));
                llvmCode.append("\n");
                break;
            // ... 其他节点类型的处理

            // 递归处理子节点
            default:
                break;
        }
//        LLVMDebugbw.write("node type: "+node.type +"   terminator: "+node.terminator+ "\n");
//        LLVMDebugbw.flush();
        if(node.terminator == 1){
            TerminalIndex++;
        }
        for (TreeNode child : node.children) {
            generateLLVMCode(child, llvmCode);
        }
    }

    private static String generateFunctionDefinition (TreeNode node) {
        // 根据函数节点生成LLVM代码并返回
        // 示例: "define ... { ... }\n"
        return "define\n";
    }

    private static void LLVMwriteToFile(String code, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(code);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
