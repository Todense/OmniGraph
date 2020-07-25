package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.ShortestPathAlgorithmService;

import java.util.ArrayList;
import java.util.List;

public class DijkstraService extends ShortestPathAlgorithmService {

 	private List<Double> costList;
	private List<Node> unvisitedSet;

	private Node startNode;
	private Node goalNode;

	public DijkstraService(Node startNode, Node goalNode, boolean customWeight) {
		super(startNode.getGraph(), customWeight);

		this.startNode = startNode;
		this.goalNode = goalNode;
	}

	@Override
	protected void perform() throws InterruptedException {
			super.pathFound = dijkstra(startNode, goalNode);
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

	private void init(Node start){
		costList = new ArrayList<>(graph.getNodes().size());
		unvisitedSet = new ArrayList<>();

		for (int i = 0; i < graph.getNodes().size(); i++) {
			costList.add(Double.MAX_VALUE);
		}

		for(Node n: graph.getNodes()) {
			unvisitedSet.add(n);
			n.setVisited(false);
		}
		setCost(start, 0);
	}

	public boolean dijkstra(Node start, Node end) throws InterruptedException {
		init(start);

		start.setVisited(true);
		painter.sleep();

		while(!unvisitedSet.isEmpty()) {
			Node current = getMinCostNode();
			if(costList.get(current.getIndex()) == Double.MAX_VALUE) return false;

			//current.setVisited(true);
			unvisitedSet.remove(current);
			painter.sleep();

			if(current == end){
				super.reconstructPath(current, start);
				super.showPath();
				return true;
			}

			for(Node neighbour : current.getNeighbours()) {
				if(!neighbour.isVisited()) {
					relaxation(current, neighbour);
				}
			}
		}
		return false;
	}

	private void relaxation(Node u, Node v) throws InterruptedException {
		Edge e =  graph.getEdge(u, v);
		double weight = weightFunction.applyAsDouble(e);
		if(getCost(v) > getCost(u) + weight) {
			setCost(v, getCost(u) + weight);
			super.setPrev(v, u);
			v.setVisited(true);
			e.setMarked(true);
			painter.sleep();
		}
	}

	private Node getMinCostNode(){
		Node minNode = null;
		double minCost = Double.POSITIVE_INFINITY;
		for(Node n : unvisitedSet){
			if(getCost(n) <= minCost){
				minNode = n;
				minCost = getCost(n);
			}
		}
		return minNode;
	}

	private double getCost(Node n){
		return costList.get(n.getIndex());
	}

	private void setCost(Node n, double cost){
		costList.set(n.getIndex(), cost);
	}

}
