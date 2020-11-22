package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.scope.AntsScope;

import java.util.Comparator;

public class RankedAntSystem extends AntColonyAlgorithmTask {

    public RankedAntSystem(Graph graph, AntsScope antsScope) {
        super(graph, antsScope);
    }

    @Override
    protected Ant getIterationBestAnt() {
        antsScope.getAnts().sort(Comparator.comparingDouble(Ant::getCycleLength));
        return antsScope.getAnts().get(0);
    }

    @Override
    protected void updatePheromones() {
        super.updatePheromones();
        addPheromoneToGlobalBestCycle(antsScope.getRankSize());
        addPheromoneToIterationBestCycles(antsScope.getRankSize());
    }

    private void addPheromoneToIterationBestCycles(int rankNumber){
        for(int i = 1; i <= rankNumber; i++){
            Ant a = antsScope.getAnts().get(i-1);  //ants are sorted
            for (int j = 0; j < a.getCycle().size(); j++) {
                int i1 = a.getCycle().get(j);
                int i2 = a.getCycle().get((j + 1) % graphOrder);
                if(super.isImportant[i1][i2]) {
                    addPheromone(i1, i2, (rankNumber - i) / a.getCycleLength());
                }
            }
        }
    }
}
