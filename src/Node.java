import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A node in the tree. Contains a parent and a set of children, as well as the feature.
 *
 * @author Sander Ploegsma
 */
public class Node {
    private Map<String, Node> children;
    private String feature;
    private List<Record> data;
    private Double gini;

    public Node(List<Record> data) {
        this.data = data;
        this.gini = 0.0;
        this.children = new HashMap<String, Node>();
    }

    public String getFeature() {
        return this.feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public List<Record> getData() {
        return this.data;
    }

    public Double getGini() {
        return this.gini;
    }

    public void setGini(Double gini) {
        this.gini = gini;
    }

    public void addChild(String featureValue, Node node) {
        this.children.put(featureValue, node);
    }

    public Map<String, Node> getChildren() {
        return this.children;
    }
}
