package front;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;
public class Symbol {
    public int Level;
    public String name;
    public String type; //函数，变量, 常量,数字
    public int Flag = 0;//0表示普通变量  1表示一维数组  2表示二维数组
    public String dataType = "int"; // Enum: 数据类型（int，float...）
    // 对于函数
    //实际上没有用到   函数的参数列表存放到了函数表FuncTableMap中
    public List<Symbol> parameterList = new ArrayList<Symbol>();
    // 函数参数类型列表
    public Symbol(int level,String name, String type) {
        this.Level = level;
        this.name = name;
        this.type = type;
    }
    public Symbol(int level,String name, String type, String DataType) {
        this.Level = level;
        this.name = name;
        this.type = type;
        this.dataType = DataType;
    }
    public Symbol(int level,String name, String type,String DataType,int i) {
        this.Level = level;
        this.name = name;
        this.type = type;
        this.dataType = DataType;
        this.Flag = i;
    }
    public String toString() {
        return "    Level: " + Level + ", Name: " + name + ", Type: " + type + ", DataType: " + dataType + ", Flag: " + Flag;
    }

}
