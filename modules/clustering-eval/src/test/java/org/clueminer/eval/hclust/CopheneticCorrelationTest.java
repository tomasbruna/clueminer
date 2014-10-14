package org.clueminer.eval.hclust;

import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.hclust.linkage.CompleteLinkage;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for Cophenetic correlation coefficient and hierarchical clustering
 *
 * @see
 * http://people.revoledu.com/kardi/tutorial/Clustering/Numerical%20Example.htm
 *
 * @author Tomas Barton
 */
public class CopheneticCorrelationTest {

    private static Dataset<Instance> dataset;
    private static CopheneticCorrelation subject;
    private static Props params;
    private static HierarchicalResult rowsResult;

    @BeforeClass
    public static void setUpClass() throws Exception {
        subject = new CopheneticCorrelation();

        int instanceCnt = 10;
        dataset = new ArrayDataset<>(instanceCnt, 2);
        dataset.setName("test");
        dataset.attributeBuilder().create("X", "NUMERIC");
        dataset.attributeBuilder().create("Y", "NUMERIC");

        Instance i;
        i = dataset.builder().create(new double[]{1, 1});
        i.setName("A");
        dataset.add(i);
        i = dataset.builder().create(new double[]{1.5, 1.5});
        i.setName("B");
        dataset.add(i);
        i = dataset.builder().create(new double[]{5, 5});
        i.setName("C");
        dataset.add(i);
        i = dataset.builder().create(new double[]{3, 4});
        i.setName("D");
        dataset.add(i);
        i = dataset.builder().create(new double[]{4, 4});
        i.setName("E");
        dataset.add(i);
        i = dataset.builder().create(new double[]{3, 3.5});
        i.setName("F");
        dataset.add(i);

        System.out.println("dataset size " + dataset.size());

        params = new Props();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getName method, of class CopheneticCorrelation.
     */
    @Test
    public void testGetName() {
        assertEquals("Cophenetic Correlation", subject.getName());
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     */
    @Test
    public void testSingleLinkage() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.put(AgglParams.LINKAGE, "Single Linkage");
        rowsResult = algorithm.hierarchy(dataset, params);
        //CPCC with single linkage
        double cpcc = subject.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        assertEquals(0.864, cpcc, 0.001);
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     *
     */
    @Test
    public void testCompleteLinkage() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.put(AgglParams.LINKAGE, "Complete Linkage");
        rowsResult = algorithm.hierarchy(dataset, params);
        //CPCC with single linkage
        double cpcc = subject.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        //result according to Matlab implementation
        assertEquals(0.864, cpcc, 0.001);
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     *
     */
    @Test
    public void testAverageLinkage() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.put(AgglParams.LINKAGE, "Average Linkage");
        rowsResult = algorithm.hierarchy(dataset, params);
        //CPCC with single linkage
        double cpcc = subject.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        //according to Matlab implementation, the result should be 0.8658
        assertEquals(0.8658, cpcc, 0.0001);
    }

    /**
     * Test of getCopheneticMatrix method, of class CopheneticCorrelation.
     *
     * test data taken from
     *
     * @see http://people.revoledu.com/kardi/tutorial/Clustering/Cophenetic.htm
     */
    @Test
    public void testGetCopheneticMatrix() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        //params.put(AgglParams.LINKAGE, "Single Linkage");
        params.put(AgglParams.LINKAGE, "Complete Linkage");
        rowsResult = algorithm.hierarchy(dataset, params);
        double precision = 0.01;
        Matrix proximity = rowsResult.getProximityMatrix();
        double[][] copheneticMatrix = subject.copheneticMatrix(proximity, rowsResult.getTreeData());
        //symetrical matrix
        assertEquals(copheneticMatrix[1][2], copheneticMatrix[2][1], precision);
        //axis is equal to 0.0 (distance to itself)
        for (int i = 0; i < copheneticMatrix.length; i++) {
            assertEquals(copheneticMatrix[i][i], 0.0, precision);
        }

        Dump.matrix(copheneticMatrix, "cophn", 2);

        //we expect this matrix
        //0.00  0.71  2.50  2.50  2.50  2.50
        //0.71  0.00  2.50  2.50  2.50  2.50
        //2.50  2.50  0.00  1.41  1.41  1.41
        //2.50  2.50  1.41  0.00  1.00  0.50FF
        //2.50  2.50  1.41  1.00  0.00  1.00
        //2.50  2.50  1.41  0.50  1.00  0.00
        assertEquals(0.71, copheneticMatrix[0][1], precision);
        assertEquals(2.50, copheneticMatrix[0][2], precision);
        assertEquals(1.41, copheneticMatrix[2][3], precision);
        assertEquals(0.50, copheneticMatrix[5][3], precision);
        assertEquals(1.00, copheneticMatrix[5][4], precision);

        //  Covariance cov = new Covariance();
    }

    /**
     * New tree structure
     */
    @Test
    public void testGetCopheneticMatrixTreeStuct() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.put(AgglParams.LINKAGE, CompleteLinkage.name);
        rowsResult = algorithm.hierarchy(dataset, params);
        double precision = 0.01;
        Matrix proximity = rowsResult.getProximityMatrix();
        assertNotNull(rowsResult);
        DendroTreeData tree = rowsResult.getTreeData();
        assertNotNull(tree);
        assertEquals(6, proximity.rowsCount());
        assertEquals(6, proximity.columnsCount());
        double[][] copheneticMatrix = subject.getCopheneticMatrix(rowsResult.getTreeData(), proximity.rowsCount(), proximity.columnsCount());
        //symetrical matrix
        assertEquals(copheneticMatrix[1][2], copheneticMatrix[2][1], precision);
        //axis is equal to 0.0 (distance to itself)
        for (int i = 0; i < copheneticMatrix.length; i++) {
            assertEquals(copheneticMatrix[i][i], 0.0, precision);
        }

        //we expect this matrix
        //0.00  0.71  2.50  2.50  2.50  2.50
        //0.71  0.00  2.50  2.50  2.50  2.50
        //2.50  2.50  0.00  1.41  1.41  1.41
        //2.50  2.50  1.41  0.00  1.00  0.50
        //2.50  2.50  1.41  1.00  0.00  1.00
        //2.50  2.50  1.41  0.50  1.00  0.00
        assertEquals(0.71, copheneticMatrix[0][1], precision);
        assertEquals(2.50, copheneticMatrix[0][2], precision);
        assertEquals(1.41, copheneticMatrix[2][3], precision);
        assertEquals(0.50, copheneticMatrix[5][3], precision);
        assertEquals(1.00, copheneticMatrix[5][4], precision);

        //  Covariance cov = new Covariance();
    }

}
