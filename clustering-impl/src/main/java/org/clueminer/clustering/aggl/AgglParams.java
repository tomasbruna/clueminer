package org.clueminer.clustering.aggl;

import java.util.prefs.Preferences;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.LinkageFactory;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;

/**
 *
 * @author Tomas Barton
 */
public class AgglParams {

    private static final String PREFIX = "org.clueminer.clustering.aggl";

    public static final String LINKAGE = PREFIX + ".clusterLinkage";

    private static final String DEFAULT_DISTANCE_FUNCTION = "Euclidean";

    public static final String DISTANCE_FUNCTION = PREFIX + ".distanceMeasure";

    public static final String DEFAULT_LINKAGE = "Complete Linkage";

    /**
     * either we are clustering rows or columns
     */
    public static final String CLUSTER_ROWS = "cluster_rows";

    private Preferences pref;

    private DistanceMeasure distance;

    public AgglParams(Preferences props) {
        this.pref = props;
        init();
    }

    private void init() {
        distance = getDistanceMeasure();
    }

    public Preferences getPref() {
        return pref;
    }

    public void setPref(Preferences pref) {
        this.pref = pref;
    }

    public DistanceMeasure getDistanceMeasure() {
        String simFuncProp = pref.get(DISTANCE_FUNCTION, DEFAULT_DISTANCE_FUNCTION);
        return DistanceFactory.getDefault().getProvider(simFuncProp);
    }

    public ClusterLinkage getLinkage() {
        String linkageProp = pref.get(LINKAGE, DEFAULT_LINKAGE);
        ClusterLinkage linkage = LinkageFactory.getDefault().getProvider(linkageProp);
        linkage.setDistanceMeasure(distance);
        return linkage;
    }

    public boolean clusterRows() {
        return pref.getBoolean(CLUSTER_ROWS, true);
    }

    public boolean clusterColumns() {
        return !clusterRows();
    }

}