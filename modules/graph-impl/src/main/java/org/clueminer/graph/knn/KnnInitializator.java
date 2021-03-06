package org.clueminer.graph.knn;


import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.KnnFactory;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Hamster
 * @param <E>
 */
@ServiceProvider(service = GraphConvertor.class)
public class KnnInitializator<E extends Instance> implements GraphConvertor<E> {

    private Distance dm;
    private static final String name = "k-NN";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Find k-nearest neighbors and add an edge between nodes
     *
     * @param graph
     * @param dataset
     * @param mapping
     * @param params
     */
    @Override
    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params) {
        KnnFactory<E> kf = KnnFactory.getInstance();
        KNNSearch<E> alg = kf.getDefault();
        if (alg == null) {
            throw new RuntimeException("did not find any provider for k-NN");
        }
        alg.setDataset(dataset);
        alg.setDistanceMeasure(dm);
        int k = params.getInt("k", 5);
        Neighbor[] nn;
        long nodeId;
        Node<E> target;
        Edge edge;
        for (Node<E> node : graph.getNodes()) {
            nn = alg.knn(node.getInstance(), k, params);
            for (Neighbor neighbor : nn) {
                nodeId = mapping[neighbor.index];
                target = graph.getNode(nodeId);
                edge = graph.getFactory().newEdge(node, target);
                graph.addEdge(edge);
            }
        }
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

}
