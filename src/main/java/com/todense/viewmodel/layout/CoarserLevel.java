package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;

import java.util.HashMap;

public class CoarserLevel {

    private final Graph prevGraph;
    private final Graph graph;
    private HashMap<Edge, Integer> edgeWeightMap = new HashMap<>();
    private HashMap<Node, Node> nodeMap = new HashMap<>();

    public CoarserLevel(Graph prevGraph, Graph graph){

        this.prevGraph = prevGraph;
        this.graph = graph;
    }

    public void coarser(){
        for (int i = 0; i < prevGraph.getNodes().size(); i++) {
            nodeMap.put(prevGraph.getNodes().get(i), graph.getNodes().get(i));
        }
        for(Node n : graph.getNodes()){
            if(n.isVisited()) continue;
            Node bestNode = null;
            int maxWeight = 0;
        }
    }

}
