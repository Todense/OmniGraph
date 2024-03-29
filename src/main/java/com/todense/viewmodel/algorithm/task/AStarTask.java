package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.ShortestPathAlgorithmTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarTask extends ShortestPathAlgorithmTask {

    private final Node startNode;
    private final Node goalNode;

    private List<Double> goalDist; //goalDist(i) = dist(goal, i)
    private List<Double> gScores;  //gScore(i) = the cost of the cheapest path from start to i currently known
    private List<Double> fScores;  //fScore(i) = gScore(i) + goalDist(i)

    public AStarTask(Node startNode, Node goalNode, Graph graph, boolean customWeight) {
        super(graph, customWeight);
        this.startNode = startNode;
        this.goalNode = goalNode;
        super.algorithmName = "A* Shortest Path";
    }

    @Override
    public void perform() throws InterruptedException {
        pathFound = super.findShortestPath(startNode, goalNode);
        result = super.pathLength;
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
                graph.getEdge(nodeTo, super.getPrev(nodeTo)).setStatus(EDGE_UNLIT);
            }
            super.setPrev(nodeTo, nodeFrom);
            gScores.set(nodeTo.getIndex(), tentativeGScore);
            fScores.set(nodeTo.getIndex(), gScores.get(nodeTo.getIndex()) + goalDist.get(nodeTo.getIndex()));
            edge.setStatus(EDGE_LIT);
            if(openSet.contains(nodeTo)){
                super.openSet.remove(nodeTo);  //remove then add to maintain queue order
            }
            super.openSet.offer(nodeTo);
            nodeTo.setStatus(NODE_VISITED);
            super.sleep();
        }
    }
}
