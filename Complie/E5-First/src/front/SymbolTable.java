package front;


import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;
public class SymbolTable {
    public int Level;
    public static BufferedWriter SymbolTableWriter;

    static {
        try {
            SymbolTableWriter = new BufferedWriter(new FileWriter("SymbolTable.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SymbolTable ParentSymbolTable = null;
    public List<SymbolTable> childSymbolTable = new ArrayList<SymbolTable>();
    Map<String, Symbol> SymbolMap = new HashMap<String, Symbol>();
    public SymbolTable(SymbolTable parentSymbolTable,int level) {
        this.ParentSymbolTable = parentSymbolTable;
        this.Level = level;
        if(parentSymbolTable!=null)parentSymbolTable.childSymbolTable.add(this);
    }
    public void addSymbol(Symbol symbol) {
        this.SymbolMap.put(symbol.name, symbol);
    }

    //返回可以查找到name的最高level的SymbolTable
    public static Symbol LookUpSymbol(SymbolTable table, String name) {
        if (table == null) {
            return null; // 没有符号表可搜索，所以直接返回 null
        }

        // 在当前符号表中查找
        Symbol found = table.SymbolMap.get(name);
        if (found != null) {
            return found;
        }

        // 如果没有找到，并且有父级符号表，则在父级中递归查找
        return LookUpSymbol(table.ParentSymbolTable, name);
    }


    public void printTableToFile(BufferedWriter writer, int indentLevel) throws IOException {
        // 为当前级别的符号表创建缩进
        String indent = String.join("", Collections.nCopies(indentLevel, "    "));

        // 打印当前符号表的开始
        writer.write(indent + "-| Symbol Table Level: " + this.Level + "\n");

        // 打印当前符号表的符号
        writer.write(indent + "    Symbols:\n");
        for (Map.Entry<String, Symbol> entry : this.SymbolMap.entrySet()) {
            writer.write(indent + "    - " + entry.getKey() + ": " + entry.getValue() + "\n");
        }

        // 如果有子符号表，打印分隔符
        if (!this.childSymbolTable.isEmpty()) {
            writer.write(indent + "    Child Symbol Tables:\n");
        }

        // 递归地打印每个子符号表
        for (SymbolTable child : this.childSymbolTable) {
            child.printTableToFile(writer, indentLevel + 1);
        }
    }

    // 一个方便的方法来开始打印过程并关闭文件
    public static void printToFile(SymbolTable table) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("LevelTable.txt"));
        table.printTableToFile(writer, 0);
        writer.flush();
        writer.close();
    }

    public static void PrintSymbolTable(SymbolTable table) throws IOException {
        SymbolTableWriter.write("{\n");
        SymbolTableWriter.write("SymbolTable \n ");
        SymbolTableWriter.write("Level: "+table.Level+"\n");
        for(String key: table.SymbolMap.keySet()){
            Symbol symbol = table.SymbolMap.get(key);
            SymbolTableWriter.write("Name: "+symbol.name+" ");
            SymbolTableWriter.write("Type: "+symbol.type+" ");
            SymbolTableWriter.write("Level: "+symbol.Level+" ");
            SymbolTableWriter.write("Flag: "+symbol.Flag+" ");
            SymbolTableWriter.write("DataType: "+symbol.dataType+"\n");
        }
        SymbolTableWriter.write("}\n");
        SymbolTableWriter.flush();
    }



    public static void printFunctionInfo(HashMap<String, FuncTable> funcTableMap, BufferedWriter writer) {
        if (funcTableMap == null) {
            try {
                writer.write("No function information available.\n");
            } catch (IOException e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
            return;
        }
        try {
            // 遍历映射
            writer.write("\n");
            writer.write("Function information:\n");
            for (String key: funcTableMap.keySet()){
                FuncTable table = funcTableMap.get(key);
                writer.write("Function name: " + table.name + "\n");
                writer.write("Parameter number: " + table.Parameters.size() + "\n");
                writer.write("Parameter list: \n");
                for (Symbol symbol: table.Parameters){
                    writer.write("Parameter   name: " + symbol.name + " ");
                    writer.write("type: " + symbol.type + " ");
                    writer.write("Flag: " + symbol.Flag + " ");
                    writer.write("Datatype: " + symbol.dataType + "\n");
                }
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace(); // 在实际情况中，您可能需要更合适的异常处理
        }

    }

}
