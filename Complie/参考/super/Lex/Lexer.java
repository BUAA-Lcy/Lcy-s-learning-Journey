package Lex;

import Config.Properties;
import Entity.Token;
import Entity.TokenType;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class LexWriter{
    public LexWriter(){ }
    public void write(TokenType type, String value, int lineNum){
        Lexer.lexTokenMap.add(new Token(type,value,lineNum));
    }
}



public class Lexer {
    //key:单词名称， value：类别码
    public static LinkedHashMap<String, String> reserveWords = new LinkedHashMap<>();

    // key:类别码， value：单词名称
    //作为词法分析程序的输出，传递给语法分析程序
    public static ArrayList<Token> lexTokenMap = new ArrayList<>();
    public static String str;

    public static LexWriter writer;


    static {
        // 保留字
        reserveWords.put("main", "MAINTK");
        reserveWords.put("const", "CONSTTK");
        reserveWords.put("int", "INTTK");
        reserveWords.put("break", "BREAKTK");
        reserveWords.put("continue", "CONTINUETK");
        reserveWords.put("if", "IFTK");
        reserveWords.put("else", "ELSETK");
        reserveWords.put("for", "FORTK");
        reserveWords.put("getint", "GETINTTK");
        reserveWords.put("printf", "PRINTFTK");
        reserveWords.put("return", "RETURNTK");
        reserveWords.put("void", "VOIDTK");
        reserveWords.put("&&", "AND");
        reserveWords.put("||", "OR");
        reserveWords.put("+", "PLUS");
        reserveWords.put("-", "MINU");
        reserveWords.put("*", "MULT");
        reserveWords.put("/", "DIV");
        reserveWords.put("%", "MOD");
        reserveWords.put(";", "SEMICN");
        reserveWords.put(",", "COMMA");
        reserveWords.put("<=", "LEQ");
        reserveWords.put(">=", "GEQ");
        reserveWords.put("==", "EQL");
        reserveWords.put("!=", "NEQ");
        reserveWords.put("!", "NOT");
        reserveWords.put("=", "ASSIGN");
        reserveWords.put("<", "LSS");
        reserveWords.put(">", "GRE");
        reserveWords.put("(", "LPARENT");
        reserveWords.put(")", "RPARENT");
        reserveWords.put("[", "LBRACK");
        reserveWords.put("]", "RBRACK");
        reserveWords.put("{", "LBRACE");
        reserveWords.put("}", "RBRACE");

        // 书法笔
        writer = new LexWriter();

    }

    //Pattern & Matcher
    public static Pattern integer_pattern = Pattern.compile("\\d+");
    public static Pattern string_pattern = Pattern.compile("\".*?\"");
    public static Pattern identifier_pattern = Pattern.compile("[a-zA-Z_]\\w*");
    public static boolean isSkipWord(char c){
        if(c==' '||c=='\n'||c=='\r'||c=='\t')
            return true;
        else
            return false;
    }

    public static void main(String[] args) throws IOException {

        String filePath = "";
        if(Properties.isDebug){
            filePath = args[0];
        }
        else {
            filePath = "testfile.txt";
        }
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);

        int currentLineNum = 0;
        boolean comment_flag = false;//多行注释

        while((str = reader.readLine()) != null){
            int index = 0;
            currentLineNum++;
            //跳过注释
            if(comment_flag){
                if(str.contains("*/")){
                    index = str.indexOf("*/")+2;
                    comment_flag = false;
                }
                else continue;
            }

            while(index < str.length()){

                //空格
                if(isSkipWord(str.charAt(index))){
                    index++;
                    continue;
                }

                String rest = str.substring(index);//index后的子串

                //单行注释
                if(rest.startsWith("//")){
                    break;//跳出该行
                }

                //处理多行注释
                if(rest.startsWith("/*")){
                    comment_flag = true;
                    if(rest.contains("*/")) {
                        index = str.indexOf("*/") + 2;
                        comment_flag = false;
                    }
                    else {
                        break;//跳出该行
                    }
                }

                rest = str.substring(index);
                Matcher integer_matcher = integer_pattern.matcher(rest);
                Matcher string_matcher = string_pattern.matcher(rest);
                Matcher idetifier_matcher = identifier_pattern.matcher(rest);

                //常数
                if(integer_matcher.find() && integer_matcher.start()==0){
                    writer.write(TokenType.INTCON,integer_matcher.group(0),currentLineNum);
                    index += integer_matcher.group(0).length();
                    continue;
                }

                //字符串
                if(string_matcher.find() && string_matcher.start()==0){
                    writer.write(TokenType.STRCON,string_matcher.group(0),currentLineNum);
                    index += string_matcher.group(0).length();
                    continue;
                }


                //标识符
                if(idetifier_matcher.find() && idetifier_matcher.start()==0){
                    String content = idetifier_matcher.group(0);
                    if(reserveWords.containsKey(content)){
                        writer.write(TokenType.valueOf(reserveWords.get(content)),content,currentLineNum);
                        index += content.length();
                        continue;
                    }
                    else{
                        writer.write(TokenType.IDENFR,content,currentLineNum);
                        index += content.length();
                        continue;
                    }

                }

                boolean match_succeed = false;
                // 识别保留字
                for (String token : reserveWords.keySet()) {
                    if (rest.startsWith(token)) {
                        writer.write(TokenType.valueOf(reserveWords.get(token)),token,currentLineNum);
                        index += token.length();
                        match_succeed = true;
                        break;
                    }
                }
                if(match_succeed) continue;
                else {
                    index++;
                    continue;
                }


            }

        }
        reader.close();
    }
}
