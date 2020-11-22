package com.todense.viewmodel.graph;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphManager {

    private Graph graph;

    private Graph clipboardGraph;

    public GraphManager(){
        setGraph(new Graph());
    }

    public void clearGraph() {
        setGraph(new Graph());
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
        selectedNodes = new ArrayList<>();
    }

    public void createPath(){
        for(int i = 0; i < graph.getNodes().size()-1; i++) {
            Node n1 = graph.getNodes().get(i);
            Node n2 = graph.getNodes().get(i+1);
            if(!isEdgeBetween(n1,n2)) {
                graph.addEdge(n1, n2);
            }
        }
    }

    public void createCompleteGraph(){
        for(int i = 0; i < graph.getNodes().size()-1; i++) {
            for(int j = i + 1; j < graph.getNodes().size(); j++) {
                Node n1 = graph.getNodes().get(i);
                Node n2 = graph.getNodes().get(j);
                if(!isEdgeBetween(n1,n2)) {
                    graph.addEdge(n1, n2);
                }
            }
        }
    }

    public void createComplementGraph() {
        for(int i = 0; i < graph.getNodes().size(); i++){
            Node n = graph.getNodes().get(i);
            for (int j = i+1; j < graph.getNodes().size(); j++) {
                Node m = graph.getNodes().get(j);
                if(!isEdgeBetween(n, m)){
                    graph.addEdge(n, m);
                }
                else{
                    graph.removeEdge(n, m);
                }
            }
        }
    }

    public void subdivideEdge(Edge e){
        graph.removeEdge(e);
        Node n = e.getN1();
        Node m = e.getN2();
        Point2D midpoint = n.getPos().midpoint(m.getPos());
        Node k = graph.addNode(midpoint);
        graph.addEdge(n, k);
        graph.addEdge(m, k);
    }

    public void subdivideEdges() {
        List<Edge> edgesCopy = new ArrayList<>(graph.getEdges());
        graph.removeAllEdges();

        for (Edge edge : edgesCopy) {
            Node n = edge.getN1();
            Node m = edge.getN2();
            Point2D midpoint = n.getPos().midpoint(m.getPos());
            Node k = graph.addNode(midpoint);
            graph.addEdge(n, k);
            graph.addEdge(m, k);
        }
    }

    public void contractEdge(Edge e) {
        for (Node m : e.getN1().getNeighbours()) {
            if(!isEdgeBetween(m, e.getN2()) && !m.equals(e.getN2())){
                graph.addEdge(m, e.getN2());
            }
        }
        graph.removeEdge(e);
        graph.removeNode(e.getN1());
    }

    public void addSubgraph(Point2D center){
        if(clipboardGraph == null)
            return;
        Point2D subgraphCenter = GraphAnalyzer.getGraphCenter(clipboardGraph);
        Point2D change = center.subtract(subgraphCenter);
        HashMap<Node, Node> isomorphismMap = new HashMap<>();
        for(Node node : clipboardGraph.getNodes()){
            Node newNode = graph.addNode(node.getPos().add(change), node.getColor());
            isomorphismMap.put(node, newNode);
        }

        for(Edge edge : clipboardGraph.getEdges()){
            graph.addEdge(isomorphismMap.get(edge.getN1()), isomorphismMap.get(edge.getN2()), edge.getColor());
        }
    }

    public Graph getSubgraphFromSelectedNodes(){
        Graph subGraph = new Graph();
        for (Node n : selectedNodes) {
            subGraph.addNode(n.getPos(), n.getColor());
        }
        for (int i = 0; i < subGraph.getNodes().size(); i++) {
            for (int j = i+1; j < subGraph.getNodes().size(); j++) {
                if(graph.getEdges().isEdgeBetween(selectedNodes.get(i), selectedNodes.get(j))){
                    subGraph.addEdge(subGraph.getNodes().get(i), subGraph.getNodes().get(j));
                }
            }
        }
        return subGraph;
    }

    public void copySelectedSubgraph(){
        clipboardGraph = getSubgraphFromSelectedNodes();
    }

    public void deleteEdges() {
        graph.removeAllEdges();
    }

    public boolean isEdgeBetween(Node n1, Node n2) {
        return graph.getEdges().isEdgeBetween(n1, n2);
    }

    public void updateNodePosition(Node n, Point2D d){
        n.setPos(n.getPos().add(d));
    }

    private List<Node> selectedNodes = new ArrayList<>();

    public List<Node> getSelectedNodes() {
        return selectedNodes;
    }

    public void createSelectedNodesList(){
        this.selectedNodes = new ArrayList<>();
        for(Node n : graph.getNodes()) {
            if (n.isSelected()) {
                selectedNodes.add(n);
            }
        }
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.selectedNodes = new ArrayList<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setClipboardGraph(Graph clipboardGraph) {
        this.clipboardGraph = clipboardGraph;
    }
}
