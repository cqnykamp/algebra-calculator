import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

/**
 * This class takes in a math expression and converts into a tree of Nodes.
 */
public class Parser {

    String digitsList = "0123456789";
    String operatorsList ="+-*/";
    String variablesList = "x";


    /**
     * This method takens in math text and parses it into a tree of Nodes
     * @param input the String, which should only contain numbers, arithmetic,
                    possibly an 'x', and whitespace
        @return root of tree. Each Node will be of the correct subclass depending on what
                they parse out to be.
     */
    public Node parse(String input) {

        input = input.replaceAll(" ", ""); //Remove all whitespace

        if(input.length() == 0) {
            System.out.println("Expression is empty");
            return null;
        }    

        int parenCount = 0;
        
        for(int i=0; i<input.length(); i++) {
            char c = input.charAt(i);
            if(digitsList.indexOf(c) == -1 && operatorsList.indexOf(c) == -1 && variablesList.indexOf(c) == -1 && c != '(' && c != ')') {
                System.out.println("Invalid character: '"+c+"'");
                return null;
            }

            if(c=='(') {
                parenCount++;
            } else if(c==')') {
                parenCount--;
            }
        }

        if(parenCount != 0) {
            System.out.println("Mismatched parentheses");
            return null;
        }

        if(operatorsList.indexOf(input.charAt(input.length()-1)) != -1) {
            System.out.println("Invalid expression. Ended with an operator.");
            return null;
        }

        
        //Tokenize the string

        ArrayList<Node> tokens = new ArrayList<>();
        String currentNum = "";
        char previousChar = '\n'; //Newline char flags the start
                
        for(char c : input.toCharArray()) {
            if(digitsList.indexOf(c) != -1) {
                currentNum += c;

            } else {
                if(currentNum != "") {

                    try {
                        tokens.add(new Number(Integer.valueOf(currentNum)));
                    } catch(NumberFormatException e) {
                        System.out.println("Those numbers were too big for the computer to handle.");
                    }
                }
                currentNum = "";
            }

            if(variablesList.indexOf(c) != -1) {
                tokens.add(new Variable(c));
            }

            if(operatorsList.indexOf(c) != -1) {
                tokens.add(new Operator(operatorsList.indexOf(c) ));
            }

            if(c == '(') {
                tokens.add(new Parenthesis(0));
            }

            if(c == ')') {
                tokens.add(new Parenthesis(1));
            }

            previousChar = c;
        }

        if(currentNum != "") {
            tokens.add(new Number(Integer.valueOf(currentNum)));
        }


        //Attach hanging negatives to numbers
        boolean operAfterNumberOrVar = true;

        for(int i=0; i< tokens.size()-1; i++) {
            Node token = tokens.get(i);
            Node nextToken = tokens.get(i+1);

            if(token.value == 1) {
                if( !nextToken.isOperator() && !nextToken.isParenthesis()) {

                    if(i == 0
                        || (tokens.get(i-1).isParenthesis() && tokens.get(i-1).value==0)
                        || (tokens.get(i-1).isOperator())
                    ) {

                        //The negative should be incorporated
                        if(nextToken.isVariable()) {
                            tokens.remove(i);
                            tokens.add(i, new Parenthesis(0));
                            tokens.add(i+1, new Number(-1));
                            tokens.add(i+2, new Operator(2));
                            // i+3 is the variable token
                            tokens.add(i+4, new Parenthesis(1));
                            i += 3;

                        } else {
                            //Next token is number
                            tokens.set(i+1, new Number(-1 * nextToken.value));
                            tokens.remove(i);
                            i -= 1;
                        }
                    }

                }

            }
        }


        //Insert multiplication where it is implied
        for(int i=0; i<tokens.size()-1; i++) {
            Node token = tokens.get(i);
            Node nextToken = tokens.get(i+1);

            if(token.isParenthesis() && token.value == 0) {
                //do nothing
            } else if(!token.isOperator() && !nextToken.isOperator()) {

                if(nextToken.isParenthesis() && nextToken.value == 1) {
                    //do nothing
                } else {
                    tokens.add(i+1, new Operator(2));
                    i += 1;
                }
            }

        }



        //Deal with parenthesis recursively
        parseTokens(tokens, 0, tokens.size(), "");

        if(tokens.size() != 1) {
            System.out.println("Invalid expression");
            return null;
        } else {
            return tokens.get(0);
        }

    }
        
    /**
     * This is a recursive function which deals with parentheses in the string. It calss itself * with only the text that is inside a certain set of parentheses, so in this way we "drill * down" to parse every symbol in the correct order. 
     * If this runs with no invalid syntax, then the tokens arraylist will be whittled down
     * into only one element.
     * @param tokens the list of tokens, ordered by how they appeared in the original string
     * @param start the start index (all recursive calls share the same array)
     * @param end the end index
     * @param offset a convenience argument to keep of how deep we are in the recursion
     */
    private void parseTokens(ArrayList<Node> tokens, int start, int end, String offset) {

        int prevLength = end-start;

        while(end-start > 1) {

            int closingParen = -1;
            for(int i= start; i< end - start; i++) {
                Node token = tokens.get(i);
                if(token.isParenthesis() && token.value() == 1) {
                    closingParen = i;
                    break;
                }
            }

            if(closingParen != -1) {

                int openingParen = -1;
                for(int i = closingParen; i>= start; i--) {
                    Node token = tokens.get(i);
                    if(token.isParenthesis() && token.value() == 0) {
                        openingParen = i;
                        break;
                    }
                }

                if(openingParen == -1) {
                    System.out.println(offset + "Error: parentheses not matching!");
                }

                tokens.remove(closingParen);
                tokens.remove(openingParen);

                parseTokens(tokens, openingParen, closingParen-1, offset+"  ");
                end -= closingParen - openingParen;


            } else {
                //Found no parentheses
            
                for(int op_id = operatorsList.length()-1; op_id>=0; op_id--) {

                    for(int i=start; i < end; i++) {

                        Node currentToken = tokens.get(i);
                        if(currentToken.isOperator() && currentToken.isLeaf() && currentToken.value() == op_id) {

                            currentToken.setChildren(tokens.get(i-1), tokens.get(i+1));

                            //NOTE: the order we remove them matters
                            tokens.remove(i+1);
                            tokens.remove(i-1);
                            i -= 1;

                            end = end - 2;
                        }
                    }

                }

            }

            if(end-start == prevLength) {
                // System.out.println("Invalid expression");
                //Invalid expression
                break;
            }

            prevLength = end-start;

        }

    }

}