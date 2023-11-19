package front;

import java.util.List;

public class FuncTable {
    public String name;
    //参数列表
    public List<Symbol> Parameters;
    public FuncTable(String name) {
        this.name = name;
    }
    public FuncTable(String name, List<Symbol> Params) {
        this.name = name;
        this.Parameters = Params;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Function Name: ").append(name).append("\n");
        sb.append(" Parameters:\n");
        for (Symbol param : Parameters) {
            sb.append(param.toString()).append("\n");
        }
        return sb.toString();
    }
}
