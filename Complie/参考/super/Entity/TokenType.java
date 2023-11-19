package Entity;

public enum TokenType {
    IDENFR,        // 标识符
    INTCON,        // 整数常量
    STRCON,        // 字符串常量
    MAINTK,        // main 关键字
    CONSTTK,       // const 关键字
    INTTK,         // int 关键字
    BREAKTK,       // break 关键字
    CONTINUETK,    // continue 关键字
    IFTK,          // if 关键字
    ELSETK,        // else 关键字
    NOT,           // 逻辑非运算符 "!"
    AND,           // 逻辑与运算符 "&&"
    OR,            // 逻辑或运算符 "||"
    FORTK,         // for 关键字
    GETINTTK,      // getint 关键字
    PRINTFTK,      // printf 关键字
    RETURNTK,      // return 关键字
    PLUS,          // 加法运算符 "+"
    MINU,          // 减法运算符 "-"
    VOIDTK,        // void 关键字
    MULT,          // 乘法运算符 "*"
    DIV,           // 除法运算符 "/"
    MOD,           // 取模运算符 "%"
    LSS,           // 小于运算符 "<"
    LEQ,           // 小于等于运算符 "<="
    GRE,           // 大于运算符 ">"
    GEQ,           // 大于等于运算符 ">="
    EQL,           // 等于运算符 "=="
    NEQ,           // 不等于运算符 "!="
    ASSIGN,        // 赋值运算符 "="
    SEMICN,        // 分号 ";"
    COMMA,         // 逗号 ","
    LPARENT,       // 左括号 "("
    RPARENT,       // 右括号 ")"
    LBRACK,        // 左方括号 "["
    RBRACK,        // 右方括号 "]"
    LBRACE,        // 左大括号 "{"
    RBRACE,        // 右大括号 "}"
    EOF,           // 越界符
}