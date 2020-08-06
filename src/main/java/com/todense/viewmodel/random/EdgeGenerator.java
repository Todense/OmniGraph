package com.todense.viewmodel.random;

import com.todense.model.graph.Node;

import java.util.List;

public interface EdgeGenerator {

    boolean[][] generateAdjacencyMatrix();

    void setNodes(List<Node> nodes);

}
