package com.todense.viewmodel.graph;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

import java.util.*;

public class GraphManager {

    private Graph graph;
    private Graph clipboardGraph;

    private boolean queueGraphOperationsOn = false;
    public final Queue<GraphOperation> operationQueue = new LinkedList<>();

    public GraphManager(){
        setGraph(new Graph());
    }

    public void performOperation(GraphOperation operation){
        if(queueGraphOperationsOn){
            operationQueue.add(operation);
        }
        else {
            operation.perform();
        }
    }

    public boolean performQueuedOperations(){
        if(operationQueue.isEmpty())
            return false;

        synchronized (Graph.LOCK) {
            while(!operationQueue.isEmpty()){
                var operation = operationQueue.poll();
                operation.perform();
            }
        }
        return true;
    }

    public void resetGraph() {
        synchronized (Graph.LOCK) {
            for (Node n : graph.getNodes()) {
                n.setSelected(false);
                n.setStatus(0);
            }
            for (Edge e : graph.getEdges()) {
                e.setMarked(false);
                e.setVisible(true);
            }
        }
        selectedNodes = new ArrayList<>();
    }

    public void createPath(List<Node> nodes){
        for (int i = 0; i < nodes.size() - 1; i++) {
            Node n1 = nodes.get(i);
            Node n2 = nodes.get(i + 1);
            if (!isEdgeBetween(n1, n2)) {
                graph.addEdge(n1, n2);
            }
        }
    }

    public void createPath(){
        this.createPath(this.graph.getNodes());
    }

    public void createCompleteGraph(List<Node> nodes){
        graph.applyToAllPairOfNodes(nodes, (n, m) -> {
            if (!isEdgeBetween(n, m))
                graph.addEdge(n, m);
        });
    }

    public void createCompleteGraph(){
        this.createCompleteGraph(this.graph.getNodes());
    }

    public void createComplementGraph(List<Node> nodes) {
        graph.applyToAllPairOfNodes(nodes, (n, m) -> {
            if (!isEdgeBetween(n, m))
                graph.addEdge(n, m);
            else
                graph.removeEdge(n, m);
        });
    }

    public void createComplementGraph() {
        this.createComplementGraph(this.graph.getNodes());
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

    public void subdivideEdges(List<Node> nodes) {
        ArrayList<Node> nodesCopy = new ArrayList<>(nodes);
        graph.applyToAllConnectedPairOfNodes(nodesCopy, (n, m) -> {
            Point2D midpoint = n.getPos().midpoint(m.getPos());
            Node k = graph.addNode(midpoint);
            graph.removeEdge(n, m);
            graph.addEdge(n, k);
            graph.addEdge(m, k);
        });
    }

    public void subdivideEdges() {
        this.subdivideEdges(this.graph.getNodes());
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

        for (Node node : clipboardGraph.getNodes()) {
            GraphOperation operation = () -> {
                Node newNode = graph.addNode(node.getPos().add(change), node.getColor());
                isomorphismMap.put(node, newNode);
            };
            performOperation(operation);
        }

        for (Edge edge : clipboardGraph.getEdges()) {
            performOperation(() -> graph.addEdge(
                        isomorphismMap.get(edge.getN1()),
                        isomorphismMap.get(edge.getN2()), edge.getColor()
                    )
            );
        }
    }

    public Graph getSelectedSubgraph(){
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
        clipboardGraph = getSelectedSubgraph();
    }

    public void deleteEdges(List<Node> nodes) {
        graph.removeEdges(nodes);
    }

    public void deleteEdges(){
        this.deleteEdges(this.graph.getNodes());
    }

    public boolean isEdgeBetween(Node n1, Node n2) {
        return graph.getEdges().isEdgeBetween(n1, n2);
    }

    public void rotateNode(Node n, Point2D pivot, double angle){
        Point2D u = n.getPos().subtract(pivot);
        double x = u.getX() * Math.cos(angle) - u.getY() * Math.sin(angle);
        double y = u.getX() * Math.sin(angle) + u.getY() * Math.cos(angle);
        graph.setNodePosition(n, new Point2D(x, y).add(pivot));
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

    public GraphOperation edgeDeletionOperation(Edge e){
        return () -> graph.removeEdge(e);
    }

    public GraphOperation edgeDeletionOperation(Node n, Node m){
        return () -> graph.removeEdge(n, m);
    }

    public GraphOperation edgeAdditionOperation(Node n, Node m){
        return () -> graph.addEdge(n, m);
    }

    public GraphOperation nodeAdditionOperation(Point2D p){
        return () -> graph.addNode(p);
    }

    public GraphOperation nodeDeletionOperation(Node n){
        return () -> graph.removeNode(n);
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.selectedNodes = new ArrayList<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setQueueGraphOperationsOn(boolean queueGraphOperationsOn) {
        this.queueGraphOperationsOn = queueGraphOperationsOn;
    }
}
