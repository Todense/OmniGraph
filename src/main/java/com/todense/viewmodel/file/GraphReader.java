package com.todense.viewmodel.file;

import com.todense.model.graph.Graph;

import java.io.File;

public interface GraphReader {

    Graph readGraph(File file);


}
