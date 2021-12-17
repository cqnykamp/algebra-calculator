
/**
 * Small subclass of Node used in Parser to represent parentheses. This class should never
 * appear in the actual expression tree.
 */
public class Parenthesis extends Node {

    public Parenthesis(int value) {
        this.value = value;
    }

    public boolean isParenthesis() {
        return true;
    }

    public boolean isOperator() {
        return false;
    }


    public String toString() {
        if(value == 0) {
            return "(";
        } else {
            return ")";
        }
    }
}