
/**
* This class represents a plain integer number on the expression tree. In practice, it is
* always a leaf on the tree. The class overrides Node by giving accurate identifier functions
* and through its simplify() method.
*/
public class Number extends Node {

    public Number(int value) {
        this.value = value;
    }

    public boolean isOperator() {
        return false;
    }
    public boolean isVariable() {
        return false;
    }


    /** This is the key functionality of this class. Instead of trying to simplify more, because
     * it is a number, it knows it is a simple as possible, and so it simply returns a copy of 
     * itself.
     */ 
    public Node simplify() {
        return new Number(value);
    }

    public String text() {
        return String.valueOf(value);
    }

    public String toString() {
        return "" + value;    
    }

}