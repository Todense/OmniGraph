package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;

import java.util.LinkedList;


public class BFSTask extends AlgorithmTask {

	private Node startNode;

	public BFSTask(Node startNode, Graph graph){
		super(graph);
		this.startNode = startNode;
		super.algorithmName = "BFS";
	}

	@Override
	public void perform() throws InterruptedException {
		BFS();
	}

	@Override
	protected void onFinished() {

	}

	public void BFS() throws InterruptedException {

		ComponentBFS(startNode);

		for(Node n: graph.getNodes()) {
			if (n.getStatus() != NODE_VISITED) {
				ComponentBFS(n);
			}
		}
	}

	public void ComponentBFS(Node n) throws InterruptedException {
		LinkedList<Node> queue = new LinkedList<>();
		queue.add(n);
		n.setStatus(NODE_VISITED);
		super.sleep();
		while(!queue.isEmpty()) {
			Node m = queue.poll();
			for(Node k: m.getNeighbours()) {
				if(k.getStatus() != NODE_VISITED) {
					graph.getEdge(k,m).setStatus(EDGE_LIT);
					queue.add(k);
					k.setStatus(NODE_VISITED);
					super.sleep();
				}
			}
		}
	}
}
