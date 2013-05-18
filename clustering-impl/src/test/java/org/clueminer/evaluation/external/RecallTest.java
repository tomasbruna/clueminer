package org.clueminer.evaluation.external;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 * @author tombart
 */
public class RecallTest {

    private static Clustering irisCorrect;
    private static CommonFixture tf = new CommonFixture();
    private static Clustering irisWrong;
    private static Recall test;
    private static double delta = 1e-9;

    public RecallTest() throws FileNotFoundException, UnsupportedAttributeType, IOException {

        irisCorrect = FakeClustering.iris();
        irisWrong = FakeClustering.irisWrong();

    }

    @BeforeClass
    public static void setUpClass() {
        test = new Recall();
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
     * Test of getName method, of class Recall.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class Recall.
     */
    @Test
    public void testScore_Clustering_Dataset() {
        double score = test.score(irisCorrect, null);
        //this is fixed clustering which correspods to true classes in dataset
        assertEquals(1.0, score, delta);
        System.out.println(test.getName() + " = " + score);

        //delta here depends on random initialization of k-means
        long start = System.currentTimeMillis();
        score = test.score(irisWrong, null);
        long end = System.currentTimeMillis();
        
        assertEquals(0.53403755868544, score, delta);
        System.out.println(test.getName() + " = " + score);
        System.out.println("measuring " + test.getName() + " took " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Recall.
     */
    @Test
    public void testScore_3args() {
    }

    /**
     * Test of compareScore method, of class Recall.
     */
    @Test
    public void testCompareScore() {
    }
}