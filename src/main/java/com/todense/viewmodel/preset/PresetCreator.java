package com.todense.viewmodel.preset;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

import java.util.*;


public class PresetCreator {


    public static Graph createCycle(int n, double size) {

        Graph g = new Graph("CycleGraph");

        double dx;
        double dy;

        //nodes
        for (int k = 0; k < n; k++) {
            dx = size/2 * Math.cos(-Math.PI/2 + (k * 2 * Math.PI) / n);
            dy = size/2 * Math.sin(-Math.PI/2 + (k * 2 * Math.PI) / n);
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

    public static Graph createGrid(int columns, int rows, Point2D size){
        Graph g = new Graph("GridGraph");

        if(columns == 1 && rows == 1){
            g.addNode(new Point2D(0,0));
            return g;
        }

        Node[][] nodeMatrix = new Node[columns][rows];

        double width = size.getX() * 0.85;
        double height = size.getY() * 0.85;

        double gap = Math.min(width/(columns-1), height/(rows-1));

        Point2D startPt = new Point2D(-(columns-1)*gap/2, -(rows-1)*gap/2);

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

    public static Graph createHexagonalGrid(int gridWidth, Point2D size){
        double pi = Math.PI;
        double sqrt3 = Math.sqrt(3);
        double width = size.getX() * 0.85;
        double height = size.getY() * 0.85;
        double gap = Math.min(width/((2*gridWidth-1)*sqrt3), height/((2*gridWidth-1)*sqrt3));
        Graph g = new Graph("HexagonalGridGraph");

        int level = 0;
        int levelSize = gridWidth;
        Point2D startPt = new Point2D(Math.cos(pi*4.0/3.0), Math.sin(pi*4.0/3.0)).multiply(sqrt3*gap*(gridWidth-1)).add(-sqrt3*gap/2, 0);
        for(int i=0; i<gridWidth; i++){
            g.addNode(startPt.add(i*sqrt3*gap, -gap/2).add(sqrt3*gap/2, -gap/2));
        }

        ArrayList<Node> prevLowerNodes = null;
        ArrayList<Node> currLowerNodes;
        while(level < 2*gridWidth-1){
            double angle;
            currLowerNodes = new ArrayList<>();
            angle = level < gridWidth -1 ? 2*pi/3: pi/3;
            levelSize = level < gridWidth ? levelSize+1: levelSize-1;

            for(int i = 0; i<levelSize; i++){
                Node lowerNode = g.addNode(startPt.add(i*sqrt3*gap, gap/2));
                Node  upperNode = g.addNode(startPt.add(i*sqrt3*gap, -gap/2));
                g.addEdge(lowerNode, upperNode);

                currLowerNodes.add(lowerNode);

                if(level > 0) {
                    if (level < gridWidth) {
                        if (i > 0) {
                            g.addEdge(upperNode, prevLowerNodes.get(i - 1));
                        }
                        if (i < levelSize - 1) {
                            g.addEdge(upperNode, prevLowerNodes.get(i));
                        }
                    } else {
                        g.addEdge(upperNode, prevLowerNodes.get(i));
                        g.addEdge(upperNode, prevLowerNodes.get(i + 1));
                    }
                }
            }
            prevLowerNodes = new ArrayList<>(currLowerNodes);
            level++;
            startPt = startPt.add(new Point2D(Math.cos(angle), Math.sin(angle)).multiply(sqrt3*gap));
        }

        startPt = startPt.subtract(new Point2D(Math.cos(pi/3), Math.sin(pi/3)).multiply(sqrt3*gap));

        for(int i=0; i<gridWidth; i++){
            g.addNode(startPt.add(i*sqrt3*gap, gap/2).add(sqrt3*gap/2, gap/2));
        }

        for(int i=0; i<gridWidth; i++){
            g.addEdge(g.getNodes().get(i), g.getNodes().get(2*i + gridWidth + 1));
            g.addEdge(g.getNodes().get(i), g.getNodes().get(2*i + gridWidth + 3));
        }

        for(int i=0; i<gridWidth; i++){
            int nodeIdx = g.getOrder()-gridWidth+i;
            g.addEdge(g.getNodes().get(nodeIdx), g.getNodes().get(nodeIdx+i-2*(gridWidth+1)));
            g.addEdge(g.getNodes().get(nodeIdx), g.getNodes().get(nodeIdx+i-2*(gridWidth)));
        }
        return g;
    }


    public static Graph createKingsGraph(int columns, int rows, Point2D center){
        Graph g = createGrid(columns, rows, center);
        for (Node node : g.getNodes()) {
            int i = node.getIndex();

            if( i % rows != 0){ //top
               if(i >= rows){ //left
                   if(!g.getEdges().isEdgeBetween(node, g.getNodes().get(i-rows-1))){
                       g.addEdge(node, g.getNodes().get(i-rows-1));
                   }
               }
               if(i < rows * (columns-1)){ //right
                   if(!g.getEdges().isEdgeBetween(node, g.getNodes().get(i+rows-1))){
                       g.addEdge(g.getNodes().get(i), g.getNodes().get(i+rows-1));
                   }
                }
            }

            if( i % rows != rows-1){ //bottom
                if(i >= rows){
                    if(!g.getEdges().isEdgeBetween(node, g.getNodes().get(i-rows+1))){
                        g.addEdge(node, g.getNodes().get(i-rows+1));
                    }
                }
                if(i < rows * (columns-1)){
                    if(!g.getEdges().isEdgeBetween(node, g.getNodes().get(i+rows+1))){
                        g.addEdge(node, g.getNodes().get(i+rows+1));
                    }

                }
            }
        }
        return g;
    }

    public static Graph createMaze(int columns, int rows, Point2D center){
        Graph g = createGrid(columns, rows, center);
        g.getNodes().forEach(n -> Collections.shuffle(n.getNeighbours()));
        DFS(g.getNodes().get(new Random().nextInt(g.getNodes().size())), g);

        Graph maze = new Graph();
        for(Node n: g.getNodes()){
            maze.addNode(n.getPos());
        }
        for(Edge e: g.getEdges()){
            if(e.isMarked()){
                Node n1 = maze.getNodes().get(e.getN1().getIndex());
                Node n2 = maze.getNodes().get(e.getN2().getIndex());
                maze.addEdge(n1, n2);
            }
        }
        return maze;
    }

    public static Graph createStar(int n, double radius){
        Graph g = new Graph("StarGraph");

        double dx;
        double dy;

        Node centerNode = g.addNode(new Point2D(0, 0));

        for (int k = 0; k < n-1; k++) {
            double angle = -Math.PI / 2 + (k * 2 * Math.PI) / (n - 1);
            dx = radius * Math.cos(angle);
            dy = radius * Math.sin(angle);
            Node newNode = g.addNode(new Point2D(dx, dy));
            g.addEdge(centerNode, newNode);
        }
        return g;
    }


    public static Graph createCompleteBipartite(int n, int m, Point2D size){
        Graph g = new Graph("CompleteBipartite");
        double gap;

        double h = size.getY() * 0.4;
        double w = size.getX() * 0.3;

        if(n == 1){
            g.addNode(new Point2D(-w, 0));
        }
        else {
            gap = 2*h/(n-1);
            for (int i = 0; i < n; i++) {
                Point2D newPos = new Point2D(-w, -h+i*gap);
                g.addNode(newPos);
            }
        }
        if(m == 1){
            g.addNode(new Point2D(w, 0));
        }
        else {
            gap = 2*h/(m-1);
            for (int i = 0; i < m; i++) {
                Point2D newPos = new Point2D(w, -h+i*gap);
                g.addNode(newPos);
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = n; j < n + m; j++) {
                g.addEdge(g.getNodes().get(i), g.getNodes().get(j));
            }
        }
        return g;
    }

    private static void DFS(Node n, Graph graph){

        Stack<Node> stack = new Stack<>();
        HashMap<Node, Node> prev = new HashMap<>();
        boolean[] visited = new boolean[graph.getOrder()];

        graph.getNodes().forEach((node) -> prev.put(node, null));
        stack.push(n);

        while(!stack.isEmpty()){
            Node m = stack.pop();

            if(visited[m.getIndex()])
                continue;
            visited[m.getIndex()] = true;

            if(prev.get(m) != null){
                graph.getEdge(m, prev.get(m)).setMarked(true);
            }

            for (Node k : m.getNeighbours()) {
                if(!visited[k.getIndex()]){
                    stack.push(k);
                    prev.put(k, m);
                }
            }
        }
    }

}
