package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.AntsScope;

public class MaxMinAntSystemTask extends AntColonyAlgorithmTask {


    public MaxMinAntSystemTask(Graph graph, AntsScope antsScope, AlgorithmScope algorithmScope) {
        super(graph, antsScope, algorithmScope);
        super.algorithmName = "Max-Min Ant System";
    }

    @Override
    protected void updatePheromones() {
        super.updatePheromones();
        super.addPheromoneToGlobalBestCycle(1);
        updatePheromoneBounds();
    }

    @Override
    protected double getInitialPheromoneLevel() {
        return super.maxPheromone;
    }

    private void updatePheromoneBounds(){
        super.maxPheromone = (1 / (super.antsScope.getEvaporation()+1e-10)) * (1 / super.getBestSolutionLength());
        super.minPheromone = super.maxPheromone / (2 * super.graphOrder);
    }

}
