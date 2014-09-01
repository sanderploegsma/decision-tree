import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used for the calculation of the entropy using the Gini Index rule.
 *
 * @author Sander Ploegsma
 */
public class Gini {

    /**
     * Calculate the Gini index for the given data set. The index is defined as 1 - SUM p_j^2.
     *
     * @param data A list of records
     * @return the Gini index for the data set
     */
    public static Double calculateGini(List<Record> data) {
        Double res = 0.0;
        if (data.size() == 0) {
            return res;
        }

        Map<String, Integer> labelCount = new HashMap<String, Integer>();
        for (Record r : data) {
            if (!labelCount.containsKey(r.getLabel())) {
                labelCount.put(r.getLabel(), 0);
            }
            labelCount.put(r.getLabel(), labelCount.get(r.getLabel()) + 1);
        }

        res = 1.0;

        for (Map.Entry<String, Integer> entry : labelCount.entrySet()) {
            res -= Math.pow((double) entry.getValue() / data.size(), 2);
        }

        return res;
    }
}
