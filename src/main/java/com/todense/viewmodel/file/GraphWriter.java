package com.todense.viewmodel.file;

import com.todense.model.graph.Graph;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public interface GraphWriter {

    void writeGraph(Graph graph, File file) throws IOException, TransformerException;

}
