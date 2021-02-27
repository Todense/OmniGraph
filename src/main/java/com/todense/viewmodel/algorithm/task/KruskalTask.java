package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.WeightedAlgorithmTask;
import com.todense.viewmodel.graph.GraphAnalyzer;

import java.util.Comparator;
import java.util.PriorityQueue;

public class KruskalTask extends WeightedAlgorithmTask {

	double weight;
	int counter;
	private PriorityQueue<Edge> queue;
	int[] parent;
	int componentCount;

	public KruskalTask(Graph graph, boolean customWeight) {
		super(graph, customWeight);
		super.algorithmName = "Kruskal's algorithm";
	}

	@Override
	public void perform() throws InterruptedException {
		result = kruskal();
	}

	@Override
	protected void onFinished() {
		if(componentCount == 1) {
			setResultMessage("Weight of  minimum-spanning-tree: " + String.format("%.3f", weight));
		}else{
			setResultMessage("Weight of  minimum-spanning-forest: " + String.format("%.3f", weight));
		}
	}

	void init(){
		weight = 0;
		counter = 0;
		parent = new int[graph.getNodes().size()];

		queue = new PriorityQueue<>(Comparator.comparingDouble(weightFunction));

		for (int i = 0; i < parent.length ; i++) {
			parent[i] = i;
		}

		for(Edge e : graph.getEdges()){
			queue.offer(e);
		}

		componentCount = GraphAnalyzer.getComponentCount(graph);

	}

	public double kruskal() throws InterruptedException {

		init();
		while(counter < graph.getNodes().size() - componentCount) {
			Edge e = queue.poll();

			assert e != null;

			Node n1 = e.getN1();
			Node n2 = e.getN2();

			int x = n1.getIndex();
			int y = n2.getIndex();

			int xSet = find(x);
			int ySet = find(y);

			if(xSet != ySet){
				e.setMarked(true);
				n1.setMarked(true);
				n2.setMarked(true);
				super.sleep();

				weight += weightFunction.applyAsDouble(e);
				union(xSet, ySet);
				counter++;
			}
		}
		return weight;
	}

	public int find(int nodeIndex){
		if(parent[nodeIndex] != nodeIndex) {
			return find(parent[nodeIndex]);
		}
		return nodeIndex;
	}

	public void union(int x, int y){
		int x_set_parent = find(x);
		int y_set_parent = find(y);
		parent[y_set_parent] = x_set_parent;
	}

}
