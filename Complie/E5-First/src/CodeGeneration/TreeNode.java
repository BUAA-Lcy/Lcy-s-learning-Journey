package CodeGeneration;

import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class TreeNode {

    public String type;
    public List<TreeNode> children = new ArrayList<TreeNode>();
    public int terminator = -1;
    public static BufferedWriter Treebw;

    static {
        try {
            Treebw = new BufferedWriter(new FileWriter("Tree.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public TreeNode(String content) {
        this.type = content;
    }
    public TreeNode(String content,int terminator) {
        this.type = content;
        this.terminator = terminator;
    }
    public void addChild(TreeNode child) {
        children.add(child);
    }
    public void printNode() throws IOException{
        printNodeHelper("", true);
        Treebw.flush();
    }

    // 递归辅助方法
    private void printNodeHelper (String prefix, boolean isTail) throws IOException{
        Treebw.write(prefix + (isTail ? "└── " : "├── ") + type);
        Treebw.newLine();
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).printNodeHelper(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1)
                    .printNodeHelper(prefix + (isTail ?"    " : "│   "), true);
        }
    }
    // 后序遍历打印树节点
    public void printPostOrder() throws IOException {
        printPostOrderHelper(this);
        Treebw.flush();
    }

    // 递归辅助方法
    private void printPostOrderHelper(TreeNode node) throws IOException {
        if (node != null) {
            // 首先遍历所有子节点
            for (TreeNode child : node.children) {
                printPostOrderHelper(child);
            }
            // 然后处理当前节点
            if(!node.type.equals("BlockItem"))
                if(!node.type.equals("Decl"))
                    if(!node.type.equals("BType"))
                        Treebw.write(node.type + "\n");

        }else{
            Treebw.write("null\n");
        }

    }

}
