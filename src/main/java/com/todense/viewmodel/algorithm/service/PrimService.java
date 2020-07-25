package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.WeightedAlgorithmService;

import java.util.Comparator;
import java.util.PriorityQueue;


public class PrimService extends WeightedAlgorithmService {

	double weight;

	private double[] cost;
	private Node[] prev;
	private PriorityQueue<Node> queue;
	private int componentCount;
	private Node startNode;

	public PrimService(Node startNode, boolean customWeight) {
		super(startNode.getGraph(), customWeight);
		this.startNode = startNode;
	}


	@Override
	protected void perform() throws InterruptedException {

		prim(startNode);
	}

	@Override
	protected void onFinished() {
		if(componentCount == 1) {
			setResultMessage("Weight of  minimum-spanning-tree: " + String.format("%.3f", weight));
		}else{
			setResultMessage("Weight of  minimum-spanning-forest: " + String.format("%.3f", weight));
		}
	}


	void init(Node start){
		cost = new double[graph.getNodes().size()];
		weight = 0;
		prev = new Node[graph.getNodes().size()];

		for(Node n : graph.getNodes()) {
			setCost(n, Double.POSITIVE_INFINITY);
			n.setVisited(false);
		}
		setCost(start, 0);

		queue = new PriorityQueue<>(Comparator.comparingDouble(this::getCost));

		for(Node n : graph.getNodes()) {
			queue.offer(n);
		}
	}

	public void prim (Node start)  throws InterruptedException {
		init(start);
		Node current;

		while(!queue.isEmpty()) {
			current = queue.poll();
			current.setVisited(true);

			if(getPrev(current) != null){
				Edge e = graph.getEdge(current, getPrev(current));
				e.setMarked(true);
				weight += weightFunction.applyAsDouble(e);
			}else{
				componentCount++;
			}
			painter.sleep();


			for(Node neighbour : current.getNeighbours()) {
				if(!neighbour.isVisited()){
					Edge e = graph.getEdge(current, neighbour);
					double weight = weightFunction.applyAsDouble(e);
					if(weight < getCost(neighbour)){
						queue.remove(neighbour);
						setCost(neighbour, weight);   //update queue
						queue.offer(neighbour);
						setPrev(neighbour, current);
					}
				}
			}
		}
	}

	private double getCost(Node n){
		return cost[n.getIndex()];
	}

	private void setCost(Node n, double value){
		cost[n.getIndex()] = value;
	}

	private void setPrev(Node n, Node m){
		prev[n.getIndex()] = m;
	}

	private Node getPrev(Node n){
		return prev[n.getIndex()];
	}
}
