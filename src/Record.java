import java.util.Map;

/**
 * A record from the train or test data. Contains a label and a map of attributes.
 *
 * @author Sander Ploegsma
 */
public class Record {
    private Map<String, String> attributes = null;
    private String label;

    public Record(String label, Map<String, String> attributes) {
        this.label = label;
        this.attributes = attributes;
    }

    public String getLabel() {
        return this.label;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}
