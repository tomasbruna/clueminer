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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Bisection.class)
public class HMetisBisector extends HMetis implements Bisection {

    private static final String name = "hMETIS";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<LinkedList<Node>> bisect(Graph g) {
        int k = 2;
        Node[] nodeMapping = createMapping(g);
        //we want to split graph into 2 parts
        runMetis(g, k);
        ArrayList<LinkedList<Node>> clusters = importMetisResult(k, nodeMapping);
        return clusters;
    }

    @Override
    public ArrayList<LinkedList<Node>> bisect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Graph removeUnusedEdges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
