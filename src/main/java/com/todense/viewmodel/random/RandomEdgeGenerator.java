package com.todense.viewmodel.random;

import com.todense.model.graph.Node;
import javafx.util.Pair;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class RandomEdgeGenerator implements EdgeGenerator {

    protected final Random rnd = new Random();
    protected List<Node> nodes;
    private Pair<Stack<Integer>, Stack<Integer>> connections;

    public RandomEdgeGenerator(){
    }

    public Pair<Stack<Integer>, Stack<Integer>> generateConnections(){
        generate();
        return this.connections;
    }

    protected abstract void generate();

    protected void addEdge(int i, int j){
        connections.getKey().push(i);
        connections.getValue().push(j);
    }

    public void setNodes(List<Node> nodes){
        this.nodes = nodes;
        connections = new Pair<>(new Stack<>(), new Stack<>());
    }
}
