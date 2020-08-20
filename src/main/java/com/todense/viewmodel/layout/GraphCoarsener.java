package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GraphCoarsener {

    private GraphManager graphManager;
    private Stack<Graph> graphSequence = new Stack<>();
    private Stack<HashMap<Node, Node>> nodeMaps = new Stack<>();
    private HashMap<Node, Integer> nodeWeights = new HashMap<>();

    private double reductionRate = 0;

    public GraphCoarsener(GraphManager graphManager){
        this.graphManager = graphManager;
    }

    public void reconstruct(double variation) {
        graphSequence.pop();
        Graph previousGraph = graphSequence.peek();
        var mapping = nodeMaps.pop();
        previousGraph.getNodes().forEach(node ->{
                double angle = Math.random() * 2 * Math.PI;
                node.setPos(mapping.get(node).getPos().add(new Point2D(
                                Math.cos(angle),
                                Math.sin(angle))
                        .multiply(variation))
                );
    });
        graphManager.setGraph(previousGraph);
    }

    public void initGraphSequence() {
        graphSequence.add(graphManager.getGraph());
        graphSequence.peek().getNodes().forEach(node -> nodeWeights.put(node, 1));
    }

    public void coarsen(){
        Graph graph = graphSequence.peek();
        Graph copyGraph = graph.copy();
        HashMap<Node, Node> nodeMap = new HashMap<>();
        HashMap<Node, Node> isomorphismMap = new HashMap<>();
        copyGraph.getNodes().forEach(node -> isomorphismMap.put(node, graph.getNodes().get(node.getIndex())));
        graph.getNodes().forEach(node -> nodeMap.put(node, copyGraph.getNodes().get(node.getIndex())));
        HashMap<Node, Integer> newNodeWeights = new HashMap<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            newNodeWeights.put(copyGraph.getNodes().get(i), nodeWeights.get(graph.getNodes().get(i)));
        }
        nodeWeights = newNodeWeights;
        List<Edge> matching = new ArrayList<>();
        for(Node n : copyGraph.getNodes()){
            if(n.isVisited()) continue;
            Node bestNode = null;
            int minWeight = Integer.MAX_VALUE;
            for(Node m : n.getNeighbours()){
                if(m.isVisited()) continue;
                if(nodeWeights.get(m) < minWeight){
                    minWeight = nodeWeights.get(m);
                    bestNode = m;
                }
            }
            if(bestNode != null){
                n.setVisited(true);
                bestNode.setVisited(true);
                matching.add(copyGraph.getEdge(n ,bestNode));
            }
        }
        for(Edge edge : matching){
            contractEdge(copyGraph, edge);
            nodeWeights.put(edge.getN2(),
                    nodeWeights.get(edge.getN1()) + nodeWeights.get(edge.getN2())
            );
            nodeMap.put(isomorphismMap.get(edge.getN1()), edge.getN2());
        }
        reductionRate = (double)copyGraph.getNodes().size()/graph.getNodes().size();
        nodeMaps.push(nodeMap);
        graphManager.setGraph(copyGraph);
        graphSequence.push(copyGraph);
    }

    public void contractEdge(Graph g, Edge e) {
        for (Node m : e.getN1().getNeighbours()) {
            if(m.equals(e.getN2()))
                continue;
            if(!g.getEdges().isEdgeBetween(m, e.getN2())){
                g.addEdge(m, e.getN2());
            }
        }
        g.removeNode(e.getN1());
    }

    public Stack<Graph> getGraphSequence() {
        return graphSequence;
    }

    public boolean maxLevelReached() {
         return (graphSequence.peek().getEdges().size() <= 1 || reductionRate > 0.75);
    }
}
