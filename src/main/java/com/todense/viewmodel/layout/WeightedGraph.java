package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;

import java.util.HashMap;

public class WeightedGraph {

    private Graph graph;
    private HashMap<Edge, Integer> edgeWeightMap;

    public WeightedGraph(Graph graph, HashMap<Edge, Integer> edgeWeightMap){
        this.graph = graph;
        this.edgeWeightMap = edgeWeightMap;
    }

    public Graph getGraph(){
        return graph;
    }

    public void setWeight(Edge e, int weight){
        edgeWeightMap.put(e, weight);
    }

    public int getWeight(Edge e){
        return edgeWeightMap.get(e);
    }

}
