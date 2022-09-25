import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The entyr point class for this program. This program takes care of getting user input,
 * displaying an answer, or showing the user the tutorial or their expression history
 */
public class Calculator {

    /**
     * Generates a fancy text box around a message
     * @param message : the message
     */
    public static String boxMessage(String message) {
        String[] lines = message.split("\n");
        int maxLength = 0;
        for(String line : lines) {
            if(line.length() > maxLength) {
                maxLength = line.length();
            }
        }

        String boxTop = "";
        for(int i=0; i < maxLength + 4; i++) {
            boxTop += "=";
        }

        String output = "";
        output += boxTop + "\n";
        for(String line : lines) {
            output += "  "+line+"  \n";
        }
        output += boxTop;
        return output;
    }


    /**
     * The entry point of the program. Takes care of scanning user input, logging the history,
     * and exiting when the user asks it to.
     */
    public static void main(String[] args) {
        HashMap<String, String> history = new HashMap<>();

        Parser parser = new Parser();

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Input an expression or type 'help', 'history', or 'exit':");
            System.out.print(">> ");

            String input = scanner.nextLine();

            if(input.toLowerCase().trim().equals("exit")) {
                break;
            }

            if(input.toLowerCase().trim().equals("help")) {
                System.out.println("\n" + boxMessage("TUTORIAL"));
                System.out.println("Input any arithmetic expression with integers.");
                System.out.println("    Example: 1 + 7 * -4");
                System.out.println("    Example: (3 + 4*8)( 2 - 1)");
                System.out.println("Also, input an equation to see if it is true.");
                System.out.println("    Example: 1 + 5/3 - 2 = 7/3\n");

                continue;
            }


            if(input.toLowerCase().trim().equals("history")) {
                if(history.isEmpty()) {
                    System.out.println("No history yet.");
                } else {
                    for(String key : history.keySet()) {
                        System.out.println(key + " --> " + history.get(key));
                    }
                }
                continue;
            }

            if(input.indexOf('x') != input.lastIndexOf('x')) {
                System.out.println("This program can only handle one x symbol");
                continue;
            }


            if(input.indexOf('=') != -1) {
                
                if(input.indexOf('=') != input.lastIndexOf('=')) {
                    System.out.println("Too many equal signs");
                    continue;
                }
                
                Node leftRoot = parser.parse(input.substring(0,input.indexOf('=')));
                Node rightRoot = parser.parse(input.substring(input.indexOf('=')+1, input.length()));

                if(leftRoot == null || rightRoot == null) {
                    continue;
                }

                NodeWithHistory leftSimple = leftRoot.simplify();
                NodeWithHistory rightSimple = rightRoot.simplify();

                System.out.println("Left side:");
                leftRoot.printTree();
                System.out.println("Right side:");
                rightRoot.printTree();

                if(leftSimple.node.hasErrorMessage) {
                    System.out.println(leftSimple.node.errorMessage);
                    continue;
                } else if(rightSimple.node.hasErrorMessage) {
                    System.out.println(rightSimple.node.errorMessage);
                    continue;

                } else {
                    String equation = leftSimple.node.toStringIncludingChildren() + " = " + rightSimple.node.toStringIncludingChildren();

                    if(leftSimple.node.toStringIncludingChildren().equals(rightSimple.node.toStringIncludingChildren())) {
                        System.out.println(boxMessage(equation+"\nThis equation is valid."));
                        history.put(equation, "valid");
                    } else {
                        System.out.println(boxMessage(equation+"\nThis equation is NOT valid."));
                        history.put(equation, "invalid");
                    }
                    continue;
                }


            } else {
                // Single Expression

                Node root = parser.parse(input);
                if(root == null) {
                    continue;
                }

                root.printTree();

                NodeWithHistory simpleWithHistory = root.simplify();

                Node simple = simpleWithHistory.node;
                ArrayList<String> valuesHistory = simpleWithHistory.values;

                if(simple.hasErrorMessage) {
                    System.out.println(simple.errorMessage);
                } else {
                    String result = "Answer: "+root.toStringIncludingChildren() +" = "+simple.toStringIncludingChildren();
                    history.put(root.toStringIncludingChildren(), simple.toStringIncludingChildren());
                    System.out.println(boxMessage(result));
                }

                System.out.println(valuesHistory.get(0));
                for(int i = 1; i < valuesHistory.size(); i++) {
                    String line = valuesHistory.get(i);
                    System.out.println(" = " + line);
                } 
 
            }

        }

        scanner.close();

    }
}