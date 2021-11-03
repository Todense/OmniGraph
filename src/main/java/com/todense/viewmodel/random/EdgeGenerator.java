package com.todense.viewmodel.random;

import com.todense.model.graph.Node;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public interface EdgeGenerator {

    Pair<Stack<Integer>, Stack<Integer>> generateConnections();

    void setNodes(List<Node> nodes);

}
