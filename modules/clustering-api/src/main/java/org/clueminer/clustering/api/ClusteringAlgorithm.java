package org.clueminer.clustering.api;

import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusteringAlgorithm<E extends Instance, C extends Cluster<E>> {

    String getName();

    /**
     * Cluster given dataset
     *
     * @param dataset
     * @param props a set of parameter that influence clustering or
     * performance
     * @return
     */
    Clustering<E, C> cluster(Dataset<E> dataset, Props props);

    Distance getDistanceFunction();

    void setDistanceFunction(Distance dm);

    /**
     * Algorithm responsible for assigning colors to new clusters
     *
     * @param cg
     */
    void setColorGenerator(ColorGenerator cg);

    /**
     *
     * @return
     */
    ColorGenerator getColorGenerator();

    /**
     * API for displaying progress in UI, if not set algorithm should work
     * anyway
     *
     * @param ph
     */
    void setProgressHandle(ProgressHandle ph);

    Parameter[] getParameters();
}
