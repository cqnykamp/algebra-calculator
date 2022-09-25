import java.util.ArrayList;

public class NodeWithHistory {
    public Node node;
    public ArrayList<String> values;

    public NodeWithHistory() {
        this.node = new Node();
        this.values = new ArrayList<>();
    }
}