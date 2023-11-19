package node;

import frontend.Parser;
import token.Token;
import utils.IOUtils;

import java.util.List;

public class LValNode {
    // LVal -> Ident {'[' Exp ']'}
    private Token ident;
    private List<Token> leftBrackets;
    private List<ExpNode> expNodes;
    private List<Token> rightBrackets;

    public LValNode(Token ident, List<Token> leftBrackets, List<ExpNode> expNodes, List<Token> rightBrackets) {
        this.ident = ident;
        this.leftBrackets = leftBrackets;
        this.expNodes = expNodes;
        this.rightBrackets = rightBrackets;
    }

    public Token getIdent() {
        return ident;
    }

    public List<ExpNode> getExpNodes() {
        return expNodes;
    }

    public void print() {
        IOUtils.write(ident.toString());
        for (int i = 0; i < leftBrackets.size(); i++) {
            IOUtils.write(leftBrackets.get(i).toString());
            expNodes.get(i).print();
            IOUtils.write(rightBrackets.get(i).toString());
        }
        IOUtils.write(Parser.nodeType.get(NodeType.LVal));
    }

    public String getStr() {
        StringBuilder s = new StringBuilder(ident.getContent());
        for (ExpNode expNode : expNodes) {
            s.append("[").append(expNode.getStr()).append("]");
        }
        return s.toString();
    }
}
