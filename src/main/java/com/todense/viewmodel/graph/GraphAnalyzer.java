package com.todense.viewmodel.graph;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.LinkedList;
import java.util.Stack;

public class GraphAnalyzer {

    public static int getComponentCount(Graph g){
        int count = 0;
        boolean[] visited = new boolean[g.getNodes().size()];
        for(Node n: g.getNodes()) {
            if(Thread.currentThread().isInterrupted()){
                return -2;
            }
            if (!visited[n.getIndex()]) {
                count++;
                ComponentDFS(n,  visited);
            }
        }

        return count;
    }

    private static void ComponentDFS(Node n, boolean[] visited){
        Stack<Node> stack = new Stack<>();
        stack.push(n);

        while(!stack.isEmpty()){
            Node m = stack.pop();
            if(visited[m.getIndex()])
                continue;
            visited[m.getIndex()] = true;
            for (Node k : m.getNeighbours()) {
                if(!visited[k.getIndex()]){
                    stack.push(k);
                }
            }
        }
    }

    public static int[] calculateEccentricities(Graph graph){
        if(graph.getNodes().size() == 0) return new int[]{0,0};
        int maxDist;
        int[] dist;
        boolean[] visited;
        int[] eccentricities = new int[graph.getNodes().size()];
        for(Node n : graph.getNodes()){
            if(Thread.currentThread().isInterrupted()){
                return new int[]{-2, -2};
            }
            dist = new int[graph.getNodes().size()];
            visited = new boolean[graph.getNodes().size()];
            LinkedList<Node> queue = new LinkedList<>();
            queue.add(n);
            dist[n.getIndex()] = 0;
            visited[n.getIndex()]= true;
            while(!queue.isEmpty()) {
                Node m = queue.poll();
                for(Node k: m.getNeighbours()) {
                    if(!visited[k.getIndex()]) {
                        visited[k.getIndex()] = true;
                        dist[k.getIndex()] = dist[m.getIndex()]+1;
                        queue.add(k);
                    }
                }
            }

            if(n.getIndex() == 0) { //check if graph is connected
                for (int i = 0; i < graph.getNodes().size(); i++) {
                    if (!visited[i]) {
                        return new int[]{-1, -1};
                    }
                }
            }

            maxDist = 0;
            for (int i = 0; i < graph.getNodes().size(); i++) {
                if(dist[i] > maxDist){
                    maxDist = dist[i];
                }
            }
            eccentricities[n.getIndex()] = maxDist;
        }

        int radius = Integer.MAX_VALUE;
        int diameter = 0;

        for (int e : eccentricities) {
            if (e < radius) radius = e;
            if (e > diameter) diameter = e;
        }

        return new int[]{radius, diameter};
    }

    public static int calculateMinDegree(Graph graph){
        if(graph.getNodes().size() == 0) return 0;
        int min = Integer.MAX_VALUE;
        for(Node n : graph.getNodes()){
            if(Thread.currentThread().isInterrupted()){
                return -2;
            }
            if(n.getNeighbours().size() < min){
                min = n.getNeighbours().size();
            }
        }

        return min;
    }

    public static int calculateMaxDegree(Graph graph){
        if(graph.getNodes().size() == 0) return 0;
        int max = 0;
        for(Node n : graph.getNodes()){
            if(Thread.currentThread().isInterrupted()){
                return -2;
            }
            if(n.getNeighbours().size() > max){
                max = n.getNeighbours().size();
            }
        }
        return max;
    }

    public static double calculateAvgDegree(Graph graph){
        if(graph.getNodes().size() == 0) return 0;
        int sum = 0;
        for(Node n : graph.getNodes()){
            if(Thread.currentThread().isInterrupted()){
                return -2;
            }
            sum += n.getNeighbours().size();
        }
        return  (double) sum/graph.getNodes().size();
    }

    public static double calculateClusteringCoefficient(Graph graph){
        double sumOfCoefficients = 0;
        for(Node node : graph.getNodes()){
            int degree = node.getDegree();
            if(degree < 2)
                continue;
            int connectedNeighboursCounter = 0;
            for (int i = 0; i < degree; i++) {
                Node neighbour1 = node.getNeighbours().get(i);
                for (int j = i+1; j < degree; j++) {
                    Node neighbour2 = node.getNeighbours().get(j);
                    if(graph.getEdges().isEdgeBetween(neighbour1, neighbour2)){
                        connectedNeighboursCounter++;
                    }
                }
            }
            sumOfCoefficients += (double) 2 * connectedNeighboursCounter / (degree * (degree - 1));
        }
        return sumOfCoefficients / graph.getNodes().size();
    }

    public static double getDensity(Graph graph) {
        if(graph.getNodes().size() == 0) return 0;
        int nodeCount = graph.getNodes().size();
        return  2 * (double)graph.getEdges().size()/((nodeCount-1) * nodeCount);
    }

    public static double calculateAvgEdgeLength(Graph graph){
        double sum = 0;
        for(Edge edge : graph.getEdges()){
            sum += edge.calcLength();
        }
        return sum / graph.getEdges().size();
    }

    public static double calculateAvgNodeDist(Graph graph){
        int order = graph.getOrder();
        double sum = 0;
        for (int i = 0; i < order; i++) {
            Node n = graph.getNodes().get(i);
            for (int j = i+1; j < order; j++) {
                Node m = graph.getNodes().get(j);
                sum += n.getPos().distance(m.getPos());
            }
        }
        return sum / (double)(order * (order-1)/4);
    }

    public static double getNearestNeighbourSpanningTreeLength(Graph graph) {
        return getNearestNeighbourSpanningTreeLength(graph, graph.getOrder()-1);
    }

    public static double getNearestNeighbourSpanningTreeLength(Graph graph, int limit){
        double length = 0;
        Node currentNode = graph.getNodes().get(0);
        currentNode.setVisited(true);
        int iterCount = limit < graph.getOrder()? limit : graph.getOrder()-1;
        for (int i = 0; i < iterCount; i++) {
            double minDist = Double.POSITIVE_INFINITY;
            Node bestNode = null;
            for(Node node : graph.getNodes()){
                if(node.isVisited())
                    continue;
                double dist = node.getPos().distance(currentNode.getPos());
                if(dist < minDist){
                    minDist = dist;
                    bestNode = node;
                }
            }
            assert bestNode != null;
            bestNode.setVisited(true);
            length += minDist;
            currentNode = bestNode;
        }
        for (Node node : graph.getNodes()){
            node.setVisited(false);
        }
        return length;
    }

    public static Point2D getGraphCenter(Graph graph){
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double x = node.getPos().getX();
            if(x < xMin) xMin = x;
            if(x > xMax) xMax = x;

            double y = node.getPos().getY();
            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }

        return new Point2D((xMax+xMin)/2, (yMax+yMin)/2);
    }

    public static Rectangle2D getGraphBounds(Graph graph){
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double x = node.getPos().getX();
            if(x < xMin) xMin = x;
            if(x > xMax) xMax = x;

            double y = node.getPos().getY();
            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }
        return new Rectangle2D(xMin, yMin, xMax-xMin, yMax-yMin);
    }
}
