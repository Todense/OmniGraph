package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.AntsScope;
import org.apache.commons.math3.exception.MathArithmeticException;

import java.util.List;
import java.util.Random;

public class AntColonySystemTask extends AntColonyAlgorithmTask {

    private Random rnd = new Random();

    public AntColonySystemTask(Graph graph, AntsScope antsScope) {
        super(graph, antsScope);
    }

    @Override
    protected void init() {
        super.init();
        double nnLength = getNearestNeighbourSearchCycleLength();
        minPheromone = 1/(graphOrder * nnLength);
    }


    @Override
    protected void moveAnt(Ant ant) {
        super.moveAnt(ant);

        //local pheromone update
        setPheromone(ant.getPrevious(), ant.getStart(),
                (1 - antsScope.getLocalEvaporation()) * getPheromone(ant.getPrevious(), ant.getStart()) +
                        antsScope.getLocalEvaporation() * minPheromone
        );
    }

    @Override
    protected void updatePheromones() {
        super.updatePheromones();
        super.addPheromoneToGlobalBestCycle(1);
    }

    @Override
    protected int getRandomNeighbour(Ant ant, List<Integer> availableNeighbours) throws MathArithmeticException {
        if (rnd.nextDouble() < antsScope.getExploitationStrength()) {
            return getBestQualityNeighbour(ant, availableNeighbours);
        }else{
            return super.getRandomNeighbour(ant, availableNeighbours);
        }
    }

    private int getBestQualityNeighbour(Ant ant, List<Integer> availableNeighbours){
        double bestQuality = 0;
        int bestNeighbour = 0;
        int start = ant.getStart();
        for (Integer i : availableNeighbours) {
            double dist = this.dist[start][i];
            double edgeQuality = Math.pow(getPheromone(start, i), antsScope.getAlpha())
                    * Math.pow(1 / dist, antsScope.getBeta());
            if (edgeQuality >= bestQuality) {
                bestQuality = edgeQuality;
                bestNeighbour = i;
            }
        }
        return bestNeighbour;
    }

    private double getNearestNeighbourSearchCycleLength(){
        double length = 0;
        boolean[] visited = new boolean[graph.getNodes().size()];
        Node n = graph.getNodes().get(0);
        while(true) {
            visited[n.getIndex()] = true;
            double minLength = Double.POSITIVE_INFINITY;
            Node minLengthNode = null;
            for (Node m : n.getNeighbours()) {
                if (!visited[m.getIndex()]) {
                    if (dist[n.getIndex()][m.getIndex()] < minLength) {
                        minLength = dist[n.getIndex()][m.getIndex()];
                        minLengthNode = m;
                    }
                }
            }
            if (minLengthNode != null) {
                length += minLength;
                visited[minLengthNode.getIndex()] = true;
                n = minLengthNode;
            } else {
                length += dist[n.getIndex()][graph.getNodes().get(0).getIndex()];
                break;
            }
        }

        return length;
    }
}
