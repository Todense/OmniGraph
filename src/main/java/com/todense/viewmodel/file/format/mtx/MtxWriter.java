package com.todense.viewmodel.file.format.mtx;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MtxWriter implements GraphWriter {

    @Override
    public void writeGraph(Graph graph, File file) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write("%%MatrixMarket matrix coordinate pattern symmetric\n");
            fileWriter.write(graph.getNodes().size()+" "+graph.getNodes().size()+" "+graph.getEdges().size()+"\n");
            for(Edge edge : graph.getEdges()){
                fileWriter.write((edge.getN1().getIndex()+1)+" "+(edge.getN2().getIndex()+1)+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
