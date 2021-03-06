package org.clueminer.dataset.api;

import org.clueminer.math.Interpolator;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Dataset for representing time series
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Timeseries<E extends ContinuousInstance> extends Dataset<E> {

    public void crop(int begin, int end, ProgressHandle ph);

    public double interpolate(int index, double x, Interpolator interpolator);

    public TimePoint[] getTimePoints();

    public double[] getTimePointsArray();

    public double[] getTimestampsArray();

    public void setTimePoints(TimePoint[] tp);

    /**
     * Minimum value in the dataset
     *
     * @return
     */
    public double getMin();

    /**
     * Maximum value in the dataset
     *
     * @return
     */
    public double getMax();
}
