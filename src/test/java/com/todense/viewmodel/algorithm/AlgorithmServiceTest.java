package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.service.*;
import com.todense.viewmodel.random.EdgeGenerator;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.RandomGraphGenerator;
import com.todense.viewmodel.random.arrangement.generators.RandomCirclePointGenerator;
import com.todense.viewmodel.random.generators.ErdosRenyiGenerator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AlgorithmServiceTest {

    @RepeatedTest(100)
    void basicAlgorithmsTest() {
        Generator<Point2D> point2DGenerator = new RandomCirclePointGenerator(1, new Point2D(0,0));
        EdgeGenerator edgeGenerator = new ErdosRenyiGenerator( 0.01);
        Graph graph = RandomGraphGenerator.generateGraph(300, point2DGenerator, edgeGenerator, 0);
        Node startNode = graph.getNodes().get(0);
        Node endNode = graph.getNodes().get(graph.getNodes().size()-1);

        DFSService dfsService = new DFSService(startNode, graph);
        BFSService bfsService = new BFSService(startNode, graph);
        AStarService aStarService = new AStarService(startNode, endNode, graph, false);
        DijkstraService dijkstraService = new DijkstraService(startNode, endNode, graph, false);
        PrimService primService = new PrimService(graph.getNodes().get(0), graph, false);
        KruskalService kruskalService = new KruskalService(graph, false);

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