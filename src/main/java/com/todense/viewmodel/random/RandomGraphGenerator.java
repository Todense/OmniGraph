package com.todense.viewmodel.random;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class RandomGraphGenerator {

    private static Random rnd = new Random();

    //NODES

    public static void generateNodesRandom(int n, double height, Point2D center, Graph g){
        Point2D newPos;
        for(int i = 0; i < n; i++) {
            newPos = new Point2D(rnd.nextDouble() * height, rnd.nextDouble() * height).add(center.subtract(height/2, height/2));
            g.addNode(new Node(newPos, g));
        }
    }

    public static void generateNodesCircle(int n, double radius, Point2D center, Graph g) {
        double dx;
        double dy;
        for (int k = 0; k < n; k++) {
            dx = center.getX() + radius * Math.cos((k * 2 * Math.PI) / n);
            dy = center.getY() + radius * Math.sin((k * 2 * Math.PI) / n);
            g.addNode(new Node(new Point2D(dx, dy), g));
        }
    }


    public static boolean generateNodesMinDist(int n, double minDist, double height, Point2D center, Graph g) {
        int nodeCount = 0;
        int failCount = 0;

        Point2D newPos;

        while(nodeCount < n){
            newPos = new Point2D(rnd.nextDouble() * height,
                    rnd.nextDouble() * height).add(center.subtract(height/2, height/2));
            boolean allowed = true;

            for(Node node : g.getNodes()){
                if(node.getPos().distance(newPos) < height * minDist){
                    allowed = false;
                }
            }

            if(allowed){
                g.addNode(new Node(newPos, g));
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


    //EDGES

    public static void generateGeometric(double radius, boolean random, double height, Graph g){
        for (int i = 0; i < g.getNodes().size(); i++) {
            Node n = g.getNodes().get(i);
            for (int j = i+1; j < g.getNodes().size(); j++) {
                Node m = g.getNodes().get(j);
                double dist = n.getPos().distance(m.getPos());
                if(dist < radius * height) {
                    if(random) {
                        double r =  Math.random() * radius * height;
                        if (r < dist) {
                            g.addEdge(new Edge(n,m));
                        }
                    }
                    else{
                        g.addEdge(new Edge(n, m));
                    }
                }
            }
        }
    }

    public static void generateErdosRenyi(double p, Graph g) {
        for (int i = 0; i < g.getNodes().size(); i++) {
            Node n = g.getNodes().get(i);
            for (int j = i+1; j < g.getNodes().size(); j++) {
                Node m = g.getNodes().get(j);
                if(rnd.nextDouble() < p){
                    g.addEdge(new Edge(n, m));
                }
            }
        }
    }

    public static void generateBarabasiAlbert(int m0, int m, Graph g){

        for (int i = 0; i < m0-1; i++) {
            g.addEdge(g.getNodes().get(i), g.getNodes().get(i+1));
        }

        for (int i = m0; i < g.getNodes().size(); i++) {
            Node current = g.getNodes().get(i);

            for (int j = 0; j < m; j++) {
                ArrayList<Pair<Node, Double>> probabilities = new ArrayList<>();
                for(Node k : g.getNodes()){
                    if(k.getIndex() < current.getIndex() && !current.getNeighbours().contains(k)) {
                        probabilities.add(new Pair<>(k, (double) k.getNeighbours().size()+1));
                    }
                }
                if(probabilities.size() > 0) {
                    EnumeratedDistribution<Node> distribution = new EnumeratedDistribution<>(probabilities);
                    Node k = distribution.sample();
                    g.addEdge(new Edge(current, k));
                }
            }
        }
    }
}
