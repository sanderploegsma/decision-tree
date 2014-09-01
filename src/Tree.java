import java.util.*;

/**
 * The decision tree. Contains a root node that in turn contains other nodes.
 *
 * @author Sander Ploegsma
 */
public class Tree {

    /**
     * Construct a new tree with the given root. Calculates the Gini impurity for each feature in the map and picks the
     * lowest of these values as best feature. The values of this feature are added as child nodes and the process is
     * repeated for each child recursively.
     *
     * @param root A node containing some data
     * @return the root node containing children
     */
    public static Node constructTree(Node root, Map<String, Set<String>> featureKeyValues) {
        root.setGini(Gini.calculateGini(root.getData()));
        if (root.getGini() == 0.0) {
            // Data consists of records all with the same label, so no splitting neccesary.
            return root;
        }

        // Calculate the Gini impurity for each feature.
        Map<String, Double> giniIndices = new HashMap<String, Double>();
        for (String feature : featureKeyValues.keySet()) {
            giniIndices.put(feature, 0.0);
            for (String value : featureKeyValues.get(feature)) {
                giniIndices.put(feature, giniIndices.get(feature) +
                    Gini.calculateGini(createDataSubset(root.getData(), feature, value)));
            }
        }

        // Find the most optimal feature to split on
        String bestFeature = null;
        Double minGini = Double.POSITIVE_INFINITY;
        for (Map.Entry<String, Double> entry : giniIndices.entrySet()) {
            if (entry.getValue() < minGini) {
                minGini = entry.getValue();
                bestFeature = entry.getKey();
            }
        }

        root.setFeature(bestFeature);
        // Create a new feature set without the previously selected best feature.
        Map<String, Set<String>> newFeatureKeyValues = new HashMap<String, Set<String>>();
        for (String feature : featureKeyValues.keySet()) {
            if (!feature.equals(bestFeature)) {
                newFeatureKeyValues.put(feature, featureKeyValues.get(feature));
            }
        }
        // Create children from the selected feature.
        for (String value : featureKeyValues.get(bestFeature)) {
            Node child = new Node(createDataSubset(root.getData(), bestFeature, value));
            constructTree(child, newFeatureKeyValues);
            root.addChild(value, child);
        }
        return root;
    }

    /**
     * Creates a subset of a set of data, based on the given value for some attribute.
     *
     * @param data      the data to split
     * @param attribute the attribute to match
     * @param value     the value the attribute should be
     * @return a set of records where the given attribute has the given value.
     */
    private static List<Record> createDataSubset(List<Record> data, String attribute, String value) {
        List<Record> res = new ArrayList<Record>();

        for (Record r : data) {
            /* Check if the record contains the feature, since the dataset description
             * states that some feature values are unknown, they all occur with feature #11.
             */
            if (r.getAttributes().containsKey(attribute) && r.getAttributes().get(attribute).equals(value)) {
                res.add(r);
            }
        }
        return res;
    }

    /**
     * Predict the label of a certain record, using the decision tree provided with the root node.
     *
     * @param r    A record
     * @param root a decision tree
     * @return the label that belongs to the record.
     */
    public static String predict(Record r, Node root) {
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (current.getChildren().isEmpty()) {
                return getMajorityVoteFromData(current.getData());
            }
            for (Map.Entry<String, Node> child : current.getChildren().entrySet()) {
                if (child.getKey().equals(r.getAttributes().get(current.getFeature()))) {
                    queue.add(child.getValue());
                }
            }
        }
        return null;
    }

    /**
     * Get the label with the most occurrences from the dataset
     *
     * @param data a dataset
     * @return the label that occurs the most in the dataset
     */
    private static String getMajorityVoteFromData(List<Record> data) {
        Map<String, Integer> labelCount = new HashMap<String, Integer>();
        for (Record r : data) {
            if (!labelCount.containsKey(r.getLabel())) {
                labelCount.put(r.getLabel(), 0);
            }
            labelCount.put(r.getLabel(), labelCount.get(r.getLabel()) + 1);
        }

        int maxCount = 0;
        String maxLabel = null;
        for (Map.Entry<String, Integer> entry : labelCount.entrySet()) {
            if (maxCount < entry.getValue()) {
                maxCount = entry.getValue();
                maxLabel = entry.getKey();
            }
        }
        return maxLabel;
    }
}
