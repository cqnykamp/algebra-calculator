/**
* This class represents a variable symbol on the expression tree. In practice, it is
* always a leaf on the tree. The class overrides Node by giving accurate identifier functions
* and through its simplify() method. It also treats the Node's value parameter as a character
* instead of an integer.
*/
public class Variable extends Node {

    public Variable(char name) {
        this.value = name + 0;
    }

    public boolean isOperator() {
        return false;
    }

    public boolean isVariable() {
        return true;
    }

    public int evaluate() {
        return value;
    }

    public NodeWithHistory simplify() {
        NodeWithHistory wrapper = new NodeWithHistory();
        wrapper.node = new Variable((char)value);
        return wrapper;
    }

    public String text() {
        return "" + (char)value;
    }

    public String toString() {
        return "" + (char)value;
    }

}