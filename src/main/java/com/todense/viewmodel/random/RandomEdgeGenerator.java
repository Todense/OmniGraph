package com.todense.viewmodel.random;

import com.todense.model.graph.Node;

import java.util.List;
import java.util.Random;

public abstract class RandomEdgeGenerator implements EdgeGenerator {

    protected Random rnd = new Random();
    protected List<Node> nodes;
    private boolean[][] adjacencyMatrix;

    public RandomEdgeGenerator(){
    }

    public boolean[][] generateAdjacencyMatrix(){
        generate();
        return this.adjacencyMatrix;
    }

    protected abstract void generate();

    protected void addEdge(int i, int j){
        if(i < j){
            adjacencyMatrix[i][j] = true;
        }
        else{
            adjacencyMatrix[j][i] = true;
        }
    }

    public void setNodes(List<Node> nodes){
        this.nodes = nodes;
        adjacencyMatrix = new boolean[nodes.size()][nodes.size()];
    }
}
