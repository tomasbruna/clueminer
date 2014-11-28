package org.clueminer.eval.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.eval.utils.CountingPairs;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class PrecisionNorm extends AbstractCountingPairs {

    private static final String name = "Precision";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countScore(Table<String, String, Integer> table, Clustering<? extends Cluster> ref, BiMap<String, String> matching) {
        Map<String, Integer> res;
        int tp, fp;
        double index = 0.0;
        double precision;
        //for each cluster we have score of quality
        Cluster c;
        for (String cluster : matching.values()) {
            c = ref.get(cluster);
            //we intentionally ignore computing precision in clusters with single instance
            if (c.size() > 1) {
                res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
                //System.out.println("class: " + matching.inverse().get(cluster) + " cluster = " + cluster);
                tp = res.get("tp");
                fp = res.get("fp");
                //System.out.println("sum = " + (tp + fp + res.get("fn") + res.get("tn")));
                precision = tp / (double) (tp + fp);
                //System.out.println("precision = " +precision);
                index += precision;
            }
        }

        //norm average value - we want to penalize higer number of cluster
        return index / (Math.abs(table.columnKeySet().size() - table.rowKeySet().size()) + table.columnKeySet().size());
    }

}