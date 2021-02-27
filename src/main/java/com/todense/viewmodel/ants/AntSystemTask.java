package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.AntsScope;

public class AntSystemTask extends AntColonyAlgorithmTask {

    public AntSystemTask(Graph graph, AntsScope antsScope, AlgorithmScope algorithmScope) {
        super(graph, antsScope, algorithmScope);
        super.algorithmName = "Ant System";
    }

    @Override
    protected void updatePheromones() {
        super.updatePheromones();

        //each ant adds pheromone to its cycle
        for(Ant ant : antsScope.getAnts()){
            addPheromoneToAntCycle(ant);
        }
    }

    private void addPheromoneToAntCycle(Ant ant){
        for (int i = 0; i < ant.getCycle().size(); i++) {
            int i1 = ant.getCycle().get(i);
            int i2 = ant.getCycle().get((i + 1) % graphOrder);
            super.addPheromone(i1, i2, 1/ant.getCycleLength());
        }
    }
}
