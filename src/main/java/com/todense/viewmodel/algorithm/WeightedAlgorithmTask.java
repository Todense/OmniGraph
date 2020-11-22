package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;

import java.util.function.ToDoubleFunction;

public abstract class WeightedAlgorithmTask extends AlgorithmTask {

    protected ToDoubleFunction<Edge> weightFunction;

    public WeightedAlgorithmTask(Graph graph, boolean customWeight) {
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
