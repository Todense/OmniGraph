package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.ShortestPathAlgorithmService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraService extends ShortestPathAlgorithmService {

 	private List<Double> costList;

	private Node startNode;
	private Node goalNode;

	public DijkstraService(Node startNode, Node goalNode, boolean customWeight) {
		super(startNode.getGraph(), customWeight);
		this.startNode = startNode;
		this.goalNode = goalNode;
	}

	@Override
	protected void perform() throws InterruptedException {
		super.pathFound = super.findShortestPath(startNode, goalNode);
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

	@Override
	protected void init(){
		costList = new ArrayList<>(graph.getNodes().size());
		super.openSet = new PriorityQueue<>(1, Comparator.comparingDouble(this::getCost));

		for (int i = 0; i < graph.getNodes().size(); i++) {
			costList.add(Double.MAX_VALUE);
		}
		super.openSet.add(startNode);
		setCost(startNode, 0);
	}

	@Override
	protected void relaxation(Node nodeFrom, Node nodeTo) throws InterruptedException {
		Edge e =  graph.getEdge(nodeFrom, nodeTo);
		double weight = weightFunction.applyAsDouble(e);
		if(getCost(nodeTo) > getCost(nodeFrom) + weight) {
			setCost(nodeTo, getCost(nodeFrom) + weight);
			if(super.getPrev(nodeTo) != null){
				graph.getEdge(nodeTo, super.getPrev(nodeTo)).setMarked(false);
			}
			e.setMarked(true);
			super.setPrev(nodeTo, nodeFrom);
			if(!nodeTo.isVisited()) {
				nodeTo.setVisited(true);
				super.openSet.offer(nodeTo);
			}
			painter.sleep();
		}
	}

	private double getCost(Node n){
		return costList.get(n.getIndex());
	}

	private void setCost(Node n, double cost){
		costList.set(n.getIndex(), cost);
	}

}
