package front;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class MyError {

    public static BufferedWriter errorbw;
    public static BufferedWriter Errorhandlebw;

    private static final BufferedWriter ErrorOutputbw ;

    static {
        try {
            errorbw = new BufferedWriter(new FileWriter("myerror.txt"));
            Errorhandlebw = new BufferedWriter(new FileWriter("DeBugError.txt"));
            ErrorOutputbw = new BufferedWriter(new FileWriter("error.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Debug(String string) throws IOException {
        Errorhandlebw.write(string+"\n");
        Errorhandlebw.flush();
    }
    //用来检查语法不对应问题的错误
    public static void myerror (Lexical_unit realUnit,String ErrorGrammarSpace) throws IOException {
        System.out.println("IN MyError");
        System.out.println("Error Index: "+ Syntax.UnitIndex);
        System.out.println("Syntax error: line "+ realUnit.linenum);
        System.out.println("Error value: "+ realUnit.value);
        System.out.println("Error token: "+ realUnit.token);
        System.out.println("Error Grammar: "+ ErrorGrammarSpace);

        errorbw.write("Error Index: "+ Syntax.UnitIndex);
        errorbw.newLine();
        errorbw.write("Syntax error: line "+ realUnit.linenum);
        errorbw.newLine();
        errorbw.write("Error value: "+ realUnit.value);
        errorbw.newLine();
        errorbw.write("Error token: "+ realUnit.token);
        errorbw.newLine();
        errorbw.write("Error Grammar: "+ ErrorGrammarSpace);
        errorbw.flush();

    }
    public static void PrintErrorList() throws IOException{
        LinkedHashMap <Integer,String> ErrorOutputMap = SyntaxFunction.ErrorMap;
        for(int i : ErrorOutputMap.keySet()){
                ErrorOutputbw.write(i+" "+ErrorOutputMap.get(i)+"\n");
        }
        ErrorOutputbw.flush();
    }

    public static boolean isValidFormatStringByStateMachine(String formatString) {
        // 检查字符串是否以双引号开始和结束
        if (!formatString.startsWith("\"") || !formatString.endsWith("\"")) {
            return false;
        }

        // 移除开始和结束的引号以方便处理
        formatString = formatString.substring(1, formatString.length() - 1);

        for (int i = 0; i < formatString.length(); i++) {
            char c = formatString.charAt(i);

            if (c == '\\') {
                // 处理转义字符
                i++; // 移到下一个字符
                if (i < formatString.length() && formatString.charAt(i) == 'n') {
                    // 是合法的 '\n'，跳过
                    continue;
                } else {
                    return false; // 除 '\n' 外其他都是非法的
                }
            } else if (c == '%') {
                // 处理格式字符
                i++; // 移到下一个字符
                if (i < formatString.length() && formatString.charAt(i) == 'd') {
                    // 是合法的 '%d'，跳过
                    continue;
                } else {
                    return false; // 除 '%d' 外其他都是非法的
                }
            } else if ((c == 32) || c == 33 || (c >= 40 && c <= 126)) {
                // ASCII字符在允许的范围内，不做处理
                continue;
            } else {
                return false; // 非法的字符
            }
        }

        return true; // 所有字符检查结束，都是合法的
    }
    public static boolean isValidFormatStringByRegex(String formatString) {
        // 定义合法字符的正则表达式
        String pattern = "^\"(%d|\\\\n|[ \\t!-~&&[^\"%\\\\]])*\"$";

        // 创建 Pattern 对象a
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(formatString);
        return m.find();
    }
    public static boolean isValidFormatString(String formatString) {
        // 定义合法字符的正则表达式
        return isValidFormatStringByStateMachine(formatString);
    }
    public static int countPercentage(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '%') {
                count++;
            }
        }
        return count;
    }

    public static void PrintSymbolList(List<Symbol> symbolList) throws IOException{
        for(Symbol symbol: symbolList){
           Errorhandlebw.write(symbol.name+" "+symbol.type+" "+symbol.dataType+" Flag: "+symbol.Flag+"\n");
        }
        Errorhandlebw.flush();
    }

}
