package com.todense.viewmodel.file;

import com.todense.model.graph.Graph;

import java.util.Scanner;

public interface GraphReader {

    Graph readGraph(Scanner scanner);
}
