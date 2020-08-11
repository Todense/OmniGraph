package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.canvas.Painter;
import com.todense.viewmodel.graph.GraphManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GraphCoarser {

    private GraphManager graphManager;
    private Painter painter;
    private Stack<Graph> graphList = new Stack<>();
    private Stack<HashMap<Node, Node>> nodeMaps = new Stack<>();
    private HashMap<Node, Integer> nodeWeights = new HashMap<>();

    public GraphCoarser(GraphManager graphManager){
        this.graphManager = graphManager;
    }

    public void coarserDown() {
        Graph currentGraph  = graphList.pop();
        Graph previousGraph = graphList.peek();
        var mapping = nodeMaps.pop();
        previousGraph.getNodes().forEach(node -> node.setPos(mapping.get(node).getPos().add(Math.random() - 0.5, Math.random() - 0.5)));
        graphManager.setGraph(previousGraph);
        try {
            painter.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createList() {
        graphList.add(graphManager.getGraph());
        nodeWeights = new HashMap<>();
        graphList.peek().getNodes().forEach(node -> nodeWeights.put(node, 1));

        while(graphList.peek().getEdges().size() > 1){
            coarserGraph();
        }
    }

    private void coarserGraph(){
        Graph graph = graphList.peek();
        Graph copyGraph = graph.copy();
        HashMap<Node, Node> nodeMap = new HashMap<>();
        HashMap<Node, Node> isomorphismMap = new HashMap<>();
        copyGraph.getNodes().forEach(node -> isomorphismMap.put(node, graph.getNodes().get(node.getIndex())));
        graph.getNodes().forEach(node -> nodeMap.put(node, copyGraph.getNodes().get(node.getIndex())));
        HashMap<Node, Integer> newNodeWeights = new HashMap<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            newNodeWeights.put(copyGraph.getNodes().get(i), nodeWeights.get(graph.getNodes().get(i)));
        }
        nodeWeights = newNodeWeights;
        List<Edge> matching = new ArrayList<>();
        for(Node n : copyGraph.getNodes()){
            if(n.isVisited()) continue;
            Node bestNode = null;
            int minWeight = Integer.MAX_VALUE;
            for(Node m : n.getNeighbours()){
                if(m.isVisited()) continue;
                if(nodeWeights.get(m) < minWeight){
                    minWeight = nodeWeights.get(m);
                    bestNode = m;
                }
            }
            if(bestNode != null){
                n.setVisited(true);
                bestNode.setVisited(true);
                matching.add(copyGraph.getEdge(n ,bestNode));
            }
        }
        for(Edge edge : matching){
            contractEdge(copyGraph, edge);
            nodeWeights.put(edge.getN2(),
                    nodeWeights.get(edge.getN1()) + nodeWeights.get(edge.getN2())
            );
            nodeMap.put(isomorphismMap.get(edge.getN1()), edge.getN2());
        }

        nodeMaps.push(nodeMap);
        graphManager.setGraph(copyGraph);
        graphList.push(copyGraph);
        try {
            painter.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void contractEdge(Graph g, Edge e) {
        for (Node m : e.getN1().getNeighbours()) {
            if(m.equals(e.getN2()))
                continue;
            if(!g.getEdges().isEdgeBetween(m, e.getN2())){
                g.addEdge(m, e.getN2());
            }
        }
        g.removeNode(e.getN1());
    }

    public Stack<Graph> getGraphList() {
        return graphList;
    }

    public Stack<HashMap<Node, Node>> getNodeMaps() {
        return nodeMaps;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
    }
}
