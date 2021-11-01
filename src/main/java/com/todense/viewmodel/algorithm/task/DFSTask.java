package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;

import java.util.HashMap;
import java.util.Stack;


public class DFSTask extends AlgorithmTask {

	private Node startNode;

	public DFSTask(Node startNode, Graph graph){
		super(graph);
		this.startNode = startNode;
		super.algorithmName = "DFS";
	}

	@Override
	public void perform() throws InterruptedException {
		DFS();
	}

	@Override
	protected void onFinished() {
	}

	public void DFS() throws InterruptedException {
		ComponentDFS(startNode);

		for(Node n: graph.getNodes()) {
			if (n.getStatus() != NODE_VISITED) {
				ComponentDFS(n);
			}
		}
	}

	public void ComponentDFS(Node n) throws InterruptedException {

		Stack<Node> stack = new Stack<>();
		HashMap<Node, Node> prev = new HashMap<>();

		graph.getNodes().forEach((node) -> prev.put(node, null));

		stack.push(n);

		while(!stack.isEmpty()){
			Node m = stack.pop();

			if(m.getStatus() == NODE_VISITED)
				continue;

			m.setStatus(NODE_VISITED);
			if(prev.get(m) != null){
				graph.getEdge(m, prev.get(m)).setStatus(EDGE_LIT);
			}
			super.sleep();

			for (Node k : m.getNeighbours()) {
				if(k.getStatus() != NODE_VISITED){
					stack.push(k);
					prev.put(k, m);
				}
			}
		}
	}
}
