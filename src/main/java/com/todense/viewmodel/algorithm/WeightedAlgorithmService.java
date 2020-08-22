package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;

import java.util.function.ToDoubleFunction;

public abstract class WeightedAlgorithmService extends AlgorithmService{

    protected ToDoubleFunction<Edge> weightFunction;

    public WeightedAlgorithmService(Graph graph, boolean customWeight) {
        super(graph);
        if(customWeight){
            weightFunction = Edge::getWeight;
        }
        else{
            for(Edge e: graph.getEdges()){
                e.calcLength();
            }
            weightFunction = Edge::getLength;
        }
    }

    @Override
    public void perform() throws InterruptedException {
    }

    @Override
    protected void onFinished() {
    }
}
