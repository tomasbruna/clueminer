package org.clueminer.clustering.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.params.AlgParam;
import org.clueminer.clustering.params.ParamType;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractClusteringAlgorithm<E extends Instance, C extends Cluster<E>> implements ClusteringAlgorithm<E, C> {

    // don't mutate distance by default - most of evaluation metrics are not
    // adjusted for this
    //@Param(name = AgglParams.DIST,
    //       factory = "org.clueminer.distance.api.DistanceFactory",
    //       type = org.clueminer.clustering.params.ParamType.STRING)
    protected Distance distanceFunction;

    //standartization method that is used as part of preprocessing
    @Param(name = AgglParams.STD,
           factory = "org.clueminer.dataset.api.DataStandardizationFactory",
           type = org.clueminer.clustering.params.ParamType.STRING)
    protected DataStandardization std;

    //apply logarithm to all values
    @Param(name = AgglParams.LOG,
           type = org.clueminer.clustering.params.ParamType.BOOLEAN)
    protected boolean logScale;

    protected ColorGenerator colorGenerator;
    protected ProgressHandle ph;

    public static final String DISTANCE = "distance";

    /**
     * Cluster label for outliers or noises.
     */
    public static final int OUTLIER = Integer.MAX_VALUE;

    public static final String OUTLIER_LABEL = "noise";

    @Override
    public Distance getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(Distance dm) {
        this.distanceFunction = dm;
    }

    @Override
    public ColorGenerator getColorGenerator() {
        return colorGenerator;
    }

    @Override
    public void setColorGenerator(ColorGenerator colorGenerator) {
        this.colorGenerator = colorGenerator;
    }

    @Override
    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    /**
     * Get all algorithm parameters that could be modified.
     *
     * @return all algorithm parameters
     */
    @Override
    public Parameter[] getParameters() {
        Collection<Parameter> res = new LinkedList<>();
        Class<?> clazz = getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                //Class type = field.getType();
                //from JDK8: field.getAnnotationsByType(Param.class);
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation anno : annotations) {
                    if (anno instanceof Param) {
                        Param p = (Param) anno;
                        String paramName = p.name();
                        if (paramName.isEmpty()) {
                            paramName = field.getName();
                        }
                        ParamType type;
                        if (p.type() != ParamType.NULL) {
                            type = p.type();
                        } else {
                            //auto-detection
                            switch (field.getType().getName()) {
                                case "double":
                                    type = ParamType.DOUBLE;
                                    break;
                                case "int":
                                    type = ParamType.INTEGER;
                                    break;
                                case "String":
                                    type = ParamType.STRING;
                                    break;
                                default:
                                    throw new RuntimeException("unknown type " + field.getType().getName());
                            }
                        }
                        Parameter out = new AlgParam(paramName, type, p.description(), p.factory());
                        switch (type) {
                            case DOUBLE:
                            case INTEGER:
                                out.setMin(p.min());
                                out.setMax(p.max());
                                break;
                        }
                        res.add(out);
                    }
                }
            }
            //go to parent class
            clazz = clazz.getSuperclass();
        }
        return res.toArray(new Parameter[res.size()]);
    }

}
