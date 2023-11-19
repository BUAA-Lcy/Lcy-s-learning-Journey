package Syntax;

//短路求值
import Config.Properties;
import Entity.Token;
import Entity.TokenType;
import Lex.Lexer;
import java.io.*;
import java.util.*;

public class SyntaxAnalyzer {
    private BufferedWriter debugWriter;
    private BufferedWriter writer;
    private String currentToken;
    private TokenType currentTokenType;
    private int currentLineNum = 1;
    private int currentTokenIndex = -1;

    private ArrayList<Token> tokens;

    public SyntaxAnalyzer(){
    }

    public void init(String filepath) throws IOException {
        writer = new BufferedWriter(new FileWriter(filepath));
        tokens = Lexer.lexTokenMap;
        advance();
    }
    private void advance() throws IOException {
        if (currentTokenIndex+1 < tokens.size()) {
            currentTokenIndex++;
            Token token = tokens.get(currentTokenIndex);
            currentToken = token.getValue();
            currentTokenType = token.getType();
            currentLineNum = token.getLineNum();
        }
        else {
            currentTokenType = TokenType.EOF;
            currentToken = "null";
        }
    }
    private Token peekToken(int number) { //往后偷看几个
        if(currentTokenIndex + number < tokens.size()){
            return tokens.get(currentTokenIndex + number);
        }
        else {
            return new Token(TokenType.EOF, "null", currentLineNum);
        }

    }
    private void match(TokenType expectedTokenType) throws IOException {
        if (currentTokenType == expectedTokenType) {
            writer.write(currentTokenType + " " + currentToken);
            writer.newLine();
            advance();
        } else {
            throw new RuntimeException("Syntax error: Expected token type " + expectedTokenType +
                    ", but found " + currentTokenType + ". currentLine: " + currentLineNum);
        }
    }
    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private void CompUnit() throws IOException {

        while (isDecl()) {
            decl();
        }
        while (isFuncDef()) {
            funcDef();
        }
        mainFuncDef();
        writer.write("<CompUnit>\n");
    }

    private boolean isDecl() { //只适用于CompUnit
        if(peekToken(2).getType() == TokenType.LPARENT){
            return false;
        }
        return true;
    }
    private boolean isFuncDef() { //只适用于CompUnit
        if(peekToken(1).getType() == TokenType.MAINTK){
            return false;
        }
        return true;
    }
    // Decl → ConstDecl | VarDecl
    private void decl() throws IOException {
        if (currentTokenType == TokenType.CONSTTK) {
            constDecl();
        } else if (currentTokenType == TokenType.INTTK) {
            varDecl();
        } else {
            throw new RuntimeException("Syntax error: Expected declaration, but found " + currentTokenType + ". currentLine: " + currentLineNum);
        }
    }
    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private void constDecl() throws IOException {
        match(TokenType.CONSTTK);
        bType();
        constDef();
        while (currentTokenType == TokenType.COMMA) {
            match(TokenType.COMMA);
            constDef();
        }
        match(TokenType.SEMICN);
        writer.write("<ConstDecl>\n");
    }
    // BType → 'int'
    private void bType() throws IOException {
        match(TokenType.INTTK);
//        writer.write("<BType>\n");
    }
    // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    private void constDef() throws IOException {
        match(TokenType.IDENFR);
        while (currentTokenType == TokenType.LBRACK) {
            match(TokenType.LBRACK);
            constExp();
            match(TokenType.RBRACK);
        }
        match(TokenType.ASSIGN);
        constInitVal();
        writer.write("<ConstDef>\n");
    }
    //    ConstExp → AddExp
    private void constExp() throws IOException {
        addExp();
        writer.write("<ConstExp>\n");
    }
    /*
    常量初值 ConstInitVal → ConstExp
                          | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     */
    private void constInitVal() throws IOException {
        if (currentTokenType == TokenType.LBRACE) {
            match(TokenType.LBRACE);
            if (currentTokenType != TokenType.RBRACE) {
                constInitVal();
                while (currentTokenType == TokenType.COMMA) {
                    match(TokenType.COMMA);
                    constInitVal();
                }
            }
            match(TokenType.RBRACE);
        } else {
            constExp();
        }
        writer.write("<ConstInitVal>\n");
    }
    // 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
    private void varDecl() throws IOException {
        bType();
        varDef();
        while (currentTokenType == TokenType.COMMA) {
            match(TokenType.COMMA);
            varDef();
        }
        match(TokenType.SEMICN);
        writer.write("<VarDecl>\n");
    }
    /*
    变量定义 VarDef → Ident { '[' ConstExp ']' }
                    | Ident { '[' ConstExp ']' } '=' InitVal
     包含普通变量、一维数组、二维数组定义
     */
    private void varDef() throws IOException {
        match(TokenType.IDENFR);
        if (currentTokenType == TokenType.LBRACK) {
            match(TokenType.LBRACK);
            constExp();
            match(TokenType.RBRACK);
        }
        if (currentTokenType == TokenType.LBRACK) {
            match(TokenType.LBRACK);
            constExp();
            match(TokenType.RBRACK);
        }
        if(currentTokenType == TokenType.ASSIGN){
            match(TokenType.ASSIGN);
            initVal();
        }
        writer.write("<VarDef>\n");
    }
    // 变量初值 InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    // 1.表达式初值 2.一维数组初值 3.二维数组初值
    private void initVal() throws IOException {
        if (currentTokenType == TokenType.LBRACE) {
            match(TokenType.LBRACE);
            if (currentTokenType != TokenType.RBRACE) {
                initVal();
                while (currentTokenType == TokenType.COMMA) {
                    match(TokenType.COMMA);
                    initVal();
                }
            }
            match(TokenType.RBRACE);
        } else {
            exp();
        }
        writer.write("<InitVal>\n");
    }

    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private void funcDef() throws IOException {
        funcType();
        match(TokenType.IDENFR);
        match(TokenType.LPARENT);
        if (currentTokenType != TokenType.RPARENT) {
            funcFParams();
        }
        match(TokenType.RPARENT);
        block();
        writer.write("<FuncDef>\n");
    }
    //    函数类型 FuncType → 'void' | 'int'
    private void funcType() throws IOException {
        if (currentTokenType == TokenType.VOIDTK) {
            match(TokenType.VOIDTK);
        } else if (currentTokenType == TokenType.INTTK) {
            match(TokenType.INTTK);
        } else {
            throw new RuntimeException("Syntax error: Expected function type, but found " + currentTokenType);
        }
        writer.write("<FuncType>\n");
    }
    //    函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
    private void funcFParams() throws IOException {
        funcFParam();
        while (currentTokenType == TokenType.COMMA) {
            match(TokenType.COMMA);
            funcFParam();
        }
        writer.write("<FuncFParams>\n");
    }
    //    函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    // 注意，按照文法的话，一维数组形参不可以写长度
    private void funcFParam() throws IOException {
        bType();
        match(TokenType.IDENFR);
        if (currentTokenType == TokenType.LBRACK) {
            match(TokenType.LBRACK);
            match(TokenType.RBRACK);
        }
        if(currentTokenType == TokenType.LBRACK){
            match(TokenType.LBRACK);
            constExp();
            match(TokenType.RBRACK);
        }
        writer.write("<FuncFParam>\n");
    }

    private void mainFuncDef() throws IOException {
        match(TokenType.INTTK);
        match(TokenType.MAINTK);
        match(TokenType.LPARENT);
        match(TokenType.RPARENT);
        block();
        writer.write("<MainFuncDef>\n");
    }
    //'{' { BlockItem } '}'
    private void block() throws IOException {
        match(TokenType.LBRACE);
        while (currentTokenType!= TokenType.RBRACE) {
            blockItem();
        }
        match(TokenType.RBRACE);
        writer.write("<Block>\n");
    }
    //    语句块项 BlockItem → Decl | Stmt
    private void blockItem() throws IOException{
        if(currentTokenType == TokenType.LBRACE) {
            stmt();
        }
        else if(currentTokenType == TokenType.INTTK || peekToken(1).getType() == TokenType.INTTK){
            decl();
        }
        else {
            stmt();
        }
    }

    /*Stmt →
    |[Exp] ';'
    |Block
    |'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    |'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    |'break' ';' | 'continue' ';
    |'return' [Exp] ';'
    |LVal '=' Exp ';'
    |LVal '=' 'getint''('')'';'
    |'printf''('FormatString{','Exp}')'';'
     */
    private void stmt() throws IOException {
        if(currentTokenType == TokenType.LBRACE){
            block();
        }
        else if(currentTokenType == TokenType.IFTK){
            match(TokenType.IFTK);
            match(TokenType.LPARENT);
            cond();
            match(TokenType.RPARENT);
            stmt();
            if (currentTokenType == TokenType.ELSETK) {
                match(TokenType.ELSETK);
                stmt();
            }
        }
        else if(currentTokenType == TokenType.FORTK){
            match(TokenType.FORTK);
            match(TokenType.LPARENT);
            if(currentTokenType!= TokenType.SEMICN) {
                forStmt();
            }
            match(TokenType.SEMICN);
            if(currentTokenType!= TokenType.SEMICN) {
                cond();
            }
            match(TokenType.SEMICN);
            if(currentTokenType!= TokenType.RPARENT) {
                forStmt();
            }
            match(TokenType.RPARENT);
            stmt();
        }
        else if(currentTokenType == TokenType.BREAKTK){
            if(currentTokenType!= TokenType.SEMICN) {
                match(TokenType.BREAKTK);
            }
            match(TokenType.SEMICN);
        }
        else if(currentTokenType == TokenType.CONTINUETK){
            if(currentTokenType!= TokenType.SEMICN) {
                match(TokenType.CONTINUETK);
            }
            match(TokenType.SEMICN);
        }
        else if(currentTokenType == TokenType.RETURNTK){
            match(TokenType.RETURNTK);
            if(currentTokenType != TokenType.SEMICN){
                exp();
            }
            match(TokenType.SEMICN);
        }
        else if(currentTokenType == TokenType.PRINTFTK) {
            match(TokenType.PRINTFTK);
            match(TokenType.LPARENT);
            T_formatString();
            while(currentTokenType != TokenType.RPARENT) {
                match(TokenType.COMMA);
                exp();
            }
            match(TokenType.RPARENT);
            match(TokenType.SEMICN);
        }
        else if(currentTokenType == TokenType.SEMICN){
            match(TokenType.SEMICN);
        }
        else {
            // 先找后面多少个是分号，确定一句话什么时候结束
            // 再找多少个是等号，如果等号在分号前，当前这个句子就是赋值语句，转到lVAl
            int index_semicn = 0;
            int index_assign = 0;
            while (peekToken(index_semicn).getType() != TokenType.SEMICN) {
                index_semicn++;
            }
            while (peekToken(index_assign).getType() != TokenType.ASSIGN) {
                if(index_assign >= index_semicn){
                    break;
                } //如果index_assgin超过了index_semicn，说明没有等号，不是赋值语句
                index_assign++;
            }
            if(index_assign < index_semicn){ //赋值语句
                lVal();
                match(TokenType.ASSIGN);
                if(currentTokenType == TokenType.GETINTTK){//LVal '=' 'getint''('')'';'
                    match(TokenType.GETINTTK);
                    match(TokenType.LPARENT);
                    match(TokenType.RPARENT);
                    match(TokenType.SEMICN);
                }
                else { //LVal '=' Exp ';'
                    exp();
                    match(TokenType.SEMICN);
                }
            }
            else{ //[Exp] ';'
                if(currentTokenType != TokenType.SEMICN) {
                    exp();
                }
                match(TokenType.SEMICN);
            }
        }
        writer.write("<Stmt>\n");
    }
    //终结符,
    //注意，这里有一些格式需求，所以留一个函数，存放检查格式和处理错误的接口
    private void T_formatString() throws IOException {
        match(TokenType.STRCON);
    }
    // ForStmt → LVal '=' Exp
    private void forStmt() throws IOException {
        lVal();
        match(TokenType.ASSIGN);
        exp();
        writer.write("<ForStmt>\n");
    }
    //    条件表达式 Cond → LOrExp
    private void cond() throws IOException {
        LOrExp();
        writer.write("<Cond>\n");
    }
    // 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
    private void LOrExp() throws IOException {
        LAndExp();
        writer.write("<LOrExp>\n");
        while (currentTokenType == TokenType.OR) {
            match(currentTokenType);
            LAndExp();
            writer.write("<LOrExp>\n");
        }
    }

    //    逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
    private void LAndExp() throws IOException{
        EqExp();
        writer.write("<LAndExp>\n");
        while (currentTokenType == TokenType.AND) {
            match(currentTokenType);
            EqExp();
            writer.write("<LAndExp>\n");
        }
    }
    //    相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
    private void EqExp() throws IOException {
        relExp();
        writer.write("<EqExp>\n");
        while (currentTokenType == TokenType.EQL || currentTokenType == TokenType.NEQ) {
            match(currentTokenType);
            relExp();
            writer.write("<EqExp>\n");
        }
    }
    //    关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private void relExp() throws IOException {
        addExp();
        writer.write("<RelExp>\n");
        while (currentTokenType == TokenType.LSS || currentTokenType == TokenType.LEQ ||
                currentTokenType == TokenType.GRE || currentTokenType == TokenType.GEQ) {
            match(currentTokenType);
            addExp();
            writer.write("<RelExp>\n");
        }
    }

    //    加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
    //  a A + a A + a A
    //  AddEp
    //  AddExp + MulExp
    //  AddExp + MulExp + MulExp
    //  MulExp + MulExp + MulExp
    private void addExp() throws IOException {
        mulExp();
        writer.write("<AddExp>\n");
        while(currentTokenType == TokenType.PLUS || currentTokenType == TokenType.MINU){
            match(currentTokenType);
            mulExp();
            writer.write("<AddExp>\n");
        }

    }
    // 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private void mulExp() throws IOException {
        unaryExp();
        writer.write("<MulExp>\n");
        while (currentTokenType == TokenType.MULT || currentTokenType == TokenType.DIV || currentTokenType == TokenType.MOD) {
            match(currentTokenType);
            unaryExp();
            writer.write("<MulExp>\n");
        }

    }
    // 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    private void unaryExp() throws IOException {

        if(peekToken(1).getType() == TokenType.LPARENT && currentTokenType == TokenType.IDENFR){
            match(TokenType.IDENFR);
            match(TokenType.LPARENT);
            if(currentTokenType != TokenType.RPARENT){
                funcRParams();
            }
            match(TokenType.RPARENT);
        }
        // - + - d
        // - + - d U U U U
        else if(currentTokenType == TokenType.NOT || currentTokenType == TokenType.PLUS || currentTokenType == TokenType.MINU){
            unaryOp();
            unaryExp();
        }
        else {
            primaryExp();
        }
        writer.write("<UnaryExp>\n");
    }
    public  void unaryOp() throws IOException{
        match(currentTokenType);
        writer.write("<UnaryOp>\n");
    }
    // 函数实参表 FuncRParams → Exp { ',' Exp }
    private void funcRParams() throws IOException {
        exp();
        while(currentTokenType == TokenType.COMMA){
            match(TokenType.COMMA);
            exp();
        }
        writer.write("<FuncRParams>\n");
    }
    //    基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number
    private void primaryExp() throws IOException {
        if(currentTokenType == TokenType.LPARENT){
            match(TokenType.LPARENT);
            exp();
            match(TokenType.RPARENT);
        }
        else if(currentTokenType == TokenType.INTCON){
            number();
        }
        else {
            lVal();
        }
        writer.write("<PrimaryExp>\n");
    }
    //    LVal → Ident {'[' Exp ']'}
    private void lVal() throws IOException {
        match(TokenType.IDENFR);
        while(currentTokenType == TokenType.LBRACK){
            match(TokenType.LBRACK);
            exp();
            match(TokenType.RBRACK);
        }
        writer.write("<LVal>\n");
    }
    //    Exp → AddExp
    private void exp() throws IOException {
        addExp();
        writer.write("<Exp>\n");
    }
    //    Number → IntConst
    private void number() throws IOException {
        match(TokenType.INTCON);
        writer.write("<Number>\n");
    }

    public static void main(String[] args) {
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
        try {
            String filepath = "";
            if(Properties.isDebug) {
                filepath = args[1];
            }
            else{
                filepath = "output.txt";
            }
            syntaxAnalyzer.init(filepath);
            syntaxAnalyzer.CompUnit();
            syntaxAnalyzer.writer.close();
            System.out.println("Syntax analysis completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}