package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.ShortestPathAlgorithmService;

import java.util.ArrayList;
import java.util.List;

public class AStarService extends ShortestPathAlgorithmService {

    private Node startNode;
    private Node goalNode;

    private List<Double> goalDist; //goalDist(i) = dist(goal, i)
    private List<Double> gScores;  //gScore(i) = the cost of the cheapest path from start to i currently known
    private List<Double> fScores;  //fScore(i) = gScore(i) + goalDist(i)
    private List<Node> openSet = new ArrayList<>();

    public AStarService(Node startNode, Node goalNode, boolean customWeight) {
        super(startNode.getGraph(), customWeight);
        this.startNode = startNode;
        this.goalNode = goalNode;
    }

    @Override
    protected void perform() throws InterruptedException {
        pathFound = aStar(startNode, goalNode);
    }

    @Override
    protected void onFinished() {
        if(pathFound){
            setResultMessage("Path length: "+String.format("%.3f", pathLength));
        }
        else{
            setResultMessage("Path does not exist!");
        }
    }

    private void init(Node start, Node end){
        int n = graph.getNodes().size();
        goalDist = new ArrayList<>(n);
        fScores = new ArrayList<>(n);
        gScores = new ArrayList<>(n);

        openSet.add(start);

        for (int i = 0; i < n; i++) {
            fScores.add(Double.POSITIVE_INFINITY);
            gScores.add(Double.POSITIVE_INFINITY);
            goalDist.add(graph.getNodes().get(i).getPos().distance(end.getPos()));
        }
        gScores.set(start.getIndex(), 0d);
        fScores.set(start.getIndex(), 0d);
    }

    public boolean aStar(Node start, Node end) throws InterruptedException {
        init(start, end);

        start.setVisited(true);
        painter.sleep();

        while(!openSet.isEmpty()){
            Node current = getLowestFScoreNode();
            if (current == end){
                super.reconstructPath(current, start);
                super.showPath();
                return true;
            }
            openSet.remove(current);
            for(Node neighbour : current.getNeighbours()){
                Edge edge = graph.getEdge(current, neighbour);
                double tentativeGScore = gScores.get(current.getIndex()) + weightFunction.applyAsDouble(edge);
                if(tentativeGScore < gScores.get(neighbour.getIndex())){
                    super.setPrev(neighbour, current);
                    gScores.set(neighbour.getIndex(), tentativeGScore);
                    fScores.set(neighbour.getIndex(), gScores.get(neighbour.getIndex()) + goalDist.get(neighbour.getIndex()));
                    if(!neighbour.isVisited()){
                        openSet.add(neighbour);
                        graph.getEdge(current, neighbour).setMarked(true);
                        neighbour.setVisited(true);
                        painter.sleep();
                    }
                }
            }
        }
        return false;
    }

    private Node getLowestFScoreNode() {
        Node lowestNode = null;
        double lowestValue = Double.POSITIVE_INFINITY;
        for(Node n : openSet){
            double fScore = fScores.get(n.getIndex());
            if(fScore < lowestValue){
                lowestNode = n;
                lowestValue = fScore;
            }
        }
        return lowestNode;
    }
}
