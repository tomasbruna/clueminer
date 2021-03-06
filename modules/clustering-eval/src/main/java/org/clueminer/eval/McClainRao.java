/*
 * Copyright (C) 2011-2015 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * This objective is called W/B in Milligan (1981)
 *
 * @param <E>
 * @param <C>
 * @cite
 * McClain, John O., and Vithala R. Rao. "Clustisz: A program to test for the
 * quality of clustering of a set of objects." JMR, Journal of Marketing
 * Research (pre-1986) 12.000004 (1975): 456.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class McClainRao<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "McClain-Rao";
    private static final long serialVersionUID = -3222061698654228829L;

    public McClainRao() {
        dm = EuclideanDistance.getInstance();
    }

    public McClainRao(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double nw = numW(clusters);
        double nt = numT(clusters);
        double nb = nt - nw;
        double sw = 0.0, sb;

        //sum of within cluster distances
        for (C clust : clusters) {
            sw += sumWithin(clust);
        }
        //sum of between cluster distances
        sb = sumBetween(clusters);

        return (nb / nw) * (sw / sb);
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

}
