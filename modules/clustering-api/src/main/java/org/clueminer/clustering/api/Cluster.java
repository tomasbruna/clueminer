package org.clueminer.clustering.api;

import java.awt.Color;
import java.io.Serializable;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Cluster<E extends Instance> extends Dataset<E>, Cloneable, Serializable, Set<E> {

    /**
     * Set cluster identification number. Starts from 0, although cluster names
     * (for humans) should always start from 1.
     *
     * @param id
     */
    void setClusterId(int id);

    /**
     * Returns ID of the cluster (starts from 0)
     *
     * @return id
     */
    int getClusterId();

    /**
     * Color used for visualizations of clusters
     *
     * @return cluster's color
     */
    Color getColor();

    /**
     * Set (usually) unique color for easier identification of the cluster
     *
     * @param color
     */
    void setColor(Color color);

    /**
     * Centroids contains average value of all attributes in cluster
     *
     * @return usually non-existing element which is in the middle of the
     * cluster
     */
    E getCentroid();

    /**
     * Counts number of identical elements in both clusters
     *
     * @param c
     * @return
     */
    int countMutualElements(Cluster<E> c);

    /**
     * Add element to cluster and marks original id (position) in input matrix,
     * which could be checked if it is contained in cluster
     *
     * @param inst
     * @param origId
     */
    //public void add(E inst, int origId);
    /**
     * Checks presence of element by original id (position in input matrix)
     *
     * @param origId
     * @return true when element present
     */
    boolean contains(int origId);

    /**
     * By default return false (density based algorithms label typically last
     * cluster as outliers)
     *
     * @return true when cluster consists of outliers
     */
    boolean isOutlier();
}
