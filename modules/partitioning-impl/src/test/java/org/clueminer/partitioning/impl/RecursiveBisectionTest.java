package org.clueminer.partitioning.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import static junit.framework.Assert.assertEquals;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.graph.GraphBuilder.KNNGraphBuilder;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.junit.Test;

/**
 *
 * @author Tomas Bruna
 */
public class RecursiveBisectionTest extends PartitioningTest {

    @Test
    public void irisDataTest() throws IOException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
        KNNGraphBuilder knn = new KNNGraphBuilder();

        Dataset dataset = FakeDatasets.irisDataset();

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g, 5);
        RecursiveBisection rb = new RecursiveBisection(new FiducciaMattheyses());

        ArrayList<LinkedList<Node>> result = rb.partition(5, g);
        assertEquals(36, result.size());
    }

}
