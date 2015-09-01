package org.clueminer.chameleon;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.NanoBench;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Bruna
 */
public class ChameleonTest {

    double delta = 1e-9;

    @Test
    public void testGetName() {
        Chameleon ch = new Chameleon();
        assertEquals("Chameleon", ch.getName());
    }

    @Test
    public void testGlass() {
        final Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        final Chameleon ch = new Chameleon();
        pref.putInt(Chameleon.K, 5);
        pref.put(Chameleon.SIM_MEASURE, RiRcSimilarity.name);
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 2.0);

        //measure clustering run
        NanoBench.create().measurements(3).measure(
                "chameleon - glass (std)",
                new Runnable() {

                    @Override
                    public void run() {
                        HierarchicalResult result = ch.hierarchy(FakeDatasets.glassDataset(), pref);
                        DendroTreeData tree = result.getTreeData();
                        DendroNode root = tree.getRoot();
                        //assertEquals(933.5638730625637, root.getHeight(), delta);
                        assertEquals(856.2465904735895, root.getHeight(), delta);
                    }

                }
        );
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testIrisStandard() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putInt(Chameleon.K, 5);
        pref.put(Chameleon.SIM_MEASURE, RiRcSimilarity.name);
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 0.5);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.irisDataset(), pref);
        DendroTreeData tree = result.getTreeData();
        DendroNode root = tree.getRoot();
        //assertEquals(662.3346235252453, root.getHeight(), delta);
        assertEquals(660.141843476143, root.getHeight(), delta);
    }

    @Test
    public void testIrisImproved() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putInt(Chameleon.K, 5);
        pref.put(Chameleon.SIM_MEASURE, ShatovskaSimilarity.name);
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 0.5);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.irisDataset(), pref);
        DendroTreeData tree = result.getTreeData();
        DendroNode root = tree.getRoot();
        assertEquals(695.2089980350675, root.getHeight(), delta);
    }

    @Test
    public void testSchool() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        HierarchicalResult result = ch.hierarchy(FakeDatasets.schoolData(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(59.40222184544098, root.getHeight(), delta);
    }

    @Test
    public void testSchoolClosenessPriority() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.putDouble(Chameleon.CLOSENESS_PRIORITY, 4);
        HierarchicalResult result = ch.hierarchy(FakeDatasets.schoolData(), pref);
        DendroTreeData tree = result.getTreeData();
        tree.print();
        DendroNode root = tree.getRoot();
        assertEquals(265.38835528693505, root.getHeight(), delta);
    }

    @Test
    public void testSchoolKernighan() {
        Props pref = new Props();
        pref.putBoolean(AgglParams.CLUSTER_COLUMNS, false);
        Chameleon ch = new Chameleon();
        pref.put(Chameleon.BISECTION, "Kernighan-Lin");
        HierarchicalResult result = ch.hierarchy(FakeDatasets.schoolData(), pref);
        DendroTreeData tree = result.getTreeData();
        DendroNode root = tree.getRoot();
        result.findCutoff(CutoffStrategyFactory.getInstance().getProvider("naive cutoff"));
        assertNotNull(root);
        assertEquals(2, result.getClustering().size());
    }

}
