package org.clueminer.clustering.aggl.linkage;

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
 * Single-linkage considers distance between two clusters as the minimal
 * distance between any two objects (each one from a different cluster)
 *
 * <math>D(X,Y)=\min_{x\in X, y\in Y} d(x,y)</math>
 *
 * Single Link or MIN
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = ClusterLinkage.class)
public class SingleLinkage<E extends Instance> extends AbstractLinkage<E> implements ClusterLinkage<E> {

    private static final long serialVersionUID = 6661476787499047883L;
    public static final String name = "Single";

    public SingleLinkage() {
        super(EuclideanDistance.getInstance());
    }

    public SingleLinkage(Distance dm) {
        super(dm);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double distance(Cluster<E> cluster1, Cluster<E> cluster2) {
        double distance = Double.MAX_VALUE;

        Instance x, y;
        double temp;
        for (int i = 0; i < cluster1.size(); i++) {
            x = cluster1.instance(i);
            for (int j = 0; j < cluster2.size(); j++) {
                y = cluster2.instance(j);
                temp = distanceMeasure.measure(x, y);
                if (temp < distance) {
                    distance = temp;
                }
            }
        }

        return distance;
    }

    /**
     * Proximity of two clusters is defined as the minimum of the distance
     * (maximum of the similarity) between any two points in the two different
     * clusters
     *
     * @param similarityMatrix
     * @param cluster
     * @param toAdd
     * @return
     */
    @Override
    public double similarity(Matrix similarityMatrix, Set<Integer> cluster, Set<Integer> toAdd) {
        double closest = Double.MAX_VALUE;
        for (int i : cluster) {
            for (int j : toAdd) {
                double s = similarityMatrix.get(i, j);
                //if (s < closest) {
                if (distanceMeasure.compare(s, closest)) {
                    closest = s;
                }
            }
        }
        return closest;
    }

    @Override
    public double alphaA(int ma, int mb, int mq) {
        return 0.5;
    }

    @Override
    public double alphaB(int ma, int mb, int mq) {
        return 0.5;
    }

    @Override
    public double beta(int ma, int mb, int mq) {
        return 0;
    }

    @Override
    public double gamma() {
        return -0.5;
    }
}
