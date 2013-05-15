package org.clueminer.evaluation.external;

import com.google.common.collect.BiMap;
import com.google.common.collect.Table;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class CountingPairsTest {

    private static Clustering clusters;
    private static CountingPairs test;
    private static CommonFixture tf = new CommonFixture();
    private static Clustering iris;

    public CountingPairsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, UnsupportedAttributeType, IOException {
        clusters = FakeClustering.iris();
        test = new CountingPairs();

        //now try some real clustering
        ClusteringAlgorithm km = new KMeans(3, 100, new EuclideanDistance());
        ARFFHandler arff = new ARFFHandler();
        Dataset<Instance> irisDataset = new SampleDataset();
        arff.load(tf.irisArff(), irisDataset, 4);
        iris = km.partition(irisDataset);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of countPairs method, of class CountingPairs.
     */
    @Test
    public void testCountPairs() {
        //this clustering has all assignments correct
        Table<String, String, Integer> table = test.countPairs(clusters);
        for (String row : table.rowKeySet()) {
            Map<String, Integer> map = table.row(row);
            for (String klass : map.keySet()) {
                int cnt = map.get(klass);
                //each class has 50 members
                assertEquals(50, cnt);
            }
        }
        System.out.println(table.toString());
    }

    /**
     * Test of findMatching method, of class CountingPairs.
     */
    @Test
    public void testFindMatching() {
        Table<String, String, Integer> table = test.countPairs(iris);
        BiMap<String, String> matching = test.findMatching(table);
        //we have 3 different classes
        assertEquals(3, matching.size());
    }

    /**
     * Test of countAssignments method, of class CountingPairs.
     */
    @Test
    public void testCountAssignments() {
        Table<String, String, Integer> table = test.countPairs(iris);
        BiMap<String, String> matching = test.findMatching(table);
        Map<String, Integer> res;
        
        int tp, fp, fn, tn, sum;
        for (String cluster : matching.values()) {
            System.out.println(cluster + " corresponds to "+matching.inverse().get(cluster));
            res = test.countAssignments(table, matching, cluster);
            assertEquals(4, res.size());
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            tn = res.get("tn");
            System.out.println("tp = " + tp + " | fp = " + fp);
            System.out.println("fn = " + fn + " | tn = " + tn);
            sum = tp+ fp+fn+tn;
            assertEquals(iris.instancesCount(), sum);
        }

    }

    /**
     * Test of countPairs2 method, of class CountingPairs.
     */
    @Test
    public void testCountPairs2() {
    }
}