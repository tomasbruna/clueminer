package org.clueminer.clustering.aggl.linkage;

import java.util.Arrays;
import java.util.Set;
import org.clueminer.clustering.api.AbstractLinkage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Median linkage is a variation of {@link AverageLinkage}, this method should
 * be less sensitive to outliers because we consider distance between existing
 * data points and not just "virtual ones".
 *
 * A.K.A. Weighted Pair Group Method using Centroids
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = ClusterLinkage.class)
public class MedianLinkage<E extends Instance> extends AbstractLinkage<E> implements ClusterLinkage<E> {

    private static final long serialVersionUID = 7942079385178130303L;
    public static final String name = "Median";

    public MedianLinkage() {
        super(EuclideanDistance.getInstance());
    }

    public MedianLinkage(Distance dm) {
        super(dm);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Clusters will be compared using the similarity of the computed median
     * data point for each cluster
     *
     * @param cluster1 first cluster
     * @param cluster2 second cluster
     * @return distance between clusters computed with current distance metric
     */
    @Override
    public double distance(Cluster<E> cluster1, Cluster<E> cluster2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        double[] similarities = new double[cluster.size() * toAdd.size()];
        int index = 0;
        for (int i : cluster) {
            for (int j : toAdd) {
                similarities[index++] = similarityMatrix.get(i, j);
            }
        }
        Arrays.sort(similarities);
        return similarities[similarities.length / 2];
    }

    @Override
    public double alphaA(int ma, int mb, int mq) {
        return ma + mb;
    }

    @Override
    public double alphaB(int ma, int mb, int mq) {
        return (double) mb / (ma + mb);
    }

    @Override
    public double beta(int ma, int mb, int mq) {
        return (double) -(ma * mb) / (Math.pow((ma + mb), 2));
    }

    @Override
    public double gamma() {
        return 0;
    }
}
