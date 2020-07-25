package com.todense.viewmodel.preset;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;


public class PresetCreator {


    public static Graph createCycle(int n, double radius, Point2D center) {

        Graph g = new Graph("CycleGraph", false);

        double dx;
        double dy;

        //nodes
        for (int k = 0; k < n; k++) {
            dx = center.getX() + radius * Math.cos(-Math.PI/2 + (k * 2 * Math.PI) / n);
            dy = center.getY() + radius * Math.sin(-Math.PI/2 + (k * 2 * Math.PI) / n);
            g.addNode(new Point2D(dx, dy));
        }

        //edges
        for (int i = 0; i < g.getNodes().size(); i++) {
            Node n1 = g.getNodes().get(i);
            Node n2 = g.getNodes().get((i+1) % g.getNodes().size());
            g.addEdge(n1, n2);
        }

        return g;
    }



    public static Graph createGrid(int columns, int rows, Point2D center){
        Graph g = new Graph("GridGraph", false);

        if(columns == 1 && rows == 1){
            g.addNode(center);
            return g;
        }

        Node[][] nodeMatrix = new Node[columns][rows];

        double width = center.getX() * 1.7;
        double height = center.getY() * 1.7;

        double gap = Math.min(width/(columns-1), height/(rows-1));

        Point2D startPt = center.subtract(new Point2D((columns-1)*gap/2, (rows-1)*gap/2));

        for(int i = 0; i < columns; i++){
            for(int j = 0; j < rows; j++){
                nodeMatrix[i][j] = g.addNode(startPt.add(new Point2D(gap*i, gap*j)));
            }
        }

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows-1; j++) {
                g.addEdge(nodeMatrix[i][j], nodeMatrix[i][j+1]);
            }
        }

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < columns-1; i++) {
                g.addEdge(nodeMatrix[i][j], nodeMatrix[i+1][j]);
            }
        }

        return g;
    }


    public static Graph createMaze(int columns, int rows, Point2D center){
        Graph g = createGrid(columns, rows, center);
        g.getNodes().forEach(n -> Collections.shuffle(n.getNeighbours()));
        DFS(g.getNodes().get(new Random().nextInt(g.getNodes().size())), g);
        g.getEdges().stream().filter(e -> !e.isMarked()).forEach(g::removeEdge);
        return g;
    }

    public static Graph createStar(int n, double radius, Point2D center){
        Graph g = new Graph("StarGraph", false);

        double dx;
        double dy;

        //nodes
        Node centerNode = g.addNode(center);

        for (int k = 0; k < n-1; k++) {
            double angle = -Math.PI / 2 + (k * 2 * Math.PI) / (n - 1);
            dx = center.getX() + radius * Math.cos(angle);
            dy = center.getY() + radius * Math.sin(angle);
            Node newNode = g.addNode(new Point2D(dx, dy));
            g.addEdge(centerNode, newNode);
        }

        return g;
    }


    public static Graph createCompleteBipartite(int n, int m, Point2D center){
        Graph g = new Graph("CompleteBipartite", false);
        double gap;

        double h = center.getY() * 0.8;

        if(n == 1){
            g.addNode(new Node(new Point2D(center.getX() - 300, center.getY()), g));
        }
        else {
            gap = 2*h/(n-1);
            for (int i = 0; i < n; i++) {
                Point2D newPos = new Point2D(
                        center.getX() - 300,
                        (0.2 * center.getY()) + i * gap);
                g.addNode(new Node(newPos, g));
            }
        }
        if(m == 1){
            g.addNode(new Node(new Point2D(center.getX() + 300, center.getY()), g));
        }
        else {
            gap = 2 * h / (m - 1);
            for (int i = 0; i < m; i++) {
                Point2D newPos = new Point2D(
                        center.getX() + 300,
                        (0.2 * center.getY()) + i*gap);
                g.addNode(new Node(newPos, g));
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = n; j < n + m; j++) {
                g.addEdge(new Edge(g.getNodes().get(i), g.getNodes().get(j)));
            }
        }
        return g;
    }

    private static void DFS(Node n, Graph graph){

        Stack<Node> stack = new Stack<>();
        HashMap<Node, Node> prev = new HashMap<>();

        graph.getNodes().forEach((node) -> prev.put(node, null));
        stack.push(n);

        while(!stack.isEmpty()){
            Node m = stack.pop();

            if(m.isVisited())
                continue;

            m.setVisited(true);

            if(prev.get(m) != null){
                graph.getEdge(m, prev.get(m)).setMarked(true);
            }

            for (Node k : m.getNeighbours()) {
                if(!k.isVisited()){
                    stack.push(k);
                    prev.put(k, m);
                }
            }
        }
    }

}
