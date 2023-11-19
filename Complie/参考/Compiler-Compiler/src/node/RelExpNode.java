package node;

import frontend.Parser;
import token.Token;
import utils.IOUtils;

public class RelExpNode {
    // RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private AddExpNode addExpNode;
    private Token operator;
    private RelExpNode relExpNode;


    public RelExpNode(AddExpNode addExpNode, Token operator, RelExpNode relExpNode) {
        this.addExpNode = addExpNode;
        this.operator = operator;
        this.relExpNode = relExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public Token getOperator() {
        return operator;
    }

    public RelExpNode getRelExpNode() {
        return relExpNode;
    }

    public void print() {
        addExpNode.print();
        IOUtils.write(Parser.nodeType.get(NodeType.RelExp));
        if (operator != null) {
            IOUtils.write(operator.toString());
            relExpNode.print();
        }
    }
}
