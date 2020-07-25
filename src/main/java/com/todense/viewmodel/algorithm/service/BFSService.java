package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmService;

import java.util.LinkedList;


public class BFSService extends AlgorithmService {

	private Node startNode;

	public BFSService(Node startNode){
		super(startNode.getGraph());
		this.startNode = startNode;
	}

	@Override
	protected void perform() throws InterruptedException {
		BFS();
	}

	@Override
	protected void onFinished() {

	}

	public void BFS() throws InterruptedException {

		ComponentBFS(startNode);

		for(Node n: graph.getNodes()) {
			if (!n.isVisited()) {
				ComponentBFS(n);
			}
		}
	}

	public void ComponentBFS(Node n) throws InterruptedException{
		LinkedList<Node> queue = new LinkedList<>();
		queue.add(n);
		n.setVisited(true);
		painter.sleep();
		while(!queue.isEmpty()) {
			Node m = queue.poll();
			for(Node k: m.getNeighbours()) {
				if(!k.isVisited()) {
					graph.getEdge(k,m).setMarked(true);
					queue.add(k);
					k.setVisited(true);
					painter.sleep();
				}
			}
		}
	}
}
