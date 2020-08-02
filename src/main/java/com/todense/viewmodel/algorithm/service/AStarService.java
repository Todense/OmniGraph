package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.ShortestPathAlgorithmService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarService extends ShortestPathAlgorithmService {

    private Node startNode;
    private Node goalNode;

    private List<Double> goalDist; //goalDist(i) = dist(goal, i)
    private List<Double> gScores;  //gScore(i) = the cost of the cheapest path from start to i currently known
    private List<Double> fScores;  //fScore(i) = gScore(i) + goalDist(i)

    public AStarService(Node startNode, Node goalNode, boolean customWeight) {
        super(startNode.getGraph(), customWeight);
        this.startNode = startNode;
        this.goalNode = goalNode;
    }

    @Override
    protected void perform() throws InterruptedException {
        pathFound = super.findShortestPath(startNode, goalNode);
    }

    @Override
    protected void init(){
        int n = graph.getNodes().size();
        goalDist = new ArrayList<>(n);
        fScores = new ArrayList<>(n);
        gScores = new ArrayList<>(n);
        super.openSet = new PriorityQueue<>(1, Comparator.comparingDouble(o -> fScores.get(o.getIndex())));
        super.openSet.add(startNode);

        for (int i = 0; i < n; i++) {
            fScores.add(Double.POSITIVE_INFINITY);
            gScores.add(Double.POSITIVE_INFINITY);
            goalDist.add(graph.getNodes().get(i).getPos().distance(goalNode.getPos()));
        }
        gScores.set(startNode.getIndex(), 0d);
        fScores.set(startNode.getIndex(), 0d);
    }

    @Override
    protected void relaxation(Node nodeFrom, Node nodeTo) throws InterruptedException {
        Edge edge = graph.getEdge(nodeFrom, nodeTo);
        double tentativeGScore = gScores.get(nodeFrom.getIndex()) + weightFunction.applyAsDouble(edge);
        if(tentativeGScore < gScores.get(nodeTo.getIndex())){
            if(super.getPrev(nodeTo) != null){
                graph.getEdge(nodeTo, super.getPrev(nodeTo)).setMarked(false);
            }
            super.setPrev(nodeTo, nodeFrom);
            gScores.set(nodeTo.getIndex(), tentativeGScore);
            fScores.set(nodeTo.getIndex(), gScores.get(nodeTo.getIndex()) + goalDist.get(nodeTo.getIndex()));
            edge.setMarked(true);
            if(openSet.contains(nodeTo)){
                super.openSet.remove(nodeTo);  //remove then add to maintain queue order
            }
            super.openSet.offer(nodeTo);
            painter.sleep();
        }
    }
}
