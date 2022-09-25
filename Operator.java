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
    public NodeWithHistory simplify() {

        NodeWithHistory result = new NodeWithHistory();

        // System.out.println("Simplifying " + this.value + " " + this.left + " " + this.right); 

        NodeWithHistory leftSimple =  left.simplify();
        NodeWithHistory rightSimple = right.simplify();

        if(leftSimple.node.hasErrorMessage) {
            return leftSimple;
        } else if(rightSimple.node.hasErrorMessage) {
            return rightSimple;
        }

        // System.out.println("Left history" + leftSimple.values);
        // System.out.println("Right history" + rightSimple.values);


        for (String leftVal : leftSimple.values) {
            result.values.add("(" + leftVal + this.toString() + this.right.toStringIncludingChildren() + ")");
        }
        String lastLeftVal = leftSimple.values.get(leftSimple.values.size() - 1);

        rightSimple.values.remove(0);
        for (String rightVal : rightSimple.values) {
            result.values.add("(" + lastLeftVal + this.toString() + rightVal + ")");
        }

        if( !leftSimple.node.isOperator() && !leftSimple.node.isVariable()
            && !rightSimple.node.isOperator() && !rightSimple.node.isVariable()) {

            int leftVal = leftSimple.node.value();
            int rightVal = rightSimple.node.value();

            switch(value) {
                case 0: {
                    result.node = new Number(leftVal + rightVal);
                    result.values.add(String.valueOf(result.node.value));
                } break;
                case 1: {
                    result.node = new Number(leftVal - rightVal);
                    result.values.add(String.valueOf(result.node.value));
                } break;
                case 2: {
                    result.node = new Number(leftVal * rightVal);
                    result.values.add(String.valueOf(result.node.value));
                } break;
                case 3: {
                    if(rightVal == 0) {
                        //Divide by zero
                        result.node = ErrorNode("Cannot divide "+leftSimple.node.value+" by 0");
                    }

                    if(leftVal % rightVal == 0) {
                        // System.out.println(leftVal + " fits in " + rightVal);
                        result.node = new Number(leftVal / rightVal);
                        result.values.add(String.valueOf(result.node.value));
                        
                    } else {
                        // System.out.println(leftVal + " does not fit in " + rightVal);

                        //Simplify the fraction and leave it as a fraction
                        int[] frac = simplifyFraction(leftVal, rightVal);

                        Node leftEvenSimpler = new Number(frac[0]);
                        Node rightEvenSimpler = new Number(frac[1]);

                        // System.out.println("Simplified fraction is " + leftSimple.node + " / " + rightSimple.node);

                        result.node = new Operator(3, leftEvenSimpler, rightEvenSimpler);
                        String lastResultValue = result.values.get(result.values.size() - 1);
                        String possibleNewValue = "(" + frac[0] + this.toString() + frac[1] + ")";
                        if( !possibleNewValue.equals(lastResultValue)) {
                            result.values.add(possibleNewValue);
                        }
                    }
                } break;
                default: {
                    throw new Error("Invalid operator type");
                }
            }

        } else if(!leftSimple.node.isOperator() && !rightSimple.node.isOperator()) {
            //One or more variable children
            result.node = new Operator(value, leftSimple.node, rightSimple.node);
            throw new Error("Variables unimplemented!");
            
        } else if(
            leftSimple.node.isVariable() || (leftSimple.node.isOperator() && (leftSimple.node.left.isVariable() || leftSimple.node.right.isVariable()))
            || rightSimple.node.isVariable() || (rightSimple.node.isOperator() && (rightSimple.node.left.isVariable() || rightSimple.node.right.isVariable()))
        ) {
            result.node = new Operator(value, leftSimple.node, rightSimple.node);
            throw new Error("Variables unimplemented");

        } else {

            int[] fracLeft = new int[2];
            int[] fracRight = new int[2];

            if(leftSimple.node.isOperator()) {
                fracLeft[0] = leftSimple.node.left.value();
                fracLeft[1] = leftSimple.node.right.value();
            } else {
                fracLeft[0] = leftSimple.node.value(); //Assuming this is number!!
                fracLeft[1] = 1;
            }

            if(rightSimple.node.isOperator()) {
                fracRight[0] = rightSimple.node.left.value();
                fracRight[1] = rightSimple.node.right.value();
            } else {
                fracRight[0] = rightSimple.node.value(); //Assuming this is number!!
                fracRight[1] = 1;
            }


            if(value == 0 || value==1) { //we're adding or subtracting fractions

                int lcm = leastCommonMultiple(fracLeft[1], fracRight[1]);
                int topLeft = fracLeft[0] * (lcm / fracLeft[1]);
                int topRight = fracRight[0] * (lcm / fracRight[1]);

                int top = (value==0) ? (topLeft + topRight) : (topLeft - topRight);
                
                Operator newOp = new Operator(3, new Number(top), new Number(lcm));
                result.values.add("(" + top + newOp.toString() + lcm + ")");
                NodeWithHistory newerOp = newOp.simplify();

                result.node = newerOp.node;
                if(result.node.isOperator()) {
                    String lastResultValue = result.values.get(result.values.size() - 1);
                    String possibleNewValue = "(" + result.node.left.toString() + result.node.toString() + result.node.right.toString() + ")";
                    if( !possibleNewValue.equals(lastResultValue)) {
                        result.values.add(possibleNewValue);
                    }

                } else {
                    result.values.add(result.node.toString());
                }


            } else if(value == 2) { //multiplication

                int top = fracLeft[0] * fracRight[0];   
                int bottom = fracLeft[1] * fracRight[1];
                result.values.add("(" + top + this.toString() + bottom + ")");


                int[] frac = simplifyFraction(top, bottom);

                result.node = new Operator(3, new Number(frac[0]), new Number(frac[1]));

                String lastResultValue = result.values.get(result.values.size() - 1);
                String possibleNewValue = "(" + result.node.left.toString() + result.node.toString() + result.node.right.toString() + ")";
                if( !possibleNewValue.equals(lastResultValue)) {
                    result.values.add(possibleNewValue);
                }


            } else if(value == 3) { //division

                int top = fracLeft[0] * fracRight[1];
                int bottom = fracLeft[1] * fracRight[0];
                result.values.add("(" + top + this.toString() + bottom + ")");


                // System.out.println("Top is " + top + " Bottom is " + bottom);

                int[] frac = simplifyFraction(top, bottom);

                // System.out.println("After simplification fraction " + frac);

                result.node = new Operator(3, new Number(frac[0]), new Number(frac[1]));
                String lastResultValue = result.values.get(result.values.size() - 1);
                String possibleNewValue = "(" + result.node.left.toString() + result.node.toString() + result.node.right.toString() + ")";
                if( !possibleNewValue.equals(lastResultValue)) {
                    result.values.add(possibleNewValue);
                }

            } else {
                throw new Error("Invalid operator");
            }
        }

        // System.out.println("value history is " + result.values);
        return result;
    }


    public String toString() {
        String opStr = "";
        if(value() == 0) {
            opStr = "+";
        } else if(value() == 1){
            opStr = "-";
        } else if(value() == 2) {
            opStr = "*";
        } else if(value() == 3) {
            opStr = "/";        
        } else {
            opStr = "???";
        }

        return opStr;
    }

    public String toStringIncludingChildren() {
        return this.left.toStringIncludingChildren() + this.toString() + this.right.toStringIncludingChildren();
    }


    // Utility functions for doing mathy things like simplifing fractions
    
    /* Generates list of prime factors for specified num */
    public ArrayList<Integer> factors(int num) {
        ArrayList<Integer> factors = new ArrayList<Integer>();
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
