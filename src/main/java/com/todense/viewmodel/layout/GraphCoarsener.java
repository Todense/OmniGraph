package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GraphCoarsener {

    private final Graph originalGraph;
    private final GraphManager graphManager;
    private final Stack<Graph> graphSequence = new Stack<>();

    private final Stack<HashMap<Node, Node>> collapseMaps = new Stack<>();
    private HashMap<Node, Integer> nodeWeights = new HashMap<>();

    private double reductionRate = 0;

    public GraphCoarsener(GraphManager graphManager){
        this.graphManager = graphManager;
        this.originalGraph = graphManager.getGraph().copy();
    }

    public void reconstruct() {
        graphSequence.pop();
        Graph previousGraph = graphSequence.peek();
        var mapping = collapseMaps.pop();
        previousGraph.getNodes().forEach(node ->{
                double angle = Math.random() * 2 * Math.PI;
                Point2D newPosition = mapping.get(node).getPos().add(new Point2D(
                                Math.cos(angle),
                                Math.sin(angle)));
                previousGraph.setNodePosition(node, newPosition);
    });
        graphManager.setGraph(previousGraph);
    }

    public void initGraphSequence() {
        graphSequence.add(graphManager.getGraph());
        graphSequence.peek().getNodes().forEach(node -> nodeWeights.put(node, 1));
    }

    // coarsening by collapsing edges in maximal matching
    public void coarsen(){

        // last graph in sequence
        Graph graph = graphSequence.peek();

        // isomorphic graph which will be coarsened and added to graph sequence
        Graph isoGraph = graph.copy();

        // graph -> isoGraph nodes mapping used in prolongation phase for setting positions of added nodes
        HashMap<Node, Node> collapseMap = new HashMap<>();
        graph.getNodes().forEach(node -> collapseMap.put(node, isoGraph.getNodes().get(node.getIndex())));

        // isoGraph -> graph isomorphism
        HashMap<Node, Node> isomorphismMap = new HashMap<>();
        isoGraph.getNodes().forEach(node -> isomorphismMap.put(node, graph.getNodes().get(node.getIndex())));

        // add isoGraph nodes to nodeWeights map
        HashMap<Node, Integer> newNodeWeights = new HashMap<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            newNodeWeights.put(isoGraph.getNodes().get(i), nodeWeights.get(graph.getNodes().get(i)));
        }
        nodeWeights = newNodeWeights;

        // create maximal matching in isoGraph
        List<Edge> matching = new ArrayList<>();
        boolean[] visited = new boolean[isoGraph.getOrder()];
        for(Node n : isoGraph.getNodes()){
            if(visited[n.getIndex()]) continue;
            Node bestNode = null;
            int minWeight = Integer.MAX_VALUE;
            for(Node m : n.getNeighbours()){
                if(visited[m.getIndex()]) continue;
                if(nodeWeights.get(m) < minWeight){
                    minWeight = nodeWeights.get(m);
                    bestNode = m;
                }
            }
            if(bestNode != null){
                visited[n.getIndex()] = true;
                visited[bestNode.getIndex()] = true;
                matching.add(isoGraph.getEdge(n ,bestNode));
            }
        }

        // contract edges in matching
        for(Edge edge : matching){
            Node n1 = edge.getN1();
            Node n2 = edge.getN2();
            contractEdge(isoGraph, edge);

            // update weight of n2
            nodeWeights.put(n2, nodeWeights.get(n1) + nodeWeights.get(n2));

            // replace collapseMap value
            collapseMap.put(isomorphismMap.get(n1), n2);
        }

        reductionRate = (double)isoGraph.getNodes().size()/graph.getNodes().size();
        collapseMaps.push(collapseMap);
        graphManager.setGraph(isoGraph);
        graphSequence.push(isoGraph);
    }

    // when contracting, node n1 is removed, node n2 is left
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

    public Stack<Graph> getGraphSequence() {
        return graphSequence;
    }

    public boolean maxLevelReached() {
        Graph lastGraph = graphSequence.peek();
         return (lastGraph.getSize() <= 5 ||
                 lastGraph.getOrder() <= 5 ||
                 reductionRate > 0.75);
    }

    public Graph getOriginalGraph() {
        return originalGraph;
    }
}
