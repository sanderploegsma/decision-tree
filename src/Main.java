import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Main class for the decision tree algorithm. Handles input and performs the algorithm.
 *
 * @author Sander Ploegsma
 */
public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    private static Map<Integer, String> featureLookup = null;
    private static Map<Integer, String> labelLookup = null;
    private static Map<String, Set<String>> featureKeyValues = null;

    /**
     * Multiple datasets are available, currently supports "mushrooms" and "chess".
     */
    private static String dataSet = "mushrooms";

    public static void main(String[] args) {
        logger.info("Algorithm started.");
        labelLookup = readData("data/" + dataSet + "/label-values");
        featureLookup = readData("data/" + dataSet + "/feature-names");
        featureKeyValues = new HashMap<String, Set<String>>();

        Map<Integer, String> trainData = readData("data/" + dataSet + "/train");
        List<Record> trainRecords = constructRecords(trainData);

        logger.info("Creating tree.");
        Node root = new Node(trainRecords);
        Tree.constructTree(root, featureKeyValues);
        logger.info("Tree construction finished");

        logger.info("Starting predictions.");
        Map<Integer, String> testData = readData("data/" + dataSet + "/test");
        List<Record> testRecords = constructRecords(testData);

        int positiveCount = 0;
        for (Record r : testRecords) {
            String outcome = Tree.predict(r, root);
            /*
             * Outcome will be null if the test record misses F11, since tree traversal cannot continue.
             * Ignore these records for now.
             */
            if (outcome != null && outcome.equals(r.getLabel())) {
                positiveCount++;
            }
        }
        logger.info(String.format("Score: %.2f percent", ((double) positiveCount / testRecords.size()) * 100.0));

        logger.info("Algorithm completed.");
    }

    /**
     * Construct records from the lines from the dataset.
     *
     * @param dataSet the lines from the dataset.
     * @return A collection of records.
     */
    private static List<Record> constructRecords(Map<Integer, String> dataSet) {
        List<Record> res = new ArrayList<Record>();
        for (Map.Entry<Integer, String> entry : dataSet.entrySet()) {
            String[] entries = entry.getValue().split(" ");
            String label = labelLookup.get(Integer.parseInt(entries[0]));

            Map<String, String> attributes = new HashMap<String, String>();
            for (int i = 1; i < entries.length; i++) {
                String[] split = entries[i].split(":");
                Tuple<String, List<String>> keyValuePair = extractFeatureFromString(
                    featureLookup.get(Integer.parseInt(split[0])));
                String val = keyValuePair.y.get(0);
                if (keyValuePair.y.size() == 2 && Integer.parseInt(split[1]) != -1) {
                    val = keyValuePair.y.get(1);
                }
                attributes.put(keyValuePair.x, val);
                if (!featureKeyValues.containsKey(keyValuePair.x)) {
                    featureKeyValues.put(keyValuePair.x, new HashSet<String>());
                }
                featureKeyValues.get(keyValuePair.x).add(val);
            }
            res.add(new Record(label, attributes));
        }
        return res;
    }

    /**
     * Read data from a file.
     *
     * @param filename the path to the file
     * @return data mapped to each line number
     */
    private static Map<Integer, String> readData(String filename) {
        logger.info(String.format("Reading data from file: %s", filename));
        Map<Integer, String> result = new HashMap<Integer, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int i = 1;
            while (line != null) {
                result.put(i, line);
                i++;
                line = reader.readLine();
            }
            reader.close();
            logger.info(String.format("Succesfully read %d records from file: %s.", i, filename));
        } catch (IOException e) {
            logger
                .severe(String.format("Unable to extract records from file: %s. Error: %s", filename, e.getMessage()));
        }
        return result;
    }

    /**
     * Extract features from the string format used in the feature-names file.
     *
     * @param featureString a line from the feature-names file.
     * @return A tuple of the feature name and its value(s). The simple feature values are both added here.
     */
    public static Tuple<String, List<String>> extractFeatureFromString(String featureString) {
        String[] split = featureString.split("-");
        String featureName = split[0];
        List<String> featureValues = new ArrayList<String>();
        if (split.length > 2) {
            featureValues.add(split[1]);
            featureValues.add(split[2].substring(0, 1));
        } else {
            featureValues.add(split[1].substring(0, 1));
        }
        return new Tuple<String, List<String>>(featureName, featureValues);
    }

    /**
     * A tuple class
     *
     * @param <X> The first item's type
     * @param <Y> The second item's type
     */
    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
