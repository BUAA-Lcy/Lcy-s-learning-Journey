//author: Lcy

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;



/*
文法如下：

编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
声明 Decl → ConstDecl | VarDecl
常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
基本类型 BType → 'int' ；
常数定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
常量初值 ConstInitVal → ConstExp |'{' [ ConstInitVal { ',' ConstInitVal } ] '}'
变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
变量定义 VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
函数类型 FuncType → 'void' | 'int'
函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
语句块 Block → '{' { BlockItem } '}'
语句块项 BlockItem → Decl | Stmt
语句 Stmt → LVal '=' Exp ';'
| [Exp] ';'
| Block
| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
| 'break' ';'
| 'continue' ';'
|'return' [Exp] ';'
| LVal '=' 'getint''('')'';'
| 'printf''('FormatString{','Exp}')'';'
语句 ForStmt → LVal '=' Exp
表达式 Exp → AddExp  Exp的First集: Ident, Number, '('
条件表达式 Cond → LOrExp
左值表达式 LVal → Ident {'[' Exp ']'}
基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number    PrimaryExp的FIRST集为: '(', Ident, Number
数值 Number → IntConst
一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'|
UnaryOp UnaryExp
单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
函数实参表 FuncRParams → Exp { ',' Exp }
乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
逻辑与表达式 LAndExp → EqExp | LAnd
常量表达式 ConstExp → AddExp      注：使用的Ident 必须是常量 // 存在即可

<BlockItem>, <Decl>, <BType>除外，其余都要输出语法成分
{}不加引号的大括号表示可以出现1-n次
[]不加引号的中括号表示可以出现0-1次
| 表示或

 */
public class SyntaxFunction    {

    public static BufferedWriter debugbw;
    public static BufferedWriter errorbw;

    public static ArrayList<String> OutputStringSet= new  ArrayList<>();
    //把该StringSet的内容输出到newoutput.txt中


    public static void writeToFile(ArrayList<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (String str : content) {
                writer.write(str);
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //当前待放入的位置的索引
    public static int OutputStringIndex = 0;


    static {
        try {
            debugbw = new BufferedWriter(new FileWriter("mydebug.txt"));
            errorbw = new BufferedWriter(new FileWriter("myerror.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void cleanup() {
        try {
            debugbw.flush();
            errorbw.flush();
            debugbw.close();
            errorbw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void AddOutputString(String str){
        OutputStringSet.add(str);
        OutputStringIndex++;
    }

    public static void next() throws IOException {
        Syntax.UnitIndex++;
    }
    public static void printLexicalUnit() throws IOException{
        //for debug lcy
        debugbw.write("Line Number | Token  | Value | Index\n");
        debugbw.write("-----------------------------------\n");
        for (int i = 0; i < Syntax.lexicalUnits.size(); i++) {
            Lexical_unit lu = Syntax.lexicalUnits.get(i);
            debugbw.write(String.format("%11d | %-6s | %-5s | %d%n", lu.linenum, lu.token, lu.value, i));
        }
        debugbw.write("-----------------------------------\n");

        //打印相同的信息到控制台上
        System.out.println("Line Number | Token  | Value | Index");
        System.out.println("-----------------------------------");
        for (int i = 0; i < Syntax.lexicalUnits.size(); i++) {
            Lexical_unit lu = Syntax.lexicalUnits.get(i);
            System.out.println(String.format("%11d | %-6s | %-5s | %d%n", lu.linenum, lu.token, lu.value, i));
        }
        System.out.println("-----------------------------------");

    }
    //未完善以后错误处理
    public static void ExaminationAndPrintByValue(String expectedValue,Lexical_unit realUnit) throws IOException{
        int templinenum = realUnit.linenum;
        if(!expectedValue.equals(realUnit.value)){
            System.out.println("————————————————\n");
            System.out.println("Value not Expected!\n");
            System.out.println("Expected: "+expectedValue+"\n");
            System.out.println("Real: "+realUnit.value+"\n");
            System.out.println("Syntax error: line "+ templinenum);
            System.exit(0);
        }
        else{
            System.out.println("————————————————");
            System.out.println("successful");
            System.out.println("Index: "+Syntax.UnitIndex);
            System.out.println("Token: "+realUnit.token);
            System.out.println("Value: "+realUnit.value);
            System.out.println("Line Number: "+realUnit.linenum);
            System.out.println("————————————————");
            
            AddOutputString(realUnit.token+" "+realUnit.value+"\n");
            if(Syntax.UnitIndex < Syntax.lexicalUnits.size()-1) {
                debugbw.write("————————————————————-\n");
                debugbw.write("Index:  "+ Syntax.UnitIndex +"\n");
                debugbw.write("Token: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).token+"\n");
                debugbw.write("Value: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).value+"\n");
                debugbw.write("Line Number: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum+"\n");
                debugbw.write("————————————————————-\n");
                debugbw.flush();
                next();
            }
            else {
                System.out.println("————————————————");
                System.out.println("Last Linenum = " + templinenum);
                System.out.println("OVER");
            }
        }
    }
    public static void ExaminationAndPrintByToken(String expectedToken,Lexical_unit realUnit) throws IOException{
        if(!expectedToken.equals(realUnit.token)){
            System.out.println("————————————————\n");
            System.out.println("Token not Expected!\n");
            System.out.println("Expected: "+expectedToken+"\n");
            System.out.println("Real: "+realUnit.token+"\n");
            System.out.println("Syntax error: line "+ Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum);
            System.exit(0);
        }
        else{
            System.out.println("————————————————");
            System.out.println("successful");
            System.out.println("Index: "+Syntax.UnitIndex);
            System.out.println("Token: "+realUnit.token);
            System.out.println("Value: "+realUnit.value);
            System.out.println("Line Number: "+realUnit.linenum);
            System.out.println("————————————————");
            
            AddOutputString(realUnit.token+" "+realUnit.value+"\n");
            if(Syntax.UnitIndex < Syntax.lexicalUnits.size()-1) {
                //调试：打印当前的Token和行号
                debugbw.write("————————————————————-\n");
                debugbw.write("Index:  "+ Syntax.UnitIndex +"\n");
                debugbw.write("Value: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).value+"\n");
                debugbw.write("Token: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).token+"\n");
                debugbw.write("Line Number: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum+"\n");
                debugbw.write("————————————————————-\n");
                debugbw.flush();
                next();
            }
            else {
                System.out.println("————————————————\n");
                System.out.println("Last Linenum = " + Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum+"\n");
                System.out.println("OVER\n");
            }
        }
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    public void CompUnit() throws IOException {

        if((Syntax.UnitIndex + 2 ) < Syntax.lexicalUnits.size()-1) {
            while (!Syntax.lexicalUnits.get(Syntax.UnitIndex + 2).value.equals("(")) {
                Decl();
            }
        }
            //判断FuncDef
        if((Syntax.UnitIndex + 1) < Syntax.lexicalUnits.size()-1) {
            while (!Syntax.lexicalUnits.get(Syntax.UnitIndex + 1).value.equals("main")) {
                FuncDef();
            }
        }

        else {
            MyError.myerror(Syntax.lexicalUnits.get(Syntax.UnitIndex),"CompUnit");
        }
        MainFuncDef();

        AddOutputString("<CompUnit>");
    }
    // 在Function类内添加如下方法
    public void Decl() throws IOException{
        // Decl → ConstDecl | VarDecl
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("const")) {
            ConstDecl();
        } else {
            VarDecl();
        }
    }

    public void ConstDecl() throws IOException{
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        ExaminationAndPrintByValue("const",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        BType();
        ConstDef();
        while (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
            ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ConstDef();
        }
        ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));

        AddOutputString("<ConstDecl>\n");
    }

    public void BType() throws IOException{
        // BType → 'int'
        //有可能会加上别的
        ExaminationAndPrintByValue("int",Syntax.lexicalUnits.get(Syntax.UnitIndex));
    }

    public void ConstDef() throws IOException{
        // 常量定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        Ident();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
            ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ConstExp();
            ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        ExaminationAndPrintByValue("=",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        ConstInitVal();

        AddOutputString("<ConstDef>\n");
    }
    public void ConstInitVal() throws IOException{
        // ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("{")){
            ExaminationAndPrintByValue("{",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
                ConstInitVal();
                while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
                    ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                    ConstInitVal();
                }
            }
            ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else{
            ConstExp();
        }

        AddOutputString("<ConstInitVal>\n");
    }
    public void ConstExp() throws IOException{
        // ConstExp → AddExp
        AddExp();
        AddOutputString("<ConstExp>\n");
    }
    public void AddExp() throws IOException{
        //加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
        // AddExp → MulExp { ('+' | '−') MulExp }
        MulExp();
        AddOutputString("<AddExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-")){
            //这里可能有更精确的实现方法
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
            MulExp();
            //注意，因为逻辑上消除了左递归，可能会出现少打印AddExp的现象
            AddOutputString("<AddExp>\n");
        }
    }
    public void MulExp() throws IOException{
        // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        //乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        UnaryExp();
        AddOutputString("<MulExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("*")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("/")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("%")){
            //这里相当于直接打印了 因为在循环条件中已经判断过了
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
            UnaryExp();
            //注意，因为逻辑上消除了左递归，可能会出现少打印MulExp的现象
            AddOutputString("<MulExp>\n");
        }
    }
    public void UnaryOP() throws IOException{
        //单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!")){
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else{
            MyError.myerror(Syntax.lexicalUnits.get(Syntax.UnitIndex),"UnaryOp");
        }
        AddOutputString("<UnaryOp>\n");
    }
    public void IntConst() throws IOException{
        //数值 Number → IntConst
        ExaminationAndPrintByToken("INTCON",Syntax.lexicalUnits.get(Syntax.UnitIndex));
    }
    public void UnaryExp() throws IOException{
        //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
        //  2 + 2
        //  a + b
        //  sum(1,2) ;
        if(((Syntax.UnitIndex + 1< Syntax.lexicalUnits.size()-1))&&(Syntax.lexicalUnits.get(Syntax.UnitIndex + 1).value.equals("("))&&(Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("IDENFR"))){
            Ident();
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(")")){
                FuncRParams();
            }
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!")) {
            UnaryOP();
            UnaryExp();
        }
        else{
            //基本表达式
            PrimaryExp();
        }
        AddOutputString("<UnaryExp>\n");
    }
    public void PrimaryExp() throws IOException{
        // 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number
        // 左值表达式 LVal → Ident {'[' Exp ']'}
        // 数值 Number → IntConst
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("(")){
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Exp();
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("IDENFR")) {
            LVal();
        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("INTCON")) {
            Number();
        }
        else{
            MyError.myerror(Syntax.lexicalUnits.get(Syntax.UnitIndex),"PrimaryExp");
        }

        AddOutputString("<PrimaryExp>\n");

    }
    public void LVal() throws IOException{
        // 左值表达式 LVal → Ident {'[' Exp ']'}
        Ident();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
            ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Exp();
            ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        AddOutputString("<LVal>\n");
    }
    public void FuncRParams() throws IOException{
        // 函数实参表 FuncRParams → Exp { ',' Exp }
        Exp();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
            ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Exp();
        }
        AddOutputString("<FuncRParams>\n");
    }
    public void Exp() throws IOException{
        // Exp → AddExp
        AddExp();
        AddOutputString("<Exp>\n");
    }
    public void Ident() throws IOException{
        // Ident → letter { letter | digit }
        ExaminationAndPrintByToken("IDENFR",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        //为终结符，不再打印
    }
    public void Number() throws IOException{
       IntConst();
       AddOutputString("<Number>\n");
    }
    public void ForStmt() throws IOException{
        // ForStmt → LVal '=' Exp
        LVal();
        ExaminationAndPrintByValue("=",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        Exp();
        AddOutputString("<ForStmt>\n");
    }
    public void FormatString() throws IOException{
        // FormatString → '"' { Formatelem } '"'
        ExaminationAndPrintByToken("STRCON",Syntax.lexicalUnits.get(Syntax.UnitIndex));

        //为终结符，不再打印

    }
    public void VarDecl() throws IOException{
        //变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
        BType();
        VarDef();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
            ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            VarDef();
        }
        ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));

        AddOutputString("<VarDecl>\n");
    }

    public void VarDef() throws IOException{
        //变量定义 VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
        Ident();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
            ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ConstExp();
            ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("=")){
            ExaminationAndPrintByValue("=",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            InitVal();
        }
        AddOutputString("<VarDef>\n");
    }
    public void InitVal() throws IOException{
        // InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("{")){
            ExaminationAndPrintByValue("{",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
                InitVal();
                while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
                    ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                    InitVal();
                }
            }
            ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else{
            Exp();
        }
        AddOutputString("<InitVal>\n");
    }
    public void FuncDef() throws IOException{
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        FuncType();
        Ident();
        ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(")")){
            FuncFParams();
        }
        ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        Block();
        AddOutputString("<FuncDef>\n");
    }
    public void Block() throws IOException{
        // Block → '{' { BlockItem } '}'
        ExaminationAndPrintByValue("{",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        while(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
            BlockItem();
        }
        ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        AddOutputString("<Block>\n");
    }
    public void BlockItem() throws IOException{
        // BlockItem → Decl | Stmt
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("const")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("int")){
            Decl();
        }
        else{
            Stmt();
        }
    }
    public void FuncFParams() throws IOException{
        // FuncFParams → FuncFParam { ',' FuncFParam }
        FuncFParam();
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
            ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            FuncFParam();
        }
        AddOutputString("<FuncFParams>\n");
    }
    public void FuncFParam() throws IOException{
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        BType();
        Ident();
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
            ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
                ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                ConstExp();
                ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            }
        }
        AddOutputString("<FuncFParam>\n");
    }
    public void Stmt() throws IOException {
//        语句 Stmt → LVal '=' Exp ';'
//                | [Exp] ';'
//                | Block
//                | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
//                | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
//                | 'break' ';'
//                | 'continue' ';'
//                |'return' [Exp] ';'
//                | LVal '=' 'getint''('')'';'
//                | 'printf''('FormatString{','Exp}')'';'

        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("if")){
            ExaminationAndPrintByValue("if",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Cond();
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Stmt();
            if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("else")){
                ExaminationAndPrintByValue("else",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                Stmt();
            }
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("for")){
            ExaminationAndPrintByValue("for",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")){
                ForStmt();
            }
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")){
                Cond();
            }
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(")")){
                ForStmt();
            }
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            Stmt();
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("break")){
            ExaminationAndPrintByValue("break",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("continue")){
            ExaminationAndPrintByValue("continue",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("return")){
            ExaminationAndPrintByValue("return",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")){
                Exp();
            }
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("printf")){
            ExaminationAndPrintByValue("printf",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            FormatString();
            while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
                ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                Exp();
            }
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));

        }
//        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")) {
//            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
//        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("{")) {
            Block();
        }


        //Exp 的开头是 Ident / (  / Number / + / - / !
        //LVal 的开头是 Ident

        //stmt -> LVal =  Exp  ;
        //     -> LVal = getint ( ) ;
        //     -> [Exp];
        // LVal → Ident {'[' Exp ']'}


        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")){
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("(")||Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("INTCON")) {
            Exp();
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("IDENFR")) {
            //author:Lcy
            //
            // 需要解析的只剩下：
            //          stmt -> LVal =  Exp  ;
            //          stmt -> LVal = getint ( ) ;
            //          stmt -> Exp ;
            //          Exp FIRST->*  UnaryExp
            //          UnaryExp FIRST->*  PrimaryExp -> LVal  (Primary First集合其余的项已经解析完毕)
            //          UnaryExp FIRST->*  Ident  '('  [ FuncRParams ] ')'
            //          LVal -> Ident {'[' Exp ']'}
            //          因此即使是Exp，在这里也可以通过LVal来解析并正常返回不报错
            int indexFlag = Syntax.UnitIndex;
            int StringFlag = OutputStringIndex;
            LVal();
            if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("=")){
                //是赋值语句
                ExaminationAndPrintByValue("=",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("getint")) {
                    // ->  getint ( ) ;
                   ExaminationAndPrintByValue("getint",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                   ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                   ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                   ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                }
                else{
                    //-> Exp ;
                    Exp();
                    ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                }
            }else {
                // 不是赋值语句
                // 语句 Stmt → [Exp] ';'
                Syntax.UnitIndex = indexFlag;
                OutputStringIndex = StringFlag;
                OutputStringSet.subList(OutputStringIndex,OutputStringSet.size()).clear();
                Exp();
                ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            }


        }else if((Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+"))||(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-"))||(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!"))){
            Exp();
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }

        AddOutputString("<Stmt>\n");

    }
    public void Cond() throws IOException{
        // Cond → LOrExp
        LOrExp();
        AddOutputString("<Cond>\n");
    }
    public void LOrExp() throws IOException{
        // LOrExp → LAndExp { '||' LAndExp }
        LAndExp();
        AddOutputString("<LOrExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("||")){
            ExaminationAndPrintByValue("||",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            LAndExp();
            AddOutputString("<LOrExp>\n");
        }
    }
    public void LAndExp() throws IOException{
        // LAndExp → EqExp { '&&' EqExp }
        EqExp();
        AddOutputString("<LAndExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("&&")){
            ExaminationAndPrintByValue("&&",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            EqExp();
            AddOutputString("<LAndExp>\n");
        }
    }
    public void EqExp() throws IOException{
        //    相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
        // EqExp → RelExp { ('==' | '!=') RelExp }
        RelExp();
        AddOutputString("<EqExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("==")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!=")){
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
            RelExp();
            AddOutputString("<EqExp>\n");
        }
    }
    public void RelExp() throws IOException{
        // RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        AddExp();
        AddOutputString("<RelExp>\n");
        while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("<")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(">")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("<=")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(">=")){
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
            AddExp();
            AddOutputString("<RelExp>\n");
        }
    }
    public void MainFuncDef() throws IOException{
        // MainFuncDef → 'int' 'main' '(' ')' Block
        ExaminationAndPrintByValue("int",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        ExaminationAndPrintByValue("main",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        Block();
        AddOutputString("<MainFuncDef>\n");
    }

    public void FuncType() throws IOException{
        // FuncType → 'void' | 'int'
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("void")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("int")){
            ExaminationAndPrintByValue(Syntax.lexicalUnits.get(Syntax.UnitIndex).value,Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else{
            MyError.myerror(Syntax.lexicalUnits.get(Syntax.UnitIndex),"FuncType");
        }
        AddOutputString("<FuncType>\n");
    }


}
/*

author:Lcy

| 非终结符     | FIRST 集                              |
|--------------|---------------------------------------|
| CompUnit     | {ε, 'const', 'void', 'int'}           |
| Decl         | {'const', 'int'}                      |
| ConstDecl    | {'const'}                             |
| BType        | {'int'}                               |
| ConstDef     | {Ident}                               |
| ConstInitVal | {Number, '{'}                         |
| VarDecl      | {'int'}                               |
| VarDef       | {Ident}                               |
| InitVal      | {'(', Ident, Number, '{'}             |
| FuncDef      | {'void', 'int'}                       |
| MainFuncDef  | {'int'}                               |
| FuncType     | {'void', 'int'}                       |
| FuncFParams  | {'int'}                               |
| FuncFParam   | {'int'}                               |
| Block        | {'{'}                                 |
| BlockItem    | {'const', 'int', '{'}                 |
| Exp          | {'(', Ident, Number, '+', '-', '!'}   |
| LVal         | {Ident}                               |
| PrimaryExp   | {'(', Ident, Number}                  |


 */