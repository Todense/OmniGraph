package com.todense.viewmodel.graph;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;

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
        int sum = 0;
        for(Node n : graph.getNodes()){
            if(Thread.currentThread().isInterrupted()){
                return -2;
            }
            sum += n.getNeighbours().size();
        }
        return  (double) sum/graph.getNodes().size();
    }

}
