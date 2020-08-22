package com.todense.viewmodel.file;

import com.todense.model.graph.Graph;

import java.io.File;

public interface GraphWriter {

    void writeGraph(Graph graph, File file);

}
