package org.clueminer.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A node structure for DAG (directed acyclic graph)
 *
 * @author Tomas Barton
 * @param <T>
 */
public class Node<T> implements Iterable<Node<T>> {

    private final T value;
    private final List<Node<T>> outEdges = new LinkedList<>();
    private final List<Node<T>> inEdges = new LinkedList<>();

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * Add outgoing edge
     *
     * @param otherNode
     */
    public void addOutEdge(Node<T> otherNode) {
        if (this.containsSucc(otherNode)) {
            throw new RuntimeException("cycle detected");
        }
        outEdges.add(otherNode);
        otherNode.addInEdge(this);
    }

    public void addInEdge(Node<T> parentNode) {
        inEdges.add(parentNode);
    }

    public void removeOutEdge(Node<T> otherNode) {
        outEdges.remove(otherNode);
        otherNode.removeInEdge(this);
    }

    public void removeInEdge(Node<T> node) {
        inEdges.remove(node);
    }

    public int outEdgesCnt() {
        return outEdges.size();
    }

    public int inEdgesCnt() {
        return inEdges.size();
    }

    /**
     *
     * @param node
     * @return true if any connected node containsSucc given node
     */
    public boolean containsSucc(Node<T> node) {
        if (node.equals(this)) {
            return true;
        }
        for (Node<T> other : outEdges) {
            //recursive
            if (other.equals(node) || other.containsSucc(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterator over outgoing edges
     *
     * @return outgoing edges iterator
     */
    @Override
    public Iterator<Node<T>> iterator() {
        return new NodeOutIterator();
    }

    /**
     * Iterator over incoming edges (node's successors)
     *
     * @return incoming edges iterator
     */
    public Iterator<Node<T>> inIterator() {
        return new NodeInIterator();
    }

    class NodeOutIterator implements Iterator<Node<T>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < outEdgesCnt();
        }

        @Override
        public Node<T> next() {
            return outEdges.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from nodes using the iterator.");

        }
    }

    class NodeInIterator implements Iterator<Node<T>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < inEdgesCnt();
        }

        @Override
        public Node<T> next() {
            return inEdges.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from nodes using the iterator.");

        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node(in->(").append(inEdgesCnt()).append("), <-out:(").append(outEdgesCnt()).append("))");
        sb.append("[").append(getValue()).append("]");
        sb.append("in: ");
        for (int i = 0; i < inEdgesCnt(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(inEdges.get(i).getValue());
        }
        sb.append("; out: ");
        for (int i = 0; i < outEdgesCnt(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(outEdges.get(i).getValue());
        }
        return sb.toString();
    }
}
