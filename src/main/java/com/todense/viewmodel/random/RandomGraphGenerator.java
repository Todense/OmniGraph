package com.todense.viewmodel.random;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;


public class RandomGraphGenerator {

    public static Graph generateGraph(int nodeCount, Generator<Point2D> pointGenerator, EdgeGenerator edgeGenerator, double minDist){
        Graph graph = new Graph("RandomGraph");
        generateNodes(nodeCount, graph, pointGenerator, minDist);
        edgeGenerator.setNodes(graph.getNodes());
        generateEdges(graph, edgeGenerator);
        return graph;
    }

    public static void generateNodes(int n, Graph g, Generator<Point2D> pointGenerator, double minDist) {
        if(minDist == 0){
            for(int i = 0; i < n; i++) {
                g.addNode(pointGenerator.next());
            }
        }
        else{
            generateNodesMinDist(n, minDist, g, pointGenerator);
        }
    }

    public static void generateNodesMinDist(int n, double minDist, Graph g, Generator<Point2D> pointGenerator) {
        int nodeCount = 0;
        int failCount = 0;

        while(nodeCount < n){
            Point2D newPoint = pointGenerator.next();
            boolean allowed = true;

            for(Node node : g.getNodes()){
                if(node.getPos().distance(newPoint) < minDist){
                    allowed = false;
                    break;
                }
            }

            if(allowed){
                g.addNode(newPoint);
                nodeCount++;
                failCount = 0;
            }
            else{
                failCount++;
            }

            if(failCount > 1000){
                throw new RuntimeException("Minimum node distance is too large");
            }
        }
    }

    public static void generateEdges(Graph graph, EdgeGenerator generator){
        var connections = generator.generateConnections();
        var beginnings = connections.getKey();
        var ends = connections.getValue();
        while(!beginnings.isEmpty())
            graph.addEdge(graph.getNodes().get(beginnings.pop()), graph.getNodes().get(ends.pop()));
    }
}
