package Entity;

public class Token{
    public TokenType type;
    public String value;
    public int lineNum;
    public Token(TokenType type,String value,int lineNum){
        this.type = type;
        this.value = value;
        this.lineNum = lineNum;
    }
    public TokenType getType(){
        return this.type;
    }
    public String getValue(){
        return this.value;
    }
    public int getLineNum(){
        return this.lineNum;
    }
}

