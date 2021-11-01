package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class HamiltonianCycleSearchTask extends AlgorithmTask {

    ArrayList<Node> cycle;
    boolean[] visitedDFS;

    boolean cycleFound;
    
    private Node startNode;
    private boolean checkingConnectivity;

    public HamiltonianCycleSearchTask(Node startNode, Graph graph, boolean checkingConnectivity){
        super(graph);
        this.startNode = startNode;
        this.checkingConnectivity = checkingConnectivity;
        super.algorithmName = "Hamiltonian Cycle Search";
    }

    @Override
    public void perform() throws InterruptedException {
        cycleFound = HCSearch(startNode);
    }

    @Override
    protected void onFinished() {
        if (cycleFound) {
            setResultMessage("Hamiltonian cycle found");
        } else {
            setResultMessage("Graph is not hamiltonian");
        }
    }

    private void init(){
        int nodeCount = graph.getNodes().size();
        cycle = new ArrayList<>();
        visitedDFS = new boolean[nodeCount];
    }


    boolean HCSearch(Node startNode) throws InterruptedException {

        init();

        Stack<Node> nodeStack = new Stack<>();

        //push node twice: first time for removing from a cycle when backtracking, second time for adding node to a cycle
        nodeStack.push(startNode);
        nodeStack.push(startNode);

        while(!nodeStack.isEmpty()){
            if(cycle.size() == graph.getNodes().size()) {
                Edge edge = graph.getEdge(cycle.get(0), cycle.get(cycle.size()-1));
                if (edge != null) {
                    edge.setStatus(EDGE_LIT);
                    super.sleep();
                    return true;
                }
            }

            Node n = nodeStack.pop();

            if(!cycle.contains(n)){
                if(n.getStatus() == NODE_VISITED)
                    continue;

                cycle.add(n);

                n.setStatus(NODE_VISITED);
                if(cycle.size() > 1) {
                    graph.getEdge(cycle.get(cycle.size()-1), cycle.get(cycle.size()-2)).setStatus(EDGE_LIT);
                }
                super.sleep();

                if(checkingConnectivity){
                    if(!isStartNodeConnectedWithUnvisitedNodes()){
                        continue;
                    }
                }

                for(Node m : n.getNeighbours()){
                    if(m.getStatus() != NODE_VISITED){
                        nodeStack.push(m);
                        nodeStack.push(m);
                    }
                }
            }
            else{
                Node lastNode = cycle.get(cycle.size()-1);
                lastNode.setStatus(NODE_UNVISITED);
                if(cycle.size() > 1) {
                    graph.getEdge(lastNode, cycle.get(cycle.size() - 2)).setStatus(EDGE_UNLIT);
                }
                cycle.remove(cycle.size()-1);
                super.sleep();
            }
        }
        return false;
    }

    private boolean isStartNodeConnectedWithUnvisitedNodes() {
        int visitedDFSCount;
        Arrays.fill(visitedDFS, false);
        Node startNodeUnvisitedNeighbour = null;
        for(Node n : startNode.getNeighbours()){
            if(n.getStatus() != NODE_VISITED){
                startNodeUnvisitedNeighbour = n;
                break;
            }
        }
        if(startNodeUnvisitedNeighbour == null){
            return false;
        }

        DFS(startNodeUnvisitedNeighbour);

        visitedDFSCount = (int) graph.getNodes().stream().filter(node -> visitedDFS[node.getIndex()]).count();
        return visitedDFSCount >= graph.getNodes().size() - cycle.size();
    }

    void DFS(Node n){
        Stack<Node> stack = new Stack<>();
        stack.push(n);

        while(!stack.isEmpty()){
            Node m = stack.pop();

            if(visitedDFS[m.getIndex()])
                continue;

            visitedDFS[m.getIndex()] = true;

            for (Node k : m.getNeighbours()) {
                if(!visitedDFS[k.getIndex()] && k.getStatus() != NODE_VISITED){
                    stack.push(k);
                }
            }
        }
    }

    public boolean isCycleFound() {
        return cycleFound;
    }
}
