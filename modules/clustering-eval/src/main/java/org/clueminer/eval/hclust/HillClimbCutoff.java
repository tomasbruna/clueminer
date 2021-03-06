package org.clueminer.eval.hclust;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = CutoffStrategy.class)
public class HillClimbCutoff implements CutoffStrategy {

    protected InternalEvaluator evaluator;
    private static final String name = "hill-climb cutoff";

    public HillClimbCutoff() {
        //evaluator must be set after calling constructor!
    }

    public HillClimbCutoff(InternalEvaluator eval) {
        this.evaluator = eval;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double findCutoff(HierarchicalResult hclust, Props params) {
        check();
        double cutoff;
        Clustering clust, prevClust = null;
        double score, prev = Double.NaN, oldcut = 0;
        int level = hclust.treeLevels() - 1;
        boolean isClimbing = true;
        String evalName;
        int clustNum;
        do {
            cutoff = hclust.cutTreeByLevel(level);
            clust = hclust.getClustering();
            //System.out.println("level: " + level + ", clust = " + clust + ", cut = " + String.format("%.2f", cutoff));
            evalName = evaluator.getName();
            clustNum = clust.size();
            if (hclust.isScoreCached(evalName, clustNum)) {
                score = hclust.getScore(evalName, clustNum);
            } else {
                score = evaluator.score(clust, params);
            }
            //System.out.println("score = " + score + " prev= " + prev);
            hclust.setScores(evaluator.getName(), clust.size(), score);
            if (!Double.isNaN(prev)) {
                if (!evaluator.isBetter(score, prev)) {
                    //System.out.println("function is not climbing anymore, reverting");
                    hclust.setCutoff(oldcut);
                    hclust.setClustering(prevClust);
                    return oldcut;
                }
            }
            prev = score;
            prevClust = clust;
            oldcut = cutoff;
            level--;

        } while (isClimbing && !Double.isNaN(score));
        return cutoff;
    }

    public InternalEvaluator getEvaluator() {
        return evaluator;
    }

    @Override
    public void setEvaluator(InternalEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    protected void check() {
        if (evaluator == null) {
            throw new RuntimeException("evaluator method must be set!");
        }
    }
}
