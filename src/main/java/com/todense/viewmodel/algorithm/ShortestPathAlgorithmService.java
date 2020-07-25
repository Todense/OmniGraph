package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class ShortestPathAlgorithmService extends WeightedAlgorithmService {

    protected List<Node> path = new ArrayList<>();
    protected List<Node> previousNodes;

    protected double pathLength = 0;
    protected boolean pathFound = false;

    public ShortestPathAlgorithmService(Graph graph, boolean customWeight) {
        super(graph, customWeight);
        previousNodes = new ArrayList<>(graph.getNodes().size());
        for (int i = 0; i < graph.getNodes().size(); i++) {
            previousNodes.add(null);
        }
    }

    protected void showPath(){
        for(Node n : graph.getNodes()){
            n.setVisited(false);
        }
        for(Edge e : graph.getEdges()){
            e.setMarked(false);
        }
        for(Node n : path){
            n.setVisited(true);
        }
        for (int i = 0; i < path.size()-1; i++) {
            Edge e = graph.getEdge(path.get(i), path.get(i+1));
            pathLength += weightFunction.applyAsDouble(e);
            e.setMarked(true);
        }
        painter.repaint();
    }

    protected void reconstructPath(Node nodeFrom, Node start){
        Node prev = previousNodes.get(nodeFrom.getIndex());
        path.add(nodeFrom);
        while(prev != start){
            path.add(0, prev);
            prev = previousNodes.get(prev.getIndex());
        }
        path.add(0, start);
    }

    protected void setPrev(Node n, Node m){
        previousNodes.set(n.getIndex(), m);
    }
}
