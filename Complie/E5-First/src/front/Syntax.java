package front;

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
常量初值 ConstInitVal → ConstExp
'{' [ ConstInitVal { ',' ConstInitVal } ] '}'
变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
变量定义 VarDef → Ident { '[' ConstExp ']' } Ident { '[' ConstExp ']' } '=' InitVal
变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
函数类型 FuncType → 'void' | 'int'
函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
语句块 Block → '{' { BlockItem } '}'语句块项 BlockItem → Decl | Stmt语句 Stmt → LVal '=' Exp ';'
[Exp] ';'
Block
'if' '(' Cond ')' Stmt [ 'else' Stmt ]
'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt 'break' ';' | 'continue' ';'
'return' [Exp] ';'
LVal '=' 'getint''('')'';'
'printf''('FormatString{','Exp}')'';'
语句 ForStmt → LVal '=' Exp
表达式 Exp → AddExp  注：SysY 表达式是int 型表达式
条件表达式 Cond → LOrExp
左值表达式 LVal → Ident {'[' Exp ']'}
基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number
数值 Number → IntConst
一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
UnaryOp UnaryExp单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
函数实参表 FuncRParams → Exp { ',' Exp }
乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
逻辑与表达式 LAndExp → EqExp | LAnd

<BlockItem>, <Decl>, <BType>除外，都要输出语法成分
{}不加引号的大括号表示可以出现1-n次
[]不加引号的中括号表示可以出现0-1次
 */


public class Syntax{
    public static int UnitIndex = 0;
    //获取词法分析结果
    public static  ArrayList<Lexical_unit> lexicalUnits = Lexer.LexicalUnitArray;
    public static SymbolTable GlobalSymbolTable = new SymbolTable(null,0);
    public static SymbolTable CurSymbolTable  = GlobalSymbolTable;
    public static Symbol CurParam ;
    public static List<Symbol> CurParamList = new ArrayList<>();
    public static FuncTable CurFuncTable;
    public static String CurFuncName ;
    public static String CurFuncType;
    public static String CurIdentType;
    public static String CurParamDataType;


    public static void main(String[] args) throws IOException  {
        System.out.println("Syntax analysis has begun.");

        SyntaxFunction func = new SyntaxFunction();
        //测试词法分析结果
        // func.printLexicalUnit();
        func.CompUnit();

        SymbolTable.printFunctionInfo(SyntaxFunction.FuncTableMap,SymbolTable.SymbolTableWriter);
        SyntaxFunction.cleanup();
        System.out.println("Syntax analysis finished.");
        SyntaxFunction.writeToFile(SyntaxFunction.OutputStringSet);
        MyError.PrintErrorList();
        //打印SyntaxFunction.OutputStringSet的内容

    }
}
