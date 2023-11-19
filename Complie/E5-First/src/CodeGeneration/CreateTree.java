package CodeGeneration;


import com.sun.source.tree.Tree;
import front.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;



public class CreateTree {
    public static int UnitIndex = 0;
    public static ArrayList<Lexical_unit> lexicalUnits = Lexer.LexicalUnitArray;
    public static SymbolTable GlobalSymbolTable  = new SymbolTable(null,0);
    public static SymbolTable CurSymbolTable  = GlobalSymbolTable;
    public static Symbol CurParam ;
    public static String CurFuncName ;
    public static String CurFuncType;
    public static String CurIdentType;
    public static String CurParamDataType;
    public static int InCirculate = 0;
    public static List<Symbol> CurParamList = new ArrayList<>();
    public static HashMap<String, FuncTable> FuncTableMap = SyntaxFunction.FuncTableMap;
    public static TreeNode Root = new TreeNode("CompUnit");
    public static LinkedList<TreeNode> UnitStack = new LinkedList<>();
    public static TreeNode CurNode;
    public static TreeNode BackUpNode;
    //用于存储刚进入某个递归下降函数中TreeNode的指针
    public static void main(String[] args) throws IOException {

        System.out.println("Tree Creation has begun.");
//        printLexicalUnit();
        CreateTree generation = new CreateTree();
        generation.CompUnit();
//        Root.printNode();
//        Root.printPostOrder();
        System.out.println("Tree Creation is over.");
    }
    public static void next() throws IOException {
        UnitIndex++;
    }
    public static void printLexicalUnit() throws IOException {
        //for debug lcy
        System.out.println("Line Number | Token  | Value | Index");
        System.out.println("-----------------------------------");
        for (int i = 0; i < lexicalUnits.size(); i++) {
            Lexical_unit lu = lexicalUnits.get(i);
            System.out.println(String.format("%11d | %-6s | %-5s | %d%n", lu.linenum, lu.token, lu.value, i));
        }
        System.out.println("-----------------------------------");
    }

    //非终结符创建函数
    void CreateTreeNode(String content) throws IOException{
        BackUp();
        TreeNode tempNode = new TreeNode(content,0);
        CurNode.addChild(tempNode);
        CurNode = tempNode;
    }


    void CreateLeafNode(String content) throws IOException{
        TreeNode tempNode = new TreeNode(content,1);
        CurNode.addChild(tempNode);
        next();
    }

    void BackUp(){
        UnitStack.push(CurNode);
    }
    void Reverse(){
        CurNode= UnitStack.pop();
    }

    void CompUnit() throws IOException {

        CurNode = Root;
        BackUpNode = Root;

        if((UnitIndex + 2 ) < lexicalUnits.size()-1) {
            while (!lexicalUnits.get(UnitIndex + 2).value.equals("(")) {
                CreateTreeNode("Decl");
                Decl();
                Reverse();
            }
        }
        //判断FuncDef
        if((UnitIndex + 1) < lexicalUnits.size()-1) {
            while (!lexicalUnits.get(UnitIndex + 1).value.equals("main")) {
                CreateTreeNode("FuncDef");
                FuncDef();
                Reverse();
            }
        }

        CreateTreeNode("MainFuncDef");
        MainFuncDef();
        Reverse();

        SymbolTable.PrintSymbolTable(CurSymbolTable);


    }
    // 在Function类内添加如下方法
    void Decl() throws IOException{
        // Decl → ConstDecl | VarDecl

        if(lexicalUnits.get(UnitIndex).value.equals("const")) {
            CreateTreeNode("ConstDecl");
            ConstDecl();
            Reverse();
        } else {
            CreateTreeNode("VarDecl");
            VarDecl();
            Reverse();
        }
    }

    void ConstDecl() throws IOException{
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'

        CreateLeafNode("const");
        CreateTreeNode("BType");
        BType();
        Reverse();


        CreateTreeNode("ConstDef");
        ConstDef();
        Reverse();


        while (lexicalUnits.get(UnitIndex).value.equals(",")){

            CreateLeafNode(",");

            CreateTreeNode("ConstDef");
            ConstDef();
            Reverse();
        }

        CreateLeafNode(";");

    }

    public void BType() throws IOException{
        // BType → 'int'

        CreateLeafNode("int");
    }

    public void ConstDef() throws IOException{
        // 常量定义 ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal

//        CreateTreeNode("Ident");
        Ident("Const");
//        Reverse();


        while(lexicalUnits.get(UnitIndex).value.equals("[")){

            CreateLeafNode("[");

            CreateTreeNode("ConstExp");
            ConstExp();
            Reverse();

            CreateLeafNode("]");
        }

        CreateLeafNode("=");

        CreateTreeNode("ConstInitVal");
        ConstInitVal();
        Reverse();

    }
    public void ConstInitVal() throws IOException{
        // ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        if(lexicalUnits.get(UnitIndex).value.equals("{")){
            CreateLeafNode("{");
            if(!lexicalUnits.get(UnitIndex).value.equals("}")){
                CreateTreeNode("ConstInitVal");
                ConstInitVal();
                Reverse();
                while(lexicalUnits.get(UnitIndex).value.equals(",")){
                    CreateLeafNode(",");
                    CreateTreeNode("ConstInitVal");
                    ConstInitVal();
                    Reverse();
                }
            }
            CreateLeafNode("}");
        }
        else{
            CreateTreeNode("ConstExp");
            ConstExp();
            Reverse();
        }


    }
    public void ConstExp() throws IOException{
        // ConstExp → AddExp
        CreateTreeNode("AddExp");
        AddExp();
        Reverse();

    }
    public void AddExp() throws IOException{
        //加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
        // AddExp → MulExp { ('+' | '−') MulExp }
        // A -> M
        // A-> A + M -> A + M + M -> M + M + M
//        MulExp();
//
//        while(lexicalUnits.get(UnitIndex).value.equals("+")||lexicalUnits.get(UnitIndex).value.equals("-")){
//            //这里可能有更精确的实现方法
//            ExaminationAndPrintByValue(lexicalUnits.get(UnitIndex).value,lexicalUnits.get(UnitIndex));
//            MulExp();
//            //注意，因为逻辑上消除了左递归，可能会出现少打印AddExp的现象
//
//        }
        CreateTreeNode("MulExp");
        MulExp();
        Reverse();

        if(lexicalUnits.get(UnitIndex).value.equals("+")||lexicalUnits.get(UnitIndex).value.equals("-")){
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);
            CreateTreeNode("AddExp");
            AddExp();
            Reverse();
        }

    }
    public void MulExp() throws IOException{
        // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        //乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        CreateTreeNode("UnaryExp");
        UnaryExp();
        Reverse();

        if(lexicalUnits.get(UnitIndex).value.equals("*")||lexicalUnits.get(UnitIndex).value.equals("/")||lexicalUnits.get(UnitIndex).value.equals("%")){
            //这里相当于直接打印了 因为在循环条件中已经判断过了
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);

            CreateTreeNode("MulExp");
            MulExp();
            Reverse();
            //注意，因为逻辑上消除了左递归，可能会出现少打印MulExp的现象

        }
    }
    public void UnaryOP() throws IOException{
        //单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
        if(lexicalUnits.get(UnitIndex).value.equals("+")||lexicalUnits.get(UnitIndex).value.equals("-")||lexicalUnits.get(UnitIndex).value.equals("!")){
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);
        }
        else{
            MyError.myerror(lexicalUnits.get(UnitIndex),"UnaryOp");
        }

    }
    public void IntConst() throws IOException{
        //数值 Number → IntConst
        CreateLeafNode("IntConst");
    }
    public void UnaryExp() throws IOException{
        //一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'| UnaryOp UnaryExp
        //   + 2
        //   - b
        //  sum(1,2) ;
        if(((UnitIndex + 1< lexicalUnits.size()-1))&&(lexicalUnits.get(UnitIndex + 1).value.equals("("))&&(lexicalUnits.get(UnitIndex).token.equals("IDENFR"))){

//            CreateTreeNode("Ident");
            Ident("null");
//            Reverse();

            CreateLeafNode("(");

            if((!lexicalUnits.get(UnitIndex).value.equals(")"))&&(!lexicalUnits.get(UnitIndex).value.equals(";"))){
                CreateTreeNode("FuncRParams");
                FuncRParams();
                Reverse();
            }
            CreateLeafNode(")");
        }
        else if (lexicalUnits.get(UnitIndex).value.equals("+")||lexicalUnits.get(UnitIndex).value.equals("-")||lexicalUnits.get(UnitIndex).value.equals("!")) {

            CreateTreeNode("UnaryOp");
            UnaryOP();
            Reverse();

            CreateTreeNode("UnaryExp");
            UnaryExp();
            Reverse();
        }
        else{
            CreateTreeNode("PrimaryExp");
            PrimaryExp();
            Reverse();
        }

    }
    public void PrimaryExp() throws IOException{
        // 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number
        // 左值表达式 LVal → Ident {'[' Exp ']'}
        // 数值 Number → IntConst
        if(lexicalUnits.get(UnitIndex).value.equals("(")){
            CreateLeafNode("(");
            CreateTreeNode("Exp");
            Exp();
            Reverse();
            CreateLeafNode(")");
        }
        else if (lexicalUnits.get(UnitIndex).token.equals("IDENFR")) {
            CreateTreeNode("LVal");
            LVal();
            Reverse();
        }
        else if (lexicalUnits.get(UnitIndex).token.equals("INTCON")) {
            CreateTreeNode("Number");
            Number();
            Reverse();
        }
        else{
            MyError.myerror(lexicalUnits.get(UnitIndex),"PrimaryExp");
        }



    }
    public void LVal() throws IOException{
        // 左值表达式 LVal → Ident {'[' Exp ']'}
//        CreateTreeNode("Ident");
        Ident("null");
//        Reverse();
        while(lexicalUnits.get(UnitIndex).value.equals("[")){
            CreateLeafNode("[");
            CreateTreeNode("Exp");
            Exp();
            Reverse();
            CreateLeafNode("]");
        }

    }
    public void FuncRParams() throws IOException{
        // 函数实参表 FuncRParams → Exp { ',' Exp }
        CreateTreeNode("Exp");
        Exp();
        Reverse();
        while(lexicalUnits.get(UnitIndex).value.equals(",")){
            CreateLeafNode(",");
            CreateTreeNode("Exp");
            Exp();
            Reverse();
        }

    }
    public void Exp() throws IOException{
        // Exp → AddExp
        CreateTreeNode("AddExp");
        AddExp();
        Reverse();
    }
    public void Ident(String type) throws IOException{
        //如果是null,表示并非是新的符号，而是已经存在的符号，所以不加入符号表
        Lexical_unit lu = lexicalUnits.get(UnitIndex);
        String name = lu.value;
        int TableLevel = CurSymbolTable.Level;
        if(type.equals("Param")){
            //函数参数
            for(int i = 0;i < CurParamList.size();i++){
                if(CurParamList.get(i).name.equals(name)){
                    break;
                }
            }
            CurParam = new Symbol(TableLevel+1,name,type,CurParamDataType);
        }
        else if (!type.equals("null")){
            //定义
            Symbol SymbolFlag = SymbolTable.LookUpSymbol(CurSymbolTable,name);
            int CurFlag = -1;
            if(!lexicalUnits.get(UnitIndex+1).value.equals("["))CurFlag = 0;
            else {
                if(lexicalUnits.get(UnitIndex+4).value.equals("["))CurFlag = 2;
                else CurFlag = 1;
            }
            if (SymbolFlag == null){
                //未定义的名字
                CurSymbolTable.addSymbol(new Symbol(TableLevel,name,type,CurIdentType,CurFlag));
            }
            else if (SymbolFlag.Level == TableLevel){
                //名字重定义  如果是参数 则之前已经加入过了

            }else if (SymbolFlag.Level < TableLevel){
                CurSymbolTable.addSymbol(new Symbol(TableLevel,name,type,CurIdentType,CurFlag));
            }

        }else {
            //使用
            Symbol SymbolFlag = SymbolTable.LookUpSymbol(CurSymbolTable,name);
            if (SymbolFlag == null){
                //未定义的名字


            }else {

            }
        }
        // Ident → letter { letter | digit }
        CreateLeafNode(lu.value);
    }
    public void Number() throws IOException{
        CreateLeafNode("IntConst");
    }
    public void ForStmt() throws IOException{
        // ForStmt → LVal '=' Exp
        CreateTreeNode("LVal");
        LVal();
        Reverse();
        CreateLeafNode("=");
        CreateTreeNode("Exp");
        Exp();
        Reverse();
    }
    public void FormatString() throws IOException{
        // FormatString → '"' { Formatelem } '"'
        String str = lexicalUnits.get(UnitIndex).value;
        CreateLeafNode(str);
        //为终结符，不再打印
    }
    public void VarDecl() throws IOException{
        //变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
        CurIdentType = lexicalUnits.get(UnitIndex).value;
        CreateTreeNode("BType");
        BType();
        Reverse();
        CreateTreeNode("VarDef");
        VarDef();
        Reverse();
        while(lexicalUnits.get(UnitIndex).value.equals(",")){
            CreateLeafNode(",");
            CreateTreeNode("VarDef");
            VarDef();
            Reverse();
        }
        CreateLeafNode(";");
    }

    public void VarDef() throws IOException{
        //变量定义 VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
//        CreateTreeNode("Ident");
        Ident("Var");
//        Reverse();
        while(lexicalUnits.get(UnitIndex).value.equals("[")){
            CreateLeafNode("[");
            CreateTreeNode("ConstExp");
            ConstExp();
            Reverse();
            CreateLeafNode("]");
        }
        if(lexicalUnits.get(UnitIndex).value.equals("=")){
            CreateLeafNode("=");
            CreateTreeNode("InitVal");
            InitVal();
            Reverse();
        }

    }
    public void InitVal() throws IOException{
        // InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        if(lexicalUnits.get(UnitIndex).value.equals("{")){
            CreateLeafNode("{");
            if(!lexicalUnits.get(UnitIndex).value.equals("}")){
                CreateTreeNode("InitVal");
                InitVal();
                Reverse();
                while(lexicalUnits.get(UnitIndex).value.equals(",")){
                    CreateLeafNode(",");
                    CreateTreeNode("InitVal");
                    InitVal();
                    Reverse();
                }
            }
            CreateLeafNode("}");
        }
        else{
            CreateTreeNode("Exp");
            Exp();
            Reverse();
        }

    }
    public void  FuncDef() throws IOException{
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        if(!CurParamList.isEmpty())
            CurParamList.clear();
        CurFuncType = lexicalUnits.get(UnitIndex).value;
        CurIdentType = lexicalUnits.get(UnitIndex).value;
        CreateTreeNode("FuncType");
        FuncType();
        Reverse();
        CurFuncName = lexicalUnits.get(UnitIndex).value;
//        CreateTreeNode("Ident");
        Ident("Func");
//        Reverse();
        CreateLeafNode("(");
        if(!lexicalUnits.get(UnitIndex).value.equals(")")){
            //这里是因为有可能忘记加上右括号了
            if(!lexicalUnits.get(UnitIndex).value.equals("{")){
                CreateTreeNode("FuncFParams");
                FuncFParams();
                Reverse();
            }

        }
        CreateLeafNode(")");
        CreateTreeNode("Block");
        Block();
        Reverse();

    }
    public void Block() throws IOException{
        // Block → '{' { BlockItem } '}'
        CurSymbolTable = new SymbolTable(CurSymbolTable,CurSymbolTable.Level + 1);
        CreateLeafNode("{");
        while(!lexicalUnits.get(UnitIndex).value.equals("}")){
            CreateTreeNode("BlockItem");
            BlockItem();
            Reverse();
        }
        CreateLeafNode("}");

        SymbolTable.PrintSymbolTable(CurSymbolTable);
        CurSymbolTable = CurSymbolTable.ParentSymbolTable;

    }

    public void BlockItem() throws IOException{
        // BlockItem → Decl | Stmt
        if(lexicalUnits.get(UnitIndex).value.equals("const")||lexicalUnits.get(UnitIndex).value.equals("int")){
            CreateTreeNode("Decl");
            Decl();
            Reverse();
        }
        else{
            CreateTreeNode("Stmt");
            Stmt();
            Reverse();
        }
    }
    public void FuncFParams() throws IOException{
        // FuncFParams → FuncFParam { ',' FuncFParam }
        CreateTreeNode("FuncFParam");
        FuncFParam();
        Reverse();
        while(lexicalUnits.get(UnitIndex).value.equals(",")){
            CreateLeafNode(",");
            CreateTreeNode("FuncFParam");
            FuncFParam();
            Reverse();
        }

    }
    public void FuncFParam() throws IOException{
        // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        CurParamDataType = lexicalUnits.get(UnitIndex).value;
        CreateTreeNode("BType");
        BType();
        Reverse();
//        CreateTreeNode("Ident");
        Ident("Param");
//        Reverse();
        int TypeFlag = 0;
        if(lexicalUnits.get(UnitIndex).value.equals("[")){
            TypeFlag = 1;
            CreateLeafNode("[");
            CreateLeafNode("]");
            while(lexicalUnits.get(UnitIndex).value.equals("[")){
                TypeFlag = 2;
                CreateLeafNode("[");
                CreateTreeNode("ConstExp");
                ConstExp();
                Reverse();
                CreateLeafNode("]");
            }
        }

        Symbol tempParam = new Symbol(CurParam.Level,CurParam.name,"Param",CurParam.dataType,TypeFlag);
        CurParamList.add(tempParam);
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

        if(lexicalUnits.get(UnitIndex).value.equals("if")){
            CreateLeafNode("if");
            CreateLeafNode("(");
            CreateTreeNode("Cond");
            Cond();
            Reverse();
            CreateLeafNode(")");
            CreateTreeNode("Stmt");
            Stmt();
            Reverse();
            if(lexicalUnits.get(UnitIndex).value.equals("else")){
                CreateLeafNode("else");
                CreateTreeNode("Stmt");
                Stmt();
                Reverse();
            }
        }
        else if(lexicalUnits.get(UnitIndex).value.equals("for")){
            CreateLeafNode("for");
            CreateLeafNode("(");
            if(!lexicalUnits.get(UnitIndex).value.equals(";")){
                CreateTreeNode("ForStmt");
                ForStmt();
                Reverse();
            }
            CreateLeafNode(";");
            if(!lexicalUnits.get(UnitIndex).value.equals(";")){
                CreateTreeNode("Cond");
                Cond();
                Reverse();
            }
            CreateLeafNode(";");
            if(!lexicalUnits.get(UnitIndex).value.equals(")")){
                CreateTreeNode("ForStmt");
                ForStmt();
                Reverse();
            }
            CreateLeafNode(")");
            InCirculate ++;
            CreateTreeNode("Stmt");
            Stmt();
            Reverse();
            InCirculate --;
        }
        else if(lexicalUnits.get(UnitIndex).value.equals("break")){
            if (InCirculate<=0){

            }
            CreateLeafNode("break");
            CreateLeafNode(";");
        }
        else if(lexicalUnits.get(UnitIndex).value.equals("continue")){
            if (InCirculate<=0){

            }
            CreateLeafNode("continue");
            CreateLeafNode(";");
        }
        else if(lexicalUnits.get(UnitIndex).value.equals("return")){
            CreateLeafNode("return");
            if(!lexicalUnits.get(UnitIndex).value.equals(";")){
                CreateTreeNode("Exp");
                Exp();
                Reverse();
            }
            CreateLeafNode(";");
        }
        else if(lexicalUnits.get(UnitIndex).value.equals("printf")){
            CreateLeafNode("printf");
            CreateLeafNode("(");
            CreateLeafNode("FormatString");
            while(lexicalUnits.get(UnitIndex).value.equals(",")){
                CreateLeafNode(",");
                CreateTreeNode("Exp");
                Exp();
                Reverse();
            }

            CreateLeafNode(")");
            CreateLeafNode(";");

        }
//        else if (lexicalUnits.get(UnitIndex).value.equals(";")) {
//            CreateLeafNode(";");
//        }
        else if (lexicalUnits.get(UnitIndex).value.equals("{")) {
            CreateTreeNode("Block");
            Block();
            Reverse();
        }


        //Exp 的开头是 Ident / (  / Number / + / - / !
        //LVal 的开头是 Ident

        //stmt -> LVal =  Exp  ;
        //     -> LVal = getint ( ) ;
        //     -> [Exp];
        // LVal → Ident {'[' Exp ']'}


        else if(lexicalUnits.get(UnitIndex).value.equals(";")){
            CreateLeafNode(";");
        }
        else if (lexicalUnits.get(UnitIndex).value.equals("(")||lexicalUnits.get(UnitIndex).token.equals("INTCON")) {
            CreateTreeNode("Exp");
            Exp();
            Reverse();
            CreateLeafNode(";");
        }
        else if (lexicalUnits.get(UnitIndex).token.equals("IDENFR")) {

            //判断是否是赋值语句
            int denghao = UnitIndex;
            int fenhao = UnitIndex;
            while(!lexicalUnits.get(fenhao).value.equals(";")){
                fenhao++;
                if(fenhao>=lexicalUnits.size())break;
            }
            while(!lexicalUnits.get(denghao).value.equals("=")){
                if (denghao >= fenhao) {
                    break;
                }
                denghao++;
            }

            if(denghao < fenhao){
                //是赋值语句
                // a[b+1] = c
                CreateTreeNode("LVal");
                LVal();
                Reverse();
                CreateLeafNode("=");
                if (lexicalUnits.get(UnitIndex).value.equals("getint")) {
                    CreateLeafNode("getint");
                    CreateLeafNode("(");
                    CreateLeafNode(")");
                    CreateLeafNode(";");
                }
                else{
                    CreateTreeNode("Exp");
                    Exp();
                    Reverse();
                    CreateLeafNode(";");
                }
            }
            else{
                //不是赋值语句
                if(!lexicalUnits.get(UnitIndex).value.equals(";")){
                    CreateTreeNode("Exp");
                    Exp();
                    Reverse();
                }
                CreateLeafNode(";");
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

        }else if((lexicalUnits.get(UnitIndex).value.equals("+"))||(lexicalUnits.get(UnitIndex).value.equals("-"))||(lexicalUnits.get(UnitIndex).value.equals("!"))){
            CreateTreeNode("Exp");
            Exp();
            Reverse();
            CreateLeafNode(";");
        }
    }

    public void Cond() throws IOException{
        // Cond → LOrExp
        CreateTreeNode("LOrExp");
        LOrExp();
        Reverse();
    }
    public void LOrExp() throws IOException{
        // LOrExp → LAndExp { '||' LAndExp }
        //  LOrExp → LAndExp | LOrExp '||' LAndExp
        CreateTreeNode("LAndExp");
        LAndExp();
        Reverse();

        if(lexicalUnits.get(UnitIndex).value.equals("||")){
            CreateLeafNode("||");
            CreateTreeNode("LOrExp");
            LOrExp();
            Reverse();
        }
    }
    public void LAndExp() throws IOException{
        // LAndExp → EqExp { '&&' EqExp }
        // LAndExp → EqExp | LAndExp '&&' EqExp
        CreateTreeNode("EqExp");
        EqExp();
        Reverse();
        if(lexicalUnits.get(UnitIndex).value.equals("&&")){
            CreateLeafNode("&&");
            CreateTreeNode("LAndExp");
            LAndExp();
            Reverse();
        }
    }
    public void EqExp() throws IOException{
        //    相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
        //    相等性表达式 EqExp → RelExp | RelExp ('==' | '!=') EqExp
        // EqExp → RelExp { ('==' | '!=') RelExp }
        CreateTreeNode("RelExp");
        RelExp();
        Reverse();
        if(lexicalUnits.get(UnitIndex).value.equals("==")||lexicalUnits.get(UnitIndex).value.equals("!=")){
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);
            CreateTreeNode("EqExp");
            EqExp();
            Reverse();
        }


    }
    public void RelExp() throws IOException{
        // RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        CreateTreeNode("AddExp");
        AddExp();
        Reverse();
        if(lexicalUnits.get(UnitIndex).value.equals("<")||lexicalUnits.get(UnitIndex).value.equals(">")||lexicalUnits.get(UnitIndex).value.equals("<=")||lexicalUnits.get(UnitIndex).value.equals(">=")){
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);
            CreateTreeNode("RelExp");
            RelExp();
            Reverse();
        }
    }
    public void MainFuncDef() throws IOException{
        // MainFuncDef → 'int' 'main' '(' ')' Block
        CreateLeafNode("int");
        CreateLeafNode("main");
        CreateLeafNode("(");
        CreateLeafNode(")");
        CreateTreeNode("Block");
        Block();
        Reverse();
    }

    public void FuncType() throws IOException{
        // FuncType → 'void' | 'int'
        if(lexicalUnits.get(UnitIndex).value.equals("void")||lexicalUnits.get(UnitIndex).value.equals("int")){
            CreateLeafNode(lexicalUnits.get(UnitIndex).value);
        }
        else{
            MyError.myerror(lexicalUnits.get(UnitIndex),"FuncType");
        }

    }
}
