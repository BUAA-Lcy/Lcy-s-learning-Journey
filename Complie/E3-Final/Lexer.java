import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;


public class Lexer{
    public static boolean isWhitespaceOrNewline(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }
    public static ArrayList<Lexical_unit> LexicalUnitArray = new  ArrayList<>();
    public static final Map<String, String> TOKENS = new LinkedHashMap<>();
    // tokens value

    static {
        // 关键字和保留字
        TOKENS.put("const", "CONSTTK");
        TOKENS.put("int", "INTTK");
        TOKENS.put("main", "MAINTK");
        TOKENS.put("for", "FORTK");
        TOKENS.put("if", "IFTK");
        TOKENS.put("else", "ELSETK");
        TOKENS.put("getint", "GETINTTK");
        TOKENS.put("printf", "PRINTFTK");
        TOKENS.put("break", "BREAKTK");
        TOKENS.put("return", "RETURNTK");
        TOKENS.put("continue", "CONTINUETK");
        TOKENS.put("void", "VOIDTK");

        // 运算符和分隔符
        //从前往后扫，先扫复杂的，再扫简单的
        TOKENS.put("&&", "AND");
        TOKENS.put("/", "DIV");
        TOKENS.put(";", "SEMICN");
        TOKENS.put("||", "OR");
        TOKENS.put("%", "MOD");
        TOKENS.put(",", "COMMA");
        TOKENS.put("<=", "LEQ");
        TOKENS.put(">=", "GEQ");
        TOKENS.put("==", "EQL");
        TOKENS.put("!=", "NEQ");
        TOKENS.put("+", "PLUS");
        TOKENS.put("-", "MINU");
        TOKENS.put("(", "LPARENT");
        TOKENS.put(")", "RPARENT");
        TOKENS.put("[", "LBRACK");
        TOKENS.put("]", "RBRACK");
        TOKENS.put("{", "LBRACE");
        TOKENS.put("}", "RBRACE");
        TOKENS.put("!", "NOT");
        TOKENS.put("*", "MULT");
        TOKENS.put("=", "ASSIGN");
        TOKENS.put("<", "LSS");
        TOKENS.put(">", "GRE");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("pre_output.txt"));

        String line;
        int linenum = 0;
        boolean inBlockComment = false; // 表示是否正在多行
        // 注释中

        while ((line = br.readLine()) != null) {
            int index = 0;
            linenum++;

            //跳过多行注释
            if(inBlockComment) {
                if(line.contains("*/")) {
                    index = line.indexOf("*/") + 2;
                    inBlockComment = false;
                } else {
                    continue;
                }
            }


            while (index < line.length()) {
                //处理空格和换行符
                if (isWhitespaceOrNewline(line.charAt(index))) {
                    index++;
                    continue;
                }
                // 处理单行注释
                if (line.substring(index).startsWith("//")) {
                    break; // 一旦找到//，则忽略此行的其余部分
                }

                //处理多行注释
                if (line.substring(index).startsWith("/*")) {
                    inBlockComment = true;
                    if(line.substring(index).contains("*/")) {
                        index = line.indexOf("*/", index) + 2;
                        inBlockComment = false;
                    } else {
                        break;
                    }
                }

                // 识别整数常数
                Matcher intMatcher = Pattern.compile("\\d+").matcher(line.substring(index));
                if (intMatcher.find() && intMatcher.start() == 0) {
                    LexicalUnitArray.add(new Lexical_unit(linenum,"INTCON",intMatcher.group()));
                    bw.write("INTCON " + intMatcher.group());
                    bw.newLine();
                    index += intMatcher.end();
                    continue;

                }
                // 识别标识符
                // 对于索引变量index，我们从字符串的这一位置开始查找标识符
                // 识别标识符
                if (index < line.length()) {
                    boolean isIdentifierStart = Character.isLetter(line.charAt(index)) || line.charAt(index) == '_';
                    if (isIdentifierStart) {
                        StringBuilder idBuilder = new StringBuilder();
                        while (index < line.length() && (Character.isLetterOrDigit(line.charAt(index)) || line.charAt(index) == '_')) {
                            idBuilder.append(line.charAt(index));
                            index++;
                        }
                        String id = idBuilder.toString();
                        if (TOKENS.containsKey(id)) {
                            LexicalUnitArray.add(new Lexical_unit(linenum,TOKENS.get(id),id));
                            bw.write(TOKENS.get(id) + " " + id);
                        } else {
                            LexicalUnitArray.add(new Lexical_unit(linenum,"IDENFR",id));
                            bw.write("IDENFR " + id);
                        }
                        bw.newLine();
                        continue;
                    }
                }

                // 识别字符串常数
                Matcher strMatcher = Pattern.compile("\".*?\"").matcher(line.substring(index));
                if (strMatcher.find() && strMatcher.start() == 0) {

                    LexicalUnitArray.add(new Lexical_unit(linenum,"STRCON", strMatcher.group()));
                    bw.write("STRCON " + strMatcher.group());
                    bw.newLine();
                    index += strMatcher.end();
                    continue;
                }

                // 识别其他符号
                boolean matched = false;
                for (String token : TOKENS.keySet()) {
                    if (line.substring(index).startsWith(token)) {
                        LexicalUnitArray.add(new Lexical_unit(linenum,TOKENS.get(token),token));
                        bw.write(TOKENS.get(token) + " " + token);
                        bw.newLine();
                        index += token.length();
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    index++;
                }
            }
        }

        br.close();
        bw.close();
    }
}
