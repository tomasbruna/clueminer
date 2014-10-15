package org.clueminer.evolution.bnb;

import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.evolution.AbstractIndividual;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class BnbIndividual extends AbstractIndividual<BnbIndividual> implements Individual<BnbIndividual> {

    private double fitness = 0;
    private static Random rand = new Random();
    private Clustering<? extends Cluster> clustering;
    private Props genom;

    public BnbIndividual(Evolution evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        this.genom = new Props();
        init();
    }

    /**
     * Copying constructor
     *
     * @param parent
     */
    public BnbIndividual(BnbIndividual parent) {
        this.evolution = parent.evolution;
        this.algorithm = parent.algorithm;
        this.genom = parent.genom.copy();

        this.fitness = parent.fitness;
    }

    private void init() {
        genom.put(AgglParams.ALG, algorithm.getName());
        genom.putBoolean(AgglParams.LOG, logscale(rand));
        genom.put(AgglParams.STD, std(rand));
        genom.putBoolean(AgglParams.CLUSTER_ROWS, true);
        genom.put(AgglParams.CUTOFF_STRATEGY, "hill-climb cutoff");
        genom.put(AgglParams.CUTOFF_SCORE, evolution.getEvaluator().getName());
        genom.put(AgglParams.LINKAGE, linkage(rand));
        genom.put(AgglParams.DIST, distance(rand));
        countFitness();
    }

    private boolean logscale(Random rand) {
        return rand.nextBoolean();
    }

    private String std(Random rand) {
        int size = ((BnbEvolution) evolution).standartizations.size();
        int i = rand.nextInt(size);
        return ((BnbEvolution) evolution).standartizations.get(i);
    }

    private String linkage(Random rand) {
        int size = ((BnbEvolution) evolution).linkage.size();
        int i = rand.nextInt(size);
        return ((BnbEvolution) evolution).linkage.get(i).getName();
    }

    private String distance(Random rand) {
        int size = ((BnbEvolution) evolution).dist.size();
        int i = rand.nextInt(size);
        return ((BnbEvolution) evolution).dist.get(i).getName();
    }

    @Override
    public Clustering<? extends Cluster> getClustering() {
        return clustering;
    }

    @Override
    public void countFitness() {
        clustering = updateCustering();
        fitness = evaluationTable(clustering).getScore(evolution.getEvaluator());
    }

    /**
     * Some algorithms (like k-means) have random initialization, so we can't
     * reproduce the same results, therefore we have to keep the resulting
     * clustering
     *
     * @return clustering according to current parameters
     */
    private Clustering<? extends Cluster> updateCustering() {
        DistanceMeasure dm = DistanceFactory.getInstance().getProvider(genom.getString(AgglParams.DIST));
        clustering = ((BnbEvolution) evolution).exec.clusterRows(evolution.getDataset(), dm, genom);

        return clustering;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    /**
     * For tests only
     *
     * @param fitness
     */
    protected void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public void mutate() {
        if (performMutation()) {
            genom.putBoolean(AgglParams.LOG, logscale(rand));
        }
        if (performMutation()) {
            genom.put(AgglParams.STD, std(rand));
        }
        if (performMutation()) {
            genom.put(AgglParams.LINKAGE, linkage(rand));
        }
        if (performMutation()) {
            genom.put(AgglParams.DIST, distance(rand));
        }
    }

    @Override
    public List<BnbIndividual> cross(Individual i) {
        throw new UnsupportedOperationException("not supported yet");
    }

    private boolean performMutation() {
        return rand.nextDouble() < evolution.getMutationProbability();
    }

    @Override
    public BnbIndividual deepCopy() {
        BnbIndividual newOne = new BnbIndividual(this);
        return newOne;
    }

    @Override
    public boolean isCompatible(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BnbIndividual duplicate() {
        BnbIndividual duplicate = new BnbIndividual(evolution);
        return duplicate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(genom.toString());
        sb.append("]");
        return sb.toString();
    }
}