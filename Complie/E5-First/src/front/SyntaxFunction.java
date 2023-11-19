package front;

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
public class  SyntaxFunction    {

    public static BufferedWriter debugbw;
    public static BufferedWriter errorbw;
    public static HashMap<String,FuncTable> FuncTableMap = new HashMap<>();

    public static ArrayList<String> OutputStringSet= new  ArrayList<>();
    public static LinkedHashMap <Integer,String> ErrorMap= new LinkedHashMap<>();
    //把该StringSet的内容输出到newoutput.txt中
    public static int InCirculate = 0;



    public static void writeToFile(ArrayList<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Syntaxoutput.txt"))) {
            for (String str : content) {
                writer.write(str);
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //当前待放入的位置的索引
    static {
        try {
            debugbw = new BufferedWriter(new FileWriter("Syntaxdebug.txt"));
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
            Lexical_unit thisUnit = Syntax.lexicalUnits.get(Syntax.UnitIndex-1);
            if(expectedValue.equals(";")){
                ErrorMap.put(thisUnit.linenum,"i");
            }else if (expectedValue.equals(")")){
                ErrorMap.put(thisUnit.linenum,"j");
            }else if(expectedValue.equals("]")){
                ErrorMap.put(thisUnit.linenum,"k");
           }
        }
        else{
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
    public static void ExaminationAndPrintAnyWay(String expectedToken,Lexical_unit realUnit) throws IOException{
            AddOutputString(realUnit.token+" "+realUnit.value+"\n");
            if(Syntax.UnitIndex < Syntax.lexicalUnits.size()-1) {
                //调试：打印当前的Token和行号
                next();
            }
            else {
                System.out.println("————————————————\n");
                System.out.println("Last Linenum = " + Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum+"\n");
                System.out.println("OVER\n");
            }
            debugbw.write("————————————————————-\n");
            debugbw.write("Index:  "+ Syntax.UnitIndex +"\n");
            debugbw.write("Value: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).value+"\n");
            debugbw.write("Token: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).token+"\n");
            debugbw.write("Line Number: "+Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum+"\n");
            debugbw.write("————————————————————-\n");
            debugbw.flush();

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
        MainFuncDef();
        SymbolTable.PrintSymbolTable(Syntax.CurSymbolTable);
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
        Ident("Const");
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
        //   + 2
        //   - b
        //  sum(1,2) ;
        if(((Syntax.UnitIndex + 1< Syntax.lexicalUnits.size()-1))&&(Syntax.lexicalUnits.get(Syntax.UnitIndex + 1).value.equals("("))&&(Syntax.lexicalUnits.get(Syntax.UnitIndex).token.equals("IDENFR"))){
            String ThisName = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;

                if(FuncTableMap.containsKey(ThisName)){
                    //如果是函数
                    List<Symbol> ExpectedParamList = new ArrayList<>();
                    ExpectedParamList = FuncTableMap.get(ThisName).Parameters;
                    List<Symbol> RList = DefineErrorRParam();
                    if(ExpectedParamList.size()!=RList.size()) {
                        MyError.Debug("\n");
                        MyError.Debug("This FuncName: "+ThisName);
                        MyError.Debug("Expected: "+ExpectedParamList.size());
                        MyError.PrintSymbolList(ExpectedParamList);
                        MyError.Debug("RList: "+RList.size());
                        MyError.PrintSymbolList(RList);
                        MyError.Debug("\n");
                        ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"d");
                    }else {
                        for(int i = 0;i < ExpectedParamList.size();i++){
                            if(!ExpectedParamList.get(i).dataType.equals(RList.get(i).dataType)){
                                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"e");
                                MyError.Debug("\n");
                                MyError.Debug("This FuncName: "+ThisName);
                                MyError.Debug("Expected: "+ExpectedParamList.size());
                                MyError.PrintSymbolList(ExpectedParamList);
                                MyError.Debug("RList: "+RList.size());
                                MyError.PrintSymbolList(RList);
                                MyError.Debug("\n");
                            }
                            if(ExpectedParamList.get(i).Flag!=RList.get(i).Flag){
                                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"e");
                                MyError.Debug("\n");
                                MyError.Debug("This FuncName: "+ThisName);
                                MyError.Debug("Expected: "+ExpectedParamList.size());
                                MyError.PrintSymbolList(ExpectedParamList);
                                MyError.Debug("RList: "+RList.size());
                                MyError.PrintSymbolList(RList);
                                MyError.Debug("\n");
                            }
                        }
                    }
                }
            Ident("null");
            ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            if((!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(")"))&&(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";"))){
                FuncRParams();
            }
            ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));


        }
        else if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-")||Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!")) {
            UnaryOP();
            UnaryExp();
        }
        else{
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
        Ident("null");
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
    public void Ident(String type) throws IOException{
        //如果是null,表示并非是新的符号，而是已经存在的符号，所以不加入符号表
        Lexical_unit lu = Syntax.lexicalUnits.get(Syntax.UnitIndex);
        String name = lu.value;
        int TableLevel = Syntax.CurSymbolTable.Level;
        if(type.equals("Param")){
            //函数参数
            for(int i = 0;i < Syntax.CurParamList.size();i++){
                if(Syntax.CurParamList.get(i).name.equals(name)){
                    ErrorMap.put(lu.linenum,"b");
                    break;
                }
            }
            Syntax.CurParam = new Symbol(TableLevel+1,name,type,Syntax.CurParamDataType);
        }
        else if (!type.equals("null")){
            //定义
            Symbol SymbolFlag = SymbolTable.LookUpSymbol(Syntax.CurSymbolTable,name);
            int CurFlag = -1;
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex+1).value.equals("["))CurFlag = 0;
            else {
                if(Syntax.lexicalUnits.get(Syntax.UnitIndex+4).value.equals("["))CurFlag = 2;
                else CurFlag = 1;
            }
            if (SymbolFlag == null){
                //未定义的名字
                Syntax.CurSymbolTable.addSymbol(new Symbol(TableLevel,name,type,Syntax.CurIdentType,CurFlag));
            }
            else if (SymbolFlag.Level == TableLevel){
                //名字重定义  如果是参数 则之前已经加入过了
                ErrorMap.put(lu.linenum,"b");
            }else if (SymbolFlag.Level < TableLevel){
                Syntax.CurSymbolTable.addSymbol(new Symbol(TableLevel,name,type,Syntax.CurIdentType,CurFlag));
            }

        }else {
            //使用
            Symbol SymbolFlag = SymbolTable.LookUpSymbol(Syntax.CurSymbolTable,name);
            if (SymbolFlag == null){
                //未定义的名字
                ErrorMap.put(lu.linenum,"c");

            }else {

            }
        }
        // Ident → letter { letter | digit }
        ExaminationAndPrintByToken("IDENFR",Syntax.lexicalUnits.get(Syntax.UnitIndex));
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
    public int FormatString() throws IOException{
        // FormatString → '"' { Formatelem } '"'
        String str = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
        if(MyError.isValidFormatString(str)){
            int num = MyError.countPercentage(str);
            ExaminationAndPrintByToken("STRCON",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            return num;
        }
        else{
            ExaminationAndPrintByToken("STRCON",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"a");
            MyError.myerror(Syntax.lexicalUnits.get(Syntax.UnitIndex),"FormatString");
            return -1;
        }
        //为终结符，不再打印

    }
    public void VarDecl() throws IOException{
        //变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
        Syntax.CurIdentType = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
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
        Ident("Var");
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
    public void  FuncDef() throws IOException{
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        if(!Syntax.CurParamList.isEmpty())
            Syntax.CurParamList.clear();
        Syntax.CurFuncType = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
        Syntax.CurIdentType = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
        FuncType();
        Syntax.CurFuncName = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
        Ident("Func");
        ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(")")){
            //这里是因为有可能忘记加上右括号了
            if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("{"))
                FuncFParams();
        }
        ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        FuncBlock();
        AddOutputString("<FuncDef>\n");
    }
    public void Block() throws IOException{
        // Block → '{' { BlockItem } '}'
            Syntax.CurSymbolTable = new SymbolTable(Syntax.CurSymbolTable,Syntax.CurSymbolTable.Level + 1);
            ExaminationAndPrintByValue("{",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            while(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
                BlockItem();
            }
            ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            AddOutputString("<Block>\n");
            SymbolTable.PrintSymbolTable(Syntax.CurSymbolTable);
            Syntax.CurSymbolTable = Syntax.CurSymbolTable.ParentSymbolTable;

    }
    public void FuncBlock() throws IOException{
        // Block → '{' { BlockItem } '}'
        //创建新的符号表
        Syntax.CurSymbolTable = new SymbolTable(Syntax.CurSymbolTable,Syntax.CurSymbolTable.Level + 1);
        //创建新的函数表
        String tempName = Syntax.CurFuncName;
        List<Symbol> TempSymbolList = new ArrayList<>();
        //Syntax.CurParamList遍历传值给TempSymbolList
        for(Symbol symbol:Syntax.CurParamList){
            TempSymbolList.add(symbol);
        }
        FuncTable tempFuncTable = new FuncTable(tempName,TempSymbolList);
        Syntax.CurFuncTable = new FuncTable(tempName,TempSymbolList);
        //加入函数表列表
        FuncTableMap.put(tempName,tempFuncTable);

        //将该函数的参数加入对应符号表中
        for(Symbol symbol:Syntax.CurParamList){
            Syntax.CurSymbolTable.addSymbol(symbol);
        }
        ExaminationAndPrintByValue("{",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        if(Syntax.CurFuncType.equals("int")){
            while(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
                BlockItem();
            }
            int PutIndex = Syntax.UnitIndex;
            ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            AddOutputString("<Block>\n");
            SymbolTable.PrintSymbolTable(Syntax.CurSymbolTable);
            Syntax.CurSymbolTable = Syntax.CurSymbolTable.ParentSymbolTable;
            int ReturnIndex = JudegReturnExist(Syntax.UnitIndex-1);
            if(ReturnIndex == -1){
                ErrorMap.put(Syntax.lexicalUnits.get(PutIndex).linenum,"g");
            }
        }else if(Syntax.CurFuncType.equals("void")){
            while(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("}")){
                BlockItem();
            }
            ExaminationAndPrintByValue("}",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            AddOutputString("<Block>\n");
            SymbolTable.PrintSymbolTable(Syntax.CurSymbolTable);
            Syntax.CurSymbolTable = Syntax.CurSymbolTable.ParentSymbolTable;
            int ReturnIndex = JudegReturnExist(Syntax.UnitIndex-1);
            if(ReturnIndex != -1){
                if(!Syntax.lexicalUnits.get(ReturnIndex+1).value.equals(";")){
                    ErrorMap.put(Syntax.lexicalUnits.get(ReturnIndex).linenum,"f");
                }
            }
        }

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
        Syntax.CurParamDataType = Syntax.lexicalUnits.get(Syntax.UnitIndex).value;
        BType();
        Ident("Param");
        int TypeFlag = 0;
        if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
            TypeFlag = 1;
            ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("[")){
                TypeFlag = 2;
                ExaminationAndPrintByValue("[",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                ConstExp();
                ExaminationAndPrintByValue("]",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            }
        }

        Symbol tempParam = new Symbol(Syntax.CurParam.Level,Syntax.CurParam.name,"Param",Syntax.CurParam.dataType,TypeFlag);
        Syntax.CurParamList.add(tempParam);

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
            InCirculate ++;
            Stmt();
            InCirculate --;
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("break")){
            if (InCirculate<=0){
                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"m");
            }
            ExaminationAndPrintByValue("break",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }
        else if(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("continue")){
            if (InCirculate<=0){
                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"m");
            }
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
            int num = FormatString();
            int tempcount = 0;
            while(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(",")){
                ExaminationAndPrintByValue(",",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                tempcount++;
                Exp();
            }
            if(num!=-1&&tempcount!=num){
                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"l");
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

            //判断是否是赋值语句
            int denghao = Syntax.UnitIndex;
            int fenhao = Syntax.UnitIndex;
            while(!Syntax.lexicalUnits.get(fenhao).value.equals(";")){
                fenhao++;
                if(fenhao>=Syntax.lexicalUnits.size())break;
            }
            while(!Syntax.lexicalUnits.get(denghao).value.equals("=")){
                if (denghao >= fenhao) {
                    break;
                }
                denghao++;
            }

            if(denghao < fenhao){
                //是赋值语句
                // a[b+1] = c
                int beginindex = Syntax.UnitIndex;
                LVal();
                JudgeConstAssignment(beginindex);
                ExaminationAndPrintByValue("=",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                if (Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("getint")) {
                    ExaminationAndPrintByValue("getint",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                    ExaminationAndPrintByValue("(",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                    ExaminationAndPrintByValue(")",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                    ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                }
                else{
                    Exp();
                    ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
                }
            }
            else{
                //不是赋值语句
                if(!Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals(";")){
                    Exp();
                }
                ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
            }

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

        }else if((Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("+"))||(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("-"))||(Syntax.lexicalUnits.get(Syntax.UnitIndex).value.equals("!"))){
            Exp();
            ExaminationAndPrintByValue(";",Syntax.lexicalUnits.get(Syntax.UnitIndex));
        }

        AddOutputString("<Stmt>\n");

    }
    public static void JudgeConstAssignment(int beginindex){
        String IdentName = Syntax.lexicalUnits.get(beginindex).value;
        Symbol t = SymbolTable.LookUpSymbol(Syntax.CurSymbolTable,IdentName);
        if(t!=null){
            if(t.type.equals("Const")){
                ErrorMap.put(Syntax.lexicalUnits.get(beginindex).linenum,"h");
            }
        }
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

        //判断是否return,如果
        int ReturnIndex = JudegReturnExist(Syntax.UnitIndex-1);
        if(ReturnIndex == -1){
            ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"g");
        }
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

public static List<Symbol> DefineErrorRParam() throws IOException{

    //作用域：截止到下一个;
    //第一个一定是参数
    //函数实参表 FuncRParams → Exp { ',' Exp }
    //       |index
    // f1    (   f3(f3(f3(f3(c[0][1+1])))),b,c );
    // f1    (      f2() + d + b[0],b,c);
    List<Symbol> tempRList = new ArrayList<>();
    int lcyIndex  = Syntax.UnitIndex+1;
    int zuokuohao = 0;
    int youkuohao = 0;
    while(true){
        if(zuokuohao == youkuohao){
            if(Syntax.lexicalUnits.get(lcyIndex).value.equals(","))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals("+"))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals("-"))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals("*"))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals("/"))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals("%"))
                break;
            else if(Syntax.lexicalUnits.get(lcyIndex).value.equals(")"))
                break;
        }
        if(Syntax.lexicalUnits.get(lcyIndex).value.equals(";")){
            break;
        }
        if(Syntax.lexicalUnits.get(lcyIndex).value.equals("(")){
            zuokuohao++;
        }
        if(Syntax.lexicalUnits.get(lcyIndex).value.equals(")")){
            youkuohao++;
        }
        // f1    (      f2() + d + b[0],b,c);
        if(Syntax.lexicalUnits.get(lcyIndex).token.equals("IDENFR")||Syntax.lexicalUnits.get(lcyIndex).token.equals("INTCON")||Syntax.lexicalUnits.get(lcyIndex).value.equals("+")||Syntax.lexicalUnits.get(lcyIndex).value.equals("-")){
            if(NewRParam(lcyIndex)!=null){
                tempRList.add(NewRParam(lcyIndex));
            }
            while(true){
                lcyIndex++;
                if(Syntax.lexicalUnits.get(lcyIndex).value.equals("(")){
                    zuokuohao++;
                }
                if(Syntax.lexicalUnits.get(lcyIndex).value.equals(")")){
                    youkuohao++;
                }
                if(zuokuohao == youkuohao){
                    break;
                }
                if((Syntax.lexicalUnits.get(lcyIndex).value.equals(","))&&(zuokuohao == youkuohao+1)){
                    break;
                }
                if(Syntax.lexicalUnits.get(lcyIndex).value.equals(";")){
                    break;
                }
            }
        }
        if(Syntax.lexicalUnits.get(lcyIndex).value.equals(";")){
            break;
        }
        lcyIndex++;
    }
    return tempRList;

}


    public static Symbol NewRParam(int index) throws IOException{
        int tempIndex = index;
        int IdentifierFlag = 0;
        if(Syntax.lexicalUnits.get(tempIndex).token.equals("IDENFR")){
            IdentifierFlag = 1;
        }
        if(IdentifierFlag == 1){
            String name = Syntax.lexicalUnits.get(tempIndex).value;
            Symbol t = SymbolTable.LookUpSymbol(Syntax.CurSymbolTable,name);
            //打印t的信息
            if(t == null){
                ErrorMap.put(Syntax.lexicalUnits.get(Syntax.UnitIndex).linenum,"c");
                return null;
            }
            if(t.Flag !=0){
                int Weidu = 0;
                if(Syntax.lexicalUnits.get(tempIndex+1).value.equals("[")){
                    Weidu++;
                    if(Syntax.lexicalUnits.get(tempIndex+1+3).value.equals("[")){
                        Weidu++;
                    }
                }
                Symbol tempSymbol = new Symbol(Syntax.CurSymbolTable.Level, t.name, t.type, t.dataType, t.Flag-Weidu);
                return tempSymbol;
            }else {
                Symbol tempSymbol = new Symbol(Syntax.CurSymbolTable.Level, t.name, t.type, t.dataType, t.Flag);
                return tempSymbol;
            }
        }else {
            Symbol tempSymbol = new Symbol(Syntax.CurSymbolTable.Level, "number", "RParam", "int", 0);
            //打印tempSymbol的信息：
            return tempSymbol;
        }
    }

    //判断是否存在Return语句并返回'Return'的位置

    public static int JudegReturnExist(int index){
        //从右大括号开始回退

        int tempIndex = index;
        //回退到下一个;或者{
        if(Syntax.lexicalUnits.get(tempIndex-1).value.equals("}")){

            return -1;
        }
        while(!Syntax.lexicalUnits.get(tempIndex).value.equals(";")&&!Syntax.lexicalUnits.get(tempIndex).value.equals("{")){
            tempIndex--;
        }
        if(Syntax.lexicalUnits.get(tempIndex).value.equals("{")){
            //空Block
            return -1;
        }
        tempIndex--;
        //回退到下一个;
        while(!Syntax.lexicalUnits.get(tempIndex).value.equals(";")&&!Syntax.lexicalUnits.get(tempIndex).value.equals("{")&&!Syntax.lexicalUnits.get(tempIndex).value.equals("}")){
            tempIndex--;
        }
        int ReturnIndex = tempIndex+1;
        if(Syntax.lexicalUnits.get(ReturnIndex).value.equals("return")){
            return ReturnIndex;
        }else {
            return -1;
        }

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