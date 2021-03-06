package org.clueminer.graph.adjacencyList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 *
 * @author Hamster
 */
public class AdjListEdgeTest {

    private static final double delta = 1e-9;

    @Test
    public void completeConstructorTest() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        double weight = 2.5;
        boolean directed = true;
        AdjListEdge edge = new AdjListEdge(id, source, target, directed, weight);
        assertEquals(id, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        assertEquals(directed, edge.isDirected());
        assertEquals(weight, edge.getWeight(), delta);
    }

    @Test
    public void defaultConstructorTest() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        AdjListEdge edge = new AdjListEdge(id, source, target);
        assertEquals(id, edge.getId());
        assertSame(source, edge.getSource());
        assertSame(target, edge.getTarget());
        assertFalse(edge.isDirected());
        assertEquals(1, edge.getWeight(), delta);
    }

    @Test
    public void testWeightSetter() {
        long id = 5;
        AdjListNode source = new AdjListNode(1);
        AdjListNode target = new AdjListNode(2);
        double weight = 10.0;
        AdjListEdge edge = new AdjListEdge(id, source, target, false, weight);
        assertEquals(weight, edge.getWeight(), delta);
        edge.setWeight(-5.0);
        assertEquals(-5.0, edge.getWeight(), delta);
    }
}
