package com.todense.viewmodel.graph;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.preset.PresetCreator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphManagerTest {

    @Test
    void complementTest(){
        GraphManager graphManager = new GraphManager();
        graphManager.setGraph(PresetCreator.createStar(6,1));
        Graph graph = graphManager.getGraph();
        int noOfEdges = graph.getEdges().size();
        int noOfNodes = graph.getNodes().size();

        graphManager.createComplementGraph();

        assertEquals(graph.getEdges().size(), noOfNodes * (noOfNodes-1)/2 - noOfEdges);
    }

    @Test
    void completeTest(){
        GraphManager graphManager = new GraphManager();
        graphManager.setGraph(PresetCreator.createStar(6,1));
        Graph graph = graphManager.getGraph();
        int noOfNodes = graph.getNodes().size();

        graphManager.createCompleteGraph();

        assertEquals(graph.getEdges().size(), noOfNodes * (noOfNodes-1)/2);
    }

    @Test
    void subdivisionTest(){
        GraphManager graphManager = new GraphManager();
        graphManager.setGraph(PresetCreator.createCycle(5,1));
        Graph graph = graphManager.getGraph();
        assertEquals(graph.getNodes().size(), 5);
        assertEquals(graph.getEdges().size(), 5);

        graphManager.subdivideEdges();

        assertEquals(graph.getNodes().size(), 10);
        assertEquals(graph.getEdges().size(), 10);
    }

    @Test
    void pathTest(){
        GraphManager graphManager = new GraphManager();
        Graph graph = new Graph();
        for (int i = 0; i < 10; i++) {
            graph.addNode();
        }
        graphManager.setGraph(graph);
        graphManager.createPath();

        assertEquals(graph.getEdges().size(), 9);

        for (int i = 0; i < 9; i++) {
            assertTrue(graph.getEdges().isEdgeBetween(graph.getNodes().get(i), graph.getNodes().get(i+1)));
        }
    }

    @Test
    void deleteEdgesTest(){
        GraphManager graphManager = new GraphManager();
        graphManager.setGraph(PresetCreator.createKingsGraph(5,5, new Point2D(0,0)));
        Graph graph = graphManager.getGraph();
        graphManager.deleteEdges();

        assertTrue(graph.getEdges().isEmpty());
    }


}