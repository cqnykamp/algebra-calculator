import java.util.List;
import java.util.ArrayList;


/**
 * Node is likely the main class in this project, and it is the superclass for a couple
 * different kinds of nodes. A Node represents one node on the expression tree, and it
 * stores references to its children, if they exist. It has methods to detect which kind
 * of Node it is (these methods are overriden by the subclasses). It also has a method
 * to display it and its children as a tree.
 */
public class Node {

    public Node left;
    public Node right;

    public int value;

    public ArrayList<String> valueHistory = new ArrayList<>();

    //To be used if operations are invalid (such as dividing by zero)
    public String errorMessage = "";
    public boolean hasErrorMessage = false;


    /** Sets the children of this Node */
    public void setChildren(Node left, Node right) {
        return;
    }

    /* If is leaf in the tree */
    public boolean isLeaf() {
        return (left == null && right == null);
    }

    /** The following functions will be overridden by subclasses */

    /**
    * Creates a copy of itself which is a simplified version of itself and its children
     */
    public Node simplify() {
        return null;
    }

    /* Display this node as text */
    public String text() {
        return "";
    }

    /* Get the value of this node */
    public int value() {
        return value;
    }

    /* If is operator (to be further defined in subclasses) */
    public boolean isOperator() {
        return false;
    }

    /* If is variable (to be further defined in subclasses) */
    public boolean isVariable() {
        return false;
    }

    /* If is a parentheses (to be further defined in subclasses) */
    public boolean isParenthesis() {
        return false;
    }


    /**
     * These two functions are ones to help search for where a variable is on the tree
     */
    public boolean hasChildVarOnRight() {
        if(right == null) {
            return isVariable();
        } else {
            return right.hasChildVarOnLeft() || right.hasChildVarOnRight();
        }
    }
    public boolean hasChildVarOnLeft() {
        if(left == null) {
            return isVariable();
        } else {
            return left.hasChildVarOnLeft() || left.hasChildVarOnRight();
        }
    }


    /**
     * Function to print the values this Node has stored over times
     */
    public void printValueHistory(String leftText, String rightText) {
        if(!valueHistory.isEmpty()) {
            System.out.println(leftText +" " +valueHistory.get(0) +" "+ rightText);
            leftText = leftText + " {{";
            rightText = "}} " + rightText;

            if(left != null) {
                left.printValueHistory(leftText, rightText);
            }
            if(right != null) {
                right.printValueHistory(leftText, rightText);
            }

        }


    }

    /**
     * Convenience method to create a Node that has an error message. This is used 
     * to notify the higher layers of recursion that something went wrong (such as dividing
     * by zero)
     */
    public static Node ErrorNode(String message) {
        Node errorNode = new Node();
        errorNode.hasErrorMessage = true;
        errorNode.errorMessage = message;
        return errorNode;        
    }





    /////////////////////  COPIED FROM BST LAB /////////////////

    /**
     * Displays an ASCII-art sort of representation of this tree (image-like text).
     */
    public void printTree() {
        Node root = this; //for the purposes of printing it
        int maxLevel = maxLevel(root);
        List<Node> rootList = new ArrayList<Node>();
        rootList.add(root);
        printNodeInternal(rootList, 1, maxLevel);
    }


    /****
     * You can ignore all code below this point!  
     * This code takes care of printing.
     * 
     * Lightly modified from user post on stack overflow:
     * http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
     * and code provided by Anna Rafferty.
     */
    private void printNodeInternal(List<Node> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || areAllElementsNull(nodes)) {
            return;
        }
        int floor = maxLevel - level;
        int edgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;
    
        printWhitespaces(firstSpaces);
    
        List<Node> newNodes = new ArrayList<Node>();
        for (Node node : nodes) {
            if (node != null) {
                System.out.print(node);
                newNodes.add(node.left);
                newNodes.add(node.right);
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }
    
            printWhitespaces(betweenSpaces);
        }
        System.out.println("");
    
        for (int i = 1; i <= edgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                printWhitespaces(firstSpaces - i);
                if (nodes.get(j) == null) {
                    printWhitespaces(edgeLines + edgeLines + i + 1);
                    continue;
                }
    
                if (nodes.get(j).left != null)
                    System.out.print("/");
                else
                    printWhitespaces(1);
    
                printWhitespaces(i + i - 1);
    
                if (nodes.get(j).right != null)
                    System.out.print("\\");
                else
                    printWhitespaces(1);
    
                printWhitespaces(edgeLines + edgeLines - i);
            }
    
            System.out.println("");
        }
    
        printNodeInternal(newNodes, level + 1, maxLevel);
    }
    
    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }
    
    private static int maxLevel(Node node) {
        if (node == null) {
            return 0;
        }
        return Math.max(maxLevel(node.left), maxLevel(node.right)) + 1;
    }
    
    private static boolean areAllElementsNull(List<Node> list) {
        for (Node object : list) {
            if (object != null)
                return false;
        }
        return true;
    }

}

