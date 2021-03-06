package org.clueminer.hclust;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroTreeDataOld;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Dump;

/*
 *
 * Stores tree structure
 *
 *                     says what is this level level and at which index are children
 * Example:                                         v
 *   idx  | 0 |  1 |  2  | 3 |  4 |  5 |  6 |  7 |  8 |  9 |
 * -------------------------------------------------------------------------------------
 * level: 0.0, 0.0, 0.0, 0.0, 0.0, 0.9, 1.5, 1.9, 7.4, 0.0
 * left:    -1,  -1,  -1,  -1,  -1,   1,   2,   0,   5,  -1
 * right:   -1,  -1,  -1,  -1,  -1,   3,   4,   6,   7,  -1
 * order: 5, 6, 7, 8, -1         ^
 *                 ^       node is a leaf
 *            tree top level
 *
 * @author Tomas Barton
 *
 */
public class TreeDataImpl implements Serializable, DendroTreeData, DendroTreeDataOld {

    private static final long serialVersionUID = -3984381476142130357L;
    private int[] left;
    private int[] right;
    private int[] order;
    private double[] height;
    private Distance function;
    private double cutoff = -1;
    private int clusterAssignment[];
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private static final Logger logger = Logger.getLogger(TreeDataImpl.class.getName());
    private int numLeaves;

    /**
     * number of cluster starts from 1, if num eq 0, node wasn't visited by the
     * numbering algorithm
     */
    private int clusterNum = 0;

    public TreeDataImpl(Distance function) {
        this.function = function;
    }

    /**
     * Array size is 2*level
     *
     * @param idx
     * @return index in level array
     */
    public int getLeft(int idx) {
        return this.left[idx];
    }

    /**
     * @param idx
     * @param left_children
     */
    public void setLeft(int idx, int left_children) {
        this.left[idx] = left_children;
    }

    public void setLeft(int[] left) {
        this.left = left;
    }

    /**
     * Array size is 2*level if level(right[idx]) == 0 => node is leaf
     *
     * @param idx
     * @return index in level array
     */
    public int getRight(int idx) {
        return right[idx];
    }

    /**
     * @param idx
     * @param right_children
     */
    public void setRight(int idx, int right_children) {
        this.right[idx] = right_children;
    }

    public void setRight(int[] right) {
        this.right = right;
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

    /**
     * @param idx
     * @return node index of tree levels
     */
    public int getOrder(int idx) {
        return this.order[idx];
    }

    public int getOrderLength() {
        return order.length;
    }

    /**
     * @param idx
     * @return tree levels at @idx level
     */
    public double getHeight(int idx) {
        return this.height[idx];
    }

    /**
     * level[0] says on which level are children in
     *
     * @param height array of level's levels
     */
    public void setHeight(double[] height) {
        this.height = height;

        //some distance functions might produce negative values which doesn't
        //make much sense in context of tree's level, therefore we have to
        //move the range
        double nodeHeightOffset;
        if (function.useTreeHeight()) {
            nodeHeightOffset = getMinHeight() * function.getNodeOffset();
        } else {
            nodeHeightOffset = function.getNodeOffset();
        }

        if (nodeHeightOffset != 0.0) {
            for (int i = 0; i < height.length; i++) {
                height[i] += nodeHeightOffset;
            }
        }
        //Dump.array(level, "tree level");
    }

    public void setFunction(Distance function) {
        this.function = function;
    }

    public Distance getFunction() {
        return function;
    }

    /**
     * Number includes leaves
     *
     * @return int
     */
    @Override
    public int treeLevels() {
        return order.length - 1;
    }

    @Override
    public int distinctHeights(double tolerance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Number of terminal nodes
     *
     * @return
     */
    @Override
    public int numLeaves() {
        return height.length / 2;
    }

    @Override
    public boolean isLeaf(int idx) {
        return getLeft(idx) == -1 && getRight(idx) == -1;
    }

    /**
     * doesn't work with negative distances
     *
     * @param zero_threshold
     * @return
     */
    public int getNumberOfTerminalNodes(double zero_threshold) {
        int n = 0;
        int index;

        for (int i = 0; i < order.length; i++) {
            index = order[i];
            if (index == -1 || height[index] < zero_threshold) {
                continue;
            }
            n++;
        }
        return n + 1;
    }

    /**
     * Get idx of tree's root
     *
     * @return node's idx
     */
    public int getIntRoot() {
        return order[order.length - 2];
    }

    /**
     * From tree cutoff we can determine the number of clusters
     *
     * @return number between 0 and max tree level
     */
    public double getCutoff() {
        return this.cutoff;
    }

    public void setCutoff(double cutoff) {
        clusterAssignment = null; //clear result, if any
        clusterNum = 0;
        this.cutoff = cutoff;
    }

    public double treeCutByLevel(int level) {
        double lower = 0.0, upper, dist = 0.0;
        int idx;
        if (level < treeLevels()) {
            idx = getOrder(level);
            upper = getHeight(idx);
            if (level > 0) {
                idx = getOrder(level - 1);
                lower = getHeight(idx);
            }
            dist = upper - lower;
        }
        //half of distance between two levels
        return (dist / 2 + lower);
    }

    public void formClusters(int leavesNum) {
        synchronized (this) {
            if (leavesNum < 1) {
                throw new RuntimeException("number of nodes " + leavesNum + " is invalid");
            }
            //ensureClusters(nodesNum);
            logger.log(Level.INFO, "allocating space for : {0}", new Object[]{leavesNum});
            clusterAssignment = new int[leavesNum];
            findClusters(getIntRoot(), -1);
        }
    }

    /**
     * @deprecated this might cause strange null pointer exception
     */
    public void formClusters() {
        synchronized (this) {
            int nodesNum = getNumberOfTerminalNodes(0.00001);
            logger.log(Level.INFO, "expected tree nodes number: {0}", new Object[]{nodesNum});
            clusterAssignment = new int[nodesNum];
            findClusters(getIntRoot(), -1);
            Dump.array(clusterAssignment, "result clusters");
        }
    }

    /**
     * Number specifies cluster assignment
     *
     * clusters [ 1 2 2 2 1 2 2 2 2 2 2 2 2 2 2 2 2 ]
     *
     * @param terminalsNum
     * @return array of node's assignments
     */
    public int[] getClusters(int terminalsNum) {
        if (clusterAssignment == null) {
            formClusters(terminalsNum);
        }
        // for each leaf we want its cluster assignment
        logger.log(Level.INFO, "leaves {0}, actual clusters size {1}", new Object[]{numLeaves(), clusterAssignment.length});
        return clusterAssignment;
    }

    /**
     * Number of cluster found by given cutoff
     *
     * @return
     */
    public int getNumberOfClusters() {
        if (clusterNum == 0) {
            //each node is a cluster for itself. we don't have clustering yet
            return numLeaves();
        }
        return clusterNum;
    }

    /**
     * According to given cutoff, assign items to clusters numbered from 1 to k
     *
     * @param idx
     * @param parent
     */
    public void findClusters(int idx, int parent) {
        if (parent > -1) {
            if (getHeight(parent) > cutoff && getHeight(idx) < cutoff) {
                //in between cutoff border -> create a cluster
                clusterNum++;
                //System.out.println("parent= "+getHeight(parent)+ ", node= "+getHeight(idx) + ", clustNum= "+clusterNum);
            }
        }

        if (isLeaf(idx)) {
            //Logger.getLogger(TreeDataImpl.class.getName()).log(Level.INFO, "getting {0} clusters size: {1}", new Object[]{idx, clusters.length});
            //assign cluster's id
            //logger.log(Level.INFO, "setting idx: {0} to cluster {1}", new Object[]{idx, clusterNum});
            clusterAssignment[idx] = clusterNum;
            return;
        }

        //left node
        findClusters(getLeft(idx), idx);
        //right node
        findClusters(getRight(idx), idx);
    }

    /**
     * Returns min level of the tree nodes.
     *
     * @return
     */
    public double getMinHeight() {
        if (min == Double.MAX_VALUE) {
            for (int i = 0; i < order.length - 1; i++) {
                min = Math.min(min, height[order[i]]);
            }
        }
        return min;
    }

    /**
     * Returns max level of the tree nodes.
     *
     * @return
     */
    public double getMaxHeight() {
        if (max == Double.MIN_VALUE) {
            for (int i = 0; i < order.length - 1; i++) {
                max = Math.max(max, height[order[i]]);
            }
        }
        return max;
    }

    /**
     * Returns true if tree is flat
     *
     * @return
     */
    public boolean flatTreeCheck() {
        if (height.length == 1) {
            return false;
        }

        for (int i = 0; i < height.length - 1; i++) {
            if (height[i] != height[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates tree node positions.
     *
     * @return
     */
    public float[] getPositions() {
        float[] pos = new float[left.length];
        Arrays.fill(pos, -1);
        if (order.length < 2) {
            return pos;
        }
        fillPositions(pos, left, right, 0, left.length - 2);
        int node;
        for (int i = 0; i < order.length - 1; i++) {
            node = order[i];
            pos[node] = (pos[left[node]] + pos[right[node]]) / 2f;
        }
        Dump.array(pos, "positions");
        return pos;
    }

    private int fillPositions(float[] positions, int[] child1, int[] child2, int pos, int index) {
        if (child1[index] != -1) {
            pos = fillPositions(positions, child1, child2, pos, child1[index]);
        }
        if (child2[index] != -1) {
            pos = fillPositions(positions, child1, child2, pos, child2[index]);
        } else {
            positions[index] = pos;
            pos++;
        }
        return pos;
    }

    public boolean isEmpty() {
        return this.height == null;
    }

    public int[] createTreeOrder() {
        return createTreeOrder(null);
    }

    public int[] createTreeOrder(int[] indices) {
        return getLeafOrder(indices);
    }

    private int[] getLeafOrder(int[] indices) {
        if (this.isEmpty() || this.getOrderLength() < 2) {
            return null;
        }
        return getLeafOrder(getOrderLength(), this.left, this.right, indices);
    }

    private int[] getLeafOrder(int nodeOrderLen, int[] left, int[] right, int[] indices) {
        int[] leafOrder = new int[nodeOrderLen];
        Arrays.fill(leafOrder, -1);
        fillLeafOrder(leafOrder, left, right, 0, left.length - 2, indices);
        return leafOrder;
    }

    /**
     * @TODO rewrite to iterative version
     *
     * @param leafOrder
     * @param child1
     * @param child2
     * @param pos
     * @param index
     * @param indices
     * @return
     */
    private int fillLeafOrder(int[] leafOrder, int[] child1, int[] child2, int pos, int index, int[] indices) {
        if (child1[index] != -1) {
            pos = fillLeafOrder(leafOrder, child1, child2, pos, child1[index], indices);
        }
        if (child2[index] != -1) {
            pos = fillLeafOrder(leafOrder, child1, child2, pos, child2[index], indices);
        } else {
            leafOrder[pos] = indices == null ? index : indices[index];
            pos++;
        }
        return pos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TreeData [");
        sb.append("function= ").append(function).append("\n");
        sb.append("height: ");
        for (int i = 0; i < height.length; i++) {
            sb.append(i).append(" = ").append(height[i]).append(", ");
        }
        sb.append("\n");

        sb.append("left: ");
        for (int i = 0; i < left.length; i++) {
            sb.append(i).append(" = ").append(left[i]).append(", ");
        }
        sb.append("\n");

        sb.append("right: ");
        for (int i = 0; i < right.length; i++) {
            sb.append(i).append(" = ").append(right[i]).append(", ");
        }
        sb.append("\n");

        sb.append("order: ");
        for (int i = 0; i < order.length; i++) {
            sb.append(i).append(" = ").append(order[i]).append(", ");
        }
        sb.append("\n");

        sb.append("]");
        return sb.toString();
    }

    @Override
    public int numNodes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Converts array representation to a tree structure
     *
     * @return tree root
     */
    @Override
    public DendroNode getRoot() {
        int id = getIntRoot();
        int level = treeLevels();
        numLeaves = 0; // just a counter
        DTreeNode root = new DTreeNode(true);
        root.setHeight(getHeight(id));
        root.setLevel(level);

        root.setLeft(constructSubTree(getLeft(id), root, level - 1));
        root.setRight(constructSubTree(getRight(id), root, level - 1));

        //computes node's position
        updatePositions(root);

        return root;
    }

    @Override
    public double updatePositions(DendroNode node) {
        if (node.isLeaf()) {
            return node.getPosition();
        }

        double position = (updatePositions(node.getLeft()) + updatePositions(node.getRight())) / 2.0;
        node.setPosition(position);
        return position;
    }

    private DendroNode constructSubTree(int node, DTreeNode parent, int level) {
        DTreeNode current = new DTreeNode(parent);
        if (isLeaf(node)) {
            //first node has 0, leaves has coordinates [0, position]
            current.setPosition(numLeaves++);
        } else {
            current.setHeight(getHeight(node));
            current.setLevel(level);
            //explore subtree
            current.setLeft(constructSubTree(getLeft(node), current, level - 1));
            current.setRight(constructSubTree(getRight(node), current, level - 1));
        }
        current.setId(node);
        return current;
    }

    @Override
    public DendroNode setRoot(DendroNode root) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Left-most node
     *
     * @return
     */
    @Override
    public DendroNode first() {
        DendroNode current = getRoot();
        while (!current.isLeaf()) {
            current = current.getLeft();
        }
        return current;
    }

    @Override
    public void print() {
        Dump.array(height, "height");
        Dump.array(left, "left");
        Dump.array(right, "right");
        DendroNode node = getRoot();
        DynamicTreeData data = new DynamicTreeData(node, height.length);
        data.print();
    }

    @Override
    public void setMapping(int[] mapping) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DendroNode getLeaf(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLeaves(DendroNode[] leaves) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMappedId(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLeaf(int i, DendroNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getMapping() {
        return clusterAssignment;
    }

    @Override
    public int[] createMapping(int n, DendroNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(DendroNode treeRoot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printWithHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsClusters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int distinctHeights() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] createMapping(int n, DendroNode node, DendroNode noise) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
