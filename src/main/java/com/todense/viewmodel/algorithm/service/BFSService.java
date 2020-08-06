package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmService;

import java.util.LinkedList;


public class BFSService extends AlgorithmService {

	private Node startNode;

	public BFSService(Node startNode, Graph graph){
		super(graph);
		this.startNode = startNode;
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
			if (!n.isMarked()) {
				ComponentBFS(n);
			}
		}
	}

	public void ComponentBFS(Node n) throws InterruptedException {
		LinkedList<Node> queue = new LinkedList<>();
		queue.add(n);
		n.setMarked(true);
		super.sleep();
		while(!queue.isEmpty()) {
			Node m = queue.poll();
			for(Node k: m.getNeighbours()) {
				if(!k.isMarked()) {
					graph.getEdge(k,m).setMarked(true);
					queue.add(k);
					k.setMarked(true);
					super.sleep();
				}
			}
		}
	}
}
