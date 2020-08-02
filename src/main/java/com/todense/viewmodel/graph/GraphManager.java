package com.todense.viewmodel.graph;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphManager {

    private ObjectProperty<Color> defaultNodeColorProperty = new SimpleObjectProperty<>(Color.RED);
    private ObjectProperty<Color> defaultEdgeColorProperty = new SimpleObjectProperty<>();

    private ObjectProperty<Node> startNodeProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Node> goalNodeProperty = new SimpleObjectProperty<>();

    int nodeIndex = 0;

    private Graph graph;

    public GraphManager(){
        setGraph(new Graph("graph0"));
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Node addNode(Point2D p, Graph g) {
        Node n = new Node(p, g);
        n.setColor(defaultNodeColorProperty.get());
        nodeIndex++;
        g.addNode(n);
        return n;
    }

    public Node addNode(Point2D p){
        return addNode(p , this.graph);
    }

    public void removeNode(Node n) {
        Graph g = getGraph();
        for (int i = n.getIndex() + 1; i < g.getNodes().size(); i++) {
            g.getNodes().get(i).setIndex( g.getNodes().get(i).getIndex() - 1) ;
        }
        if(startNodeProperty.get() == n){
            startNodeProperty.set(null);
        }
        if(goalNodeProperty.get() == n){
            goalNodeProperty.set(null);
        }
        g.removeNode(n);
    }

    public void addEdge(Node n1, Node n2) {
        Edge edge = new Edge(n1, n2);
        edge.setColor(defaultEdgeColorProperty.get());
        graph.addEdge(edge);
    }

    public void removeEdge(Node n1, Node n2) {
        graph.removeEdge(n1, n2);
    }

    public void removeEdge(Edge edge){
        graph.removeEdge(edge.getN1(), edge.getN2());
    }

    public void applyColors(){
        for (Node node : graph.getNodes()) {
            node.setColor(defaultNodeColorProperty.get());
        }
        for (Edge edge : graph.getEdges()) {
            edge.setColor(defaultEdgeColorProperty.get());
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void clearGraph() {
        graph = new Graph("graph0");
        nodeIndex = 0;
        startNodeProperty.set(null);
        goalNodeProperty.set(null);
    }

    public void resetGraph() {
        for (Node n : graph.getNodes()) {
            n.setMarked(false);
            n.setSelected(false);
            n.setVisited(false);
        }
        for (Edge e : graph.getEdges()) {
            e.setMarked(false);
            e.setVisible(true);
        }
    }

    public void createPath(){
        for(int i = 0; i < graph.getNodes().size()-1; i++) {
            Node n1 = graph.getNodes().get(i);
            Node n2 = graph.getNodes().get(i+1);
            if(noEdgeBetween(n1,n2)) {
                addEdge(n1, n2);
            }
        }
    }

    public void createCompleteGraph(){
        for(int i = 0; i< graph.getNodes().size()-1; i++) {
            for(int j = i; j< graph.getNodes().size(); j++) {
                Node n1 = graph.getNodes().get(i);
                Node n2 = graph.getNodes().get(j);
                if(noEdgeBetween(n1,n2)) {
                    addEdge(n1, n2);
                }
            }
        }
    }

    public void createComplementGraph() {
        for(int i = 0; i < graph.getNodes().size(); i++){
            Node n = graph.getNodes().get(i);
            for (int j = i+1; j < graph.getNodes().size(); j++) {
                Node m = graph.getNodes().get(j);
                if(noEdgeBetween(n, m)){
                    addEdge(n, m);
                }
                else{
                    removeEdge(n, m);
                }
            }
        }
    }

    public void subdivideEdge(Edge e){
        graph.removeEdge(e);
        Node n = e.getN1();
        Node m = e.getN2();
        Point2D midpoint = n.getPos().midpoint(m.getPos());
        Node k = addNode(midpoint);
        addEdge(n, k);
        addEdge(m, k);
    }

    public void subdivideEdges() {
        List<Edge> edgesCopy = new ArrayList<>(graph.getEdges());
        graph.removeAllEdges();

        for (Edge edge : edgesCopy) {
            Node n = edge.getN1();
            Node m = edge.getN2();
            Point2D midpoint = n.getPos().midpoint(m.getPos());
            Node k = addNode(midpoint);
            addEdge(n, k);
            addEdge(m, k);
        }
    }

    public void contractEdge(Edge e) {
        for (Node m : e.getN1().getNeighbours()) {
            if(noEdgeBetween(m, e.getN2())){
                addEdge(m, e.getN2());
            }
        }
        removeEdge(e);
        removeNode(e.getN1());
    }

    public void deleteEdges() {
        graph.removeAllEdges();
    }

    public boolean noEdgeBetween(Node n1, Node n2) {
        return !n1.getNeighbours().contains(n2) && n1 != n2;
    }

    public void updateNodePosition(Node n, Point2D d){
        n.setPos(n.getPos().add(d));
    }

    public LinkedList<Node> getSelectedNodes(){
        LinkedList<Node> selectedNodes = new LinkedList<>();
        for(Node n : graph.getNodes()) {
            if (n.isSelected()) {
                selectedNodes.add(n);
            }
        }
        return  selectedNodes;
    }

    public ObjectProperty<Color> nodeColorProperty() {
        return defaultNodeColorProperty;
    }

    public ObjectProperty<Color> edgeColorProperty() {
        return defaultEdgeColorProperty;
    }

    public ObjectProperty<Node> startNodeProperty() {
        return startNodeProperty;
    }

    public ObjectProperty<Node> goalNodeProperty() {
        return goalNodeProperty;
    }



}
