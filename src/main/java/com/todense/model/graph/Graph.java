package com.todense.model.graph;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graph {

    boolean directed;

    int idCounter = 0;

    Color color;

    CopyOnWriteArrayList<Node> nodes = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Edge> edges = new CopyOnWriteArrayList<>();

    HashMap<Integer, Node> nodeMap = new HashMap<>();
    HashMap<String, Edge> edgeMap = new HashMap<>();

    public String name;

    public Graph(String name, boolean directed){
        this.name=name;
        this.directed = directed;
        this.color = Color.rgb(150,50,50);
    }

    public Node addNode(Node n){
        nodes.add(n);
        nodeMap.put(n.getIndex(), n);
        return n;
    }

    public Node addNode(Point2D pt){
        return addNode(new Node(pt, this));
    }


    public void addEdge(Edge edge){
        edges.add(edge);

        edge.getN1().getNeighbours().add(edge.getN2());
        edge.getN2().getNeighbours().add(edge.getN1());

        edgeMap.put(edge.getN1().getId()+"-"+edge.getN2().getId(), edge);
        edgeMap.put(edge.getN2().getId()+"-"+edge.getN1().getId(), edge);
    }

    public void addEdge(Node n, Node m){
         addEdge(new Edge(n, m));
    }

    public void removeEdge(Node n1, Node n2){
        edges.remove(edgeMap.get(n1.getId()+"-"+n2.getId()));
        edgeMap.remove(n1.getId()+"-"+n2.getId());
        edgeMap.remove(n2.getId()+"-"+n1.getId());
        n1.getNeighbours().remove(n2);
        n2.getNeighbours().remove(n1);
    }

    public void removeEdge(Edge e){
        removeEdge(e.getN1(), e.getN2());
    }

    public void removeNode(Node n){
        nodes.remove(n);
        nodeMap.remove(n.getIndex());
        for(Node m: n.getNeighbours()) {
            edges.remove(getEdge(n, m));
            m.getNeighbours().remove(n);
        }
    }

    public Edge getEdge(Node n, Node m){
        return edgeMap.get(n.getId()+"-"+m.getId());
    }

    public Edge getEdge(int i, int j){
        return getEdge(nodes.get(i), nodes.get(j));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CopyOnWriteArrayList<Node> getNodes() {
        return nodes;
    }
    public CopyOnWriteArrayList<Edge> getEdges() {
        return edges;
    }
    
}
