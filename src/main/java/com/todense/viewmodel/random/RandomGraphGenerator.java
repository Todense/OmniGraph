package com.todense.viewmodel.random;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

public class RandomGraphGenerator {

    public static boolean generateNodes(int n, Graph g, Generator<Point2D> pointGenerator, double minDist){
        if(minDist == 0){
            for(int i = 0; i < n; i++) {
                g.addNode(pointGenerator.next());
            }
            return true;
        }
        else{
            return generateNodesMinDist(n, minDist, g, pointGenerator);
        }
    }

    public static boolean generateNodesMinDist(int n, double minDist, Graph g, Generator<Point2D> pointGenerator){
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
                return false;
            }
        }
        return  true;
    }

    public static void generateEdges(Graph graph, EdgeGenerator generator){
        var adjacencyMatrix = generator.generateAdjacencyMatrix();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j = i + 1; j < graph.getNodes().size(); j++) {
                if (adjacencyMatrix[i][j]){
                    graph.addEdge(new Edge(graph.getNodes().get(i), graph.getNodes().get(j)));
                }
            }
        }
    }
}
