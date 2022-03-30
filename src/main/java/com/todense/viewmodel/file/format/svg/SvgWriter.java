package com.todense.viewmodel.file.format.svg;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphWriter;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SvgWriter implements GraphWriter {

    @Override
    public void writeGraph(Graph graph, File file) throws IOException, TransformerException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n");
        fileWriter.close();
    }
}
