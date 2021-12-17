import java.util.ArrayList;

/**
 * This class is a subclass of Node. It is used for any element in the expression tree which
 * is an arithmetic operator: + - * /.  This class has added on to the Node superclass class by 
 * specifying an in-depth that it and its children can be simplified.
 */
public class Operator extends Node {

    /* Construct an Operator with children */
    public Operator(int value, Node left, Node right) {
        this.value = value;
        setChildren(left, right);
    }

    /* Construct an Operator with null children */
    public Operator(int value) {
        this.value = value;
        if(value < 0 || value > 3) {
            System.out.println("Warning! Invalid operator value: " + value);
        }
    }

    public boolean isOperator() {
        return true;
    }
    public void setChildren(Node left, Node right) {
        this.left = left;
        this.right = right;
    }


    /** The main functionality of this subclass. Searches children to simplify it and them as
     * much as possible. If children are Numbers, then this function will simply compute the
     * number answer. If children are other Operators, then recursively calls them to simplify()
    */
    public Node simplify() {

        valueHistory.add(text());

        Node leftSimple =  left.simplify();
        Node rightSimple = right.simplify();

        if(leftSimple.hasErrorMessage) {
            return leftSimple;
        } else if(rightSimple.hasErrorMessage) {
            return rightSimple;
        }

        if( !leftSimple.isOperator() && !leftSimple.isVariable()
            && !rightSimple.isOperator() && !rightSimple.isVariable()) {

                int leftVal = leftSimple.value();
                int rightVal = rightSimple.value();

            switch(value) {
                case 0: {
                    Number newNum = new Number(leftVal + rightVal);
                    valueHistory.add(""+newNum.value);
                    return newNum;
                } case 1: {
                    Number newNum = new Number(leftVal - rightVal);
                    valueHistory.add(""+newNum.value);
                    return newNum;
                } case 2: {
                    Number newNum = new Number(leftVal * rightVal);
                    valueHistory.add(""+newNum.value);
                    return newNum;
                } case 3: {
                    if(rightVal == 0) {
                        //Divide by zero
                        return ErrorNode("Cannot divide "+leftSimple.value+" by 0");
                    }

                    if(leftVal % rightVal == 0) {
                        Number newNum = new Number(leftVal / rightVal);
                        valueHistory.add(""+newNum.value);
                        return newNum;
                        
                    } else {
                        //Simplify the fraction and leave it as a fraction
                        int[] frac = simplifyFraction(leftVal, rightVal);

                        leftSimple = new Number(frac[0]);
                        rightSimple = new Number(frac[1]);

                        Operator newOp = new Operator(3, leftSimple, rightSimple);
                        valueHistory.add(""+newOp.value);
                        return newOp;
                    }
                }
            }

        } else if(!leftSimple.isOperator() && !rightSimple.isOperator()) {
            //One or more variable children
            return new Operator(value, leftSimple, rightSimple);
            
        } else {

            if(leftSimple.isVariable() || (leftSimple.isOperator() && (leftSimple.left.isVariable() || leftSimple.right.isVariable()))

            || rightSimple.isVariable() || (rightSimple.isOperator() && (rightSimple.left.isVariable() || rightSimple.right.isVariable()))
             ) {
                return new Operator(value, leftSimple, rightSimple);
            }

            int[] fracLeft = new int[2];
            int[] fracRight = new int[2];

            if(leftSimple.isOperator()) {
                fracLeft[0] = leftSimple.left.value();
                fracLeft[1] = leftSimple.right.value();
            } else {
                fracLeft[0] = leftSimple.value(); //Assuming this is number!!
                fracLeft[1] = 1;
            }

            if(rightSimple.isOperator()) {
                fracRight[0] = rightSimple.left.value();
                fracRight[1] = rightSimple.right.value();
            } else {
                fracRight[0] = rightSimple.value(); //Assuming this is number!!
                fracRight[1] = 1;
            }


            if(value == 0 || value==1) { //we're adding or subtracting fractions

                int lcm = leastCommonMultiple(fracLeft[1], fracRight[1]);
                int topLeft = fracLeft[0] * (lcm / fracLeft[1]);
                int topRight = fracRight[0] * (lcm / fracRight[1]);

                int top = (value==0) ? (topLeft + topRight) : (topLeft - topRight);
                
                Operator newOp = new Operator(3, new Number(top), new Number(lcm));
                Node newerOp = newOp.simplify();
                valueHistory.add(""+newerOp.value);
                return newerOp;


            } else if(value == 2) { //multiplication

                int top = fracLeft[0] * fracRight[0];   
                int bottom = fracLeft[1] * fracRight[1];

                int[] frac = simplifyFraction(top, bottom);

                return new Operator(3, new Number(frac[0]), new Number(frac[1]));

            } else if(value == 3) { //division

                int top = fracLeft[0] * fracRight[1];
                int bottom = fracLeft[1] * fracRight[0];

                int[] frac = simplifyFraction(top, bottom);

                return new Operator(3, new Number(frac[0]), new Number(frac[1]));

            }

            return new Operator(value, leftSimple, rightSimple);
        }

        return ErrorNode("A problem occurred.");
    }



    /**
     * Function to print the values this Node has stored over time.
     * @param leftText the text which was to the left of this operation in the expression
     * @param rightText the text which was to the right of this operation in the expression
     */
    public void printValueHistory(String leftText, String rightText) {
        if(valueHistory.size() > 1) {

            leftText = leftText + "(";
            rightText = ")" + rightText;

            String addedLeftText = left.text() + this.toString();

            if(left != null) {
                left.printValueHistory(leftText, this.toString() + right.text()+rightText);
                if( !left.valueHistory.isEmpty()) {
                    addedLeftText = left.valueHistory.get(left.valueHistory.size()-1) + this.toString();
                }
            }


            if(right != null) {
                right.printValueHistory(leftText + addedLeftText, rightText);
            }

            System.out.println(leftText +valueHistory.get(1) + rightText);

        }


    }

    /** Displaying nicely as text with its own children */
    public String text() {
        return textWithCustomChildren(left, right);
    }

    /* This is useful for displaying the steps of the equation */
    private String textWithCustomChildren(Node leftChild, Node rightChild) {
        boolean leftParen = false;
        if( (value==2 || value==3) && 
            leftChild.isOperator() &&
            (leftChild.value()==0 || leftChild.value()==1) ) {
            leftParen = true;
        }
        String leftText = (leftParen ? "(" : "") + leftChild.text() + (leftParen ? ")" : "");

        boolean rightParen = false;
        if(value==3 && right.isOperator()) {
            rightParen = true;

        } else if( value==2 && 
            rightChild.isOperator() &&
            (rightChild.value()==0 || rightChild.value()==1) ) {
            rightParen = true;
        }
        String rightText = (rightParen ? "(" : "") + rightChild.text() + (rightParen ? ")" : "");

        return leftText + "" +this.toString()+ "" + rightText;
    }


    public String toString() {
        // String tag = nonInteger ? "" : "f";
        String tag = "";
        if(value() == 0) { 
            return "+" + tag;
        } else if(value() == 1){
            return "-" + tag;
        } else if(value() == 2) {
            return "*" + tag;
        } else if(value() == 3) {
            return "/" + tag;
        
        } else {
            return "??? "+ tag;
        }
    }


    // Utility functions for doing mathy things like simplifing fractions
    
    /* Generates list of prime factors for specified num */
    public ArrayList<Integer> factors(int num) {
        ArrayList<Integer> factors = new ArrayList<>();
        factors.add(1); //gets removed at the end

        boolean isNegative = false;
        if(num < 0) {
            num = -1 * num;
            isNegative = true;
        }

        int i=2;
        while(i * factors.get(factors.size()-1) < num) {
            // System.out.println("Num: "+num +" i: "+i);
            if(num % i == 0) {
                factors.add(i);
                num = num / i;
                
            } else {
                i++;
            }
        }
        factors.add(num);

        factors.remove(0);
        if(isNegative) {
            factors.add(0, -1);
        }
        return factors;
    }

    /* Given a numerator and a denominator, simplifies them and returns as a array of 2 elements */
    private int[] simplifyFraction(int leftNum, int rightNum) {
        ArrayList<Integer> leftFactors = factors(leftNum);
        ArrayList<Integer> rightFactors = factors(rightNum);

        int i=0;
        int j=0;
        while(i < leftFactors.size()) {
            if(leftFactors.get(i) == rightFactors.get(j)) {
                leftNum /= leftFactors.get(i);
                rightNum /= leftFactors.get(i); //common divisor
                leftFactors.remove(i);
                rightFactors.remove(j);
            } else {
                j++;
            }

            if(j > rightFactors.size()-1) {
                i++;
                j=0;
            }
        }

        //Make the negative on the top
        if(rightNum < 0) {
            leftNum *= -1;
            rightNum *= -1;
        }

        return new int[] {leftNum, rightNum};
    }

    /** Find the least common multiple between two numbers */
    public int leastCommonMultiple(int a, int b) {
        int[] frac = simplifyFraction(a,b);
        return a * frac[1]; //which should be the same as b * frac[0];
    }


}
