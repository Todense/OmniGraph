package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.task.*;
import com.todense.viewmodel.random.EdgeGenerator;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.RandomGraphGenerator;
import com.todense.viewmodel.random.arrangement.generators.RandomCirclePointGenerator;
import com.todense.viewmodel.random.generators.ErdosRenyiGenerator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmTaskTest {

    @RepeatedTest(100)
    void hamiltonianCycleTest() {
        int nodeCount = 20;
        Generator<Point2D> point2DGenerator = new RandomCirclePointGenerator(1, new Point2D(0,0));
        EdgeGenerator edgeGenerator = new ErdosRenyiGenerator( 0.1);
        Graph graph = RandomGraphGenerator.generateGraph(nodeCount, point2DGenerator, edgeGenerator, 0);
        for (int i = 0; i < nodeCount; i++) {
            Node n = graph.getNodes().get(i);
            Node m = graph.getNodes().get((i+1)%nodeCount);
            if(!graph.getEdges().isEdgeBetween(n, m)){
                graph.addEdge(n, m);
            }
        }
        HCSearchTask hcService = new HCSearchTask(graph.getNodes().get(0),graph, true);
        assertDoesNotThrow(hcService::perform, () -> "HC Service should not throw exceptions");
        assertTrue(hcService.isCycleFound(), () -> "Hamiltonian Cycle should be found");
    }

    @RepeatedTest(100)
    void basicAlgorithmsTest() {
        Generator<Point2D> point2DGenerator = new RandomCirclePointGenerator(1, new Point2D(0,0));
        EdgeGenerator edgeGenerator = new ErdosRenyiGenerator( 0.01);
        Graph graph = RandomGraphGenerator.generateGraph(300, point2DGenerator, edgeGenerator, 0);
        Node startNode = graph.getNodes().get(0);
        Node endNode = graph.getNodes().get(graph.getNodes().size()-1);

        DFSTask dfsService = new DFSTask(startNode, graph);
        BFSTask bfsService = new BFSTask(startNode, graph);
        AStarTask aStarService = new AStarTask(startNode, endNode, graph, false);
        DijkstraTask dijkstraService = new DijkstraTask(startNode, endNode, graph, false);
        PrimTask primService = new PrimTask(graph.getNodes().get(0), graph, false);
        KruskalTask kruskalService = new KruskalTask(graph, false);

        assertDoesNotThrow(dfsService::perform, () -> "DFS should not throw exceptions");
        assertDoesNotThrow(bfsService::perform, () -> "BFS should not throw exceptions");
        assertDoesNotThrow(aStarService::perform, () -> "A* should not throw exceptions");
        assertDoesNotThrow(dijkstraService::perform, () -> "Dijkstra should not throw exceptions");
        assertDoesNotThrow(primService::perform, () -> "Prim should not throw exceptions");
        assertDoesNotThrow(kruskalService::perform, () -> "Kruskal should not throw exceptions");

        assertEquals(aStarService.result, dijkstraService.result, 0.000001,
                () -> "A* and Dijkstra results should be equal");

        assertEquals(primService.result, kruskalService.result, 0.000001,
                () -> "Prim and Kruskal results should be equal");
    }
}