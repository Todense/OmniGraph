package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class ShortestPathAlgorithmTask extends WeightedAlgorithmTask {

    protected List<Node> path = new ArrayList<>();
    protected List<Node> previousNodes;
    protected PriorityQueue<Node> openSet;

    protected double pathLength = 0;
    protected boolean pathFound = false;

    @Override
    protected void onFinished() {
        if(pathFound){
            setResultMessage("Path length: "+String.format("%.3f", pathLength));
        }
        else{
            setResultMessage("Path does not exist!");
        }
    }

    public ShortestPathAlgorithmTask(Graph graph, boolean customWeight) {
        super(graph, customWeight);
        previousNodes = new ArrayList<>(graph.getNodes().size());
        for (int i = 0; i < graph.getNodes().size(); i++) {
            previousNodes.add(null);
        }
    }

    protected abstract void init();

    protected abstract void relaxation(Node nodeFrom, Node nodeTo) throws InterruptedException;

    protected boolean findShortestPath(Node start, Node end) throws InterruptedException {
        init();

        while(!openSet.isEmpty()){
            Node current = openSet.poll();
            current.setMarked(true);
            super.sleep();

            if (current == end){
                reconstructPath(current, start);
                showPath();
                return true;
            }

            for(Node neighbour : current.getNeighbours()){
                relaxation(current, neighbour);
            }
        }
        return false;
    }

    protected void showPath(){
        for(Node n : graph.getNodes()){
            n.setMarked(false);
        }
        for(Edge e : graph.getEdges()){
            e.setMarked(false);
        }
        for(Node n : path){
            n.setMarked(true);
        }
        for (int i = 0; i < path.size()-1; i++) {
            Edge e = graph.getEdge(path.get(i), path.get(i+1));
            pathLength += weightFunction.applyAsDouble(e);
            e.setMarked(true);
        }
        super.repaint();
    }

    protected void reconstructPath(Node nodeFrom, Node start){
        Node prev = previousNodes.get(nodeFrom.getIndex());
        path.add(nodeFrom);
        while(prev != start){
            path.add(0, prev);
            prev = previousNodes.get(prev.getIndex());
        }
        path.add(0, start);
    }

    protected void setPrev(Node n, Node m){
        previousNodes.set(n.getIndex(), m);
    }

    protected Node getPrev(Node n){
        return previousNodes.get(n.getIndex());
    }
}
