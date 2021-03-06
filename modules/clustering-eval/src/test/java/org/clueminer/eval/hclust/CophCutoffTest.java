package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class CophCutoffTest {

    private static final CophCutoff subject = new CophCutoff();

    @Test
    public void testFindCutoff() {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        HACLW alg = new HACLW();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        pref.put(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        //result.getTreeData().print();
        double cut = subject.findCutoff(result, pref);
        assertEquals(true, cut > 0);
        assertEquals(6, result.getClustering().size());
    }

}
