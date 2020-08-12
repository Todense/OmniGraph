package com.todense.viewmodel.file;

import com.todense.model.graph.Graph;

import java.io.FileWriter;

public interface GraphWriter {

    void writeGraph(Graph graph, FileWriter fileWriter);

}
