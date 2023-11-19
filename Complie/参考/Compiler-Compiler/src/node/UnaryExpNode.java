package node;

import frontend.Parser;
import token.Token;
import utils.IOUtils;

public class UnaryExpNode {
    // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    private PrimaryExpNode primaryExpNode = null;
    private Token ident = null;
    private Token leftParentToken = null;
    private FuncRParamsNode funcRParamsNode = null;
    private Token rightParentToken = null;
    private UnaryOpNode unaryOpNode = null;
    private UnaryExpNode unaryExpNode = null;

    public UnaryExpNode(PrimaryExpNode primaryExpNode) {
        this.primaryExpNode = primaryExpNode;
    }

    public UnaryExpNode(Token ident, Token leftParentToken, FuncRParamsNode funcRParamsNode, Token rightParentToken) {
        this.ident = ident;
        this.leftParentToken = leftParentToken;
        this.funcRParamsNode = funcRParamsNode;
        this.rightParentToken = rightParentToken;
    }

    public UnaryExpNode(UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    public PrimaryExpNode getPrimaryExpNode() {
        return primaryExpNode;
    }

    public Token getIdent() {
        return ident;
    }

    public Token getLeftParentToken() {
        return leftParentToken;
    }

    public FuncRParamsNode getFuncRParamsNode() {
        return funcRParamsNode;
    }

    public Token getRightParentToken() {
        return rightParentToken;
    }

    public UnaryOpNode getUnaryOpNode() {
        return unaryOpNode;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }

    public void print() {
        if (primaryExpNode != null) {
            primaryExpNode.print();
        } else if (ident != null) {
            IOUtils.write(ident.toString());
            IOUtils.write(leftParentToken.toString());
            if (funcRParamsNode != null) {
                funcRParamsNode.print();
            }
            IOUtils.write(rightParentToken.toString());
        } else {
            unaryOpNode.print();
            unaryExpNode.print();
        }
        IOUtils.write(Parser.nodeType.get(NodeType.UnaryExp));
    }

    public String getStr() {
        if (primaryExpNode != null) {
            return primaryExpNode.getStr();
        } else if (ident != null) {
            String s = ident.getContent() + leftParentToken.getContent();
            if (funcRParamsNode != null) {
                s += funcRParamsNode.getStr();
            }
            s += rightParentToken.getContent();
            return s;
        } else {
            return unaryOpNode.getStr() + unaryExpNode.getStr();
        }
    }
}
