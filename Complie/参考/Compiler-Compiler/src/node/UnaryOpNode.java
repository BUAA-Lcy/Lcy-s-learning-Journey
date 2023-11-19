package node;

import frontend.Parser;
import token.Token;
import utils.IOUtils;

public class UnaryOpNode {
    // UnaryOp -> '+' | '−' | '!'

    Token token;

    public UnaryOpNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void print() {
        IOUtils.write(token.toString());
        IOUtils.write(Parser.nodeType.get(NodeType.UnaryOp));
    }

    public String getStr() {
        return token.getContent();
    }
}
