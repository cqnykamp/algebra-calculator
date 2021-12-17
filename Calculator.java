import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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

                Node leftSimple = leftRoot.simplify();
                Node rightSimple = rightRoot.simplify();

                System.out.println("Left side:");
                leftRoot.printTree();
                System.out.println("Right side:");
                rightRoot.printTree();

                if(leftSimple.hasErrorMessage) {
                    System.out.println(leftSimple.errorMessage);
                    continue;
                } else if(rightSimple.hasErrorMessage) {
                    System.out.println(rightSimple.errorMessage);
                    continue;

                } else {
                    String equation = leftSimple.text() + " = " + rightSimple.text();

                    if(leftSimple.text().equals(rightSimple.text())) {
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

                Node simple = root.simplify();

                if(simple.hasErrorMessage) {
                    System.out.println(simple.errorMessage);
                } else {
                    String result = "Answer: "+root.text() +" = "+simple.text();
                    history.put(root.text(), simple.text());
                    System.out.println(boxMessage(result));
                }

                System.out.println("  " + root.text());
                root.printValueHistory("= ", "");


            }

        }

    }
}