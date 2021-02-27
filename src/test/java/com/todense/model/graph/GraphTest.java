package com.todense.model.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    Graph graph = new Graph();

    @Test
    void test(){
        Node n1 = graph.addNode();
        Node n2 = graph.addNode();

        Edge e =  graph.addEdge(n1, n2);

        assertThrows(AssertionError.class, () -> graph.addEdge(n2, n1));
        assertEquals(graph.getEdges().size(), 1);
        assertEquals(graph.getEdges().getEdge(n1, n2), e);
        assertEquals(graph.getEdges().getEdge(n2, n1), e);
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(n1, n1));

        Node n3 = graph.addNode();
        Node n4 = graph.addNode();

        Edge e1 = graph.addEdge(n3, n4);
        Edge e2 = graph.addEdge(n1, n4);

        assertEquals(graph.getEdges().size(), 3);

        graph.removeEdge(e1);
        graph.removeEdge(e1);

        graph.removeEdge(e2);

        assertEquals(graph.getEdges().size(), 1);

        graph.removeNode(n1);
        assertEquals(graph.getNodes().get(graph.getNodes().size()-1).getIndex(), graph.getNodes().size()-1);
    }

}