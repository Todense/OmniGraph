package com.todense.model.graph;

import com.todense.model.EdgeList;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private int idCounter = 0;

    private List<Node> nodes = new ArrayList<>();
    private EdgeList edges = new EdgeList();

    public String name;

    public Graph(String name){
        this.name=name;
    }

    public Node addNode(Node n){
        nodes.add(n);
        return n;
    }

    public Node addNode(Point2D pt){
        return addNode(new Node(pt, this));
    }

    public void addEdge(Edge edge){
       edges.add(edge);
    }

    public void addEdge(Node n, Node m){
        edges.add(new Edge(n, m));
    }

    public void removeEdge(Node n1, Node n2){
        edges.remove(n1, n2);
    }

    public void removeEdge(Edge e){
        edges.remove(e);
    }

    public void removeAllEdges(){
        edges.clear();
        for (Node node : nodes) {
            node.getNeighbours().clear();
        }
    }

    public void removeNode(Node n){
        nodes.remove(n);
        new ArrayList<>(n.getNeighbours()).forEach(m -> removeEdge(n, m));
    }

    public Edge getEdge(Node n, Node m){
        return edges.getEdge(n, m);
    }

    public int nextID() {
        return idCounter++;
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

    public List<Node> getNodes() {
        return nodes;
    }

    public EdgeList getEdges() {
        return edges;
    }

}
