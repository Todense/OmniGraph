package com.todense.viewmodel.file.format.tsp;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.GraphWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TspWriter implements GraphWriter {


    @Override
    public void writeGraph(Graph graph, File file) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write("NAME: "+graph.toString()+"\n");
            fileWriter.write("COMMENT: "+"\n");
            fileWriter.write("TYPE: "+"TSP"+"\n");
            fileWriter.write("DIMENSION: "+graph.getNodes().size()+"\n");
            fileWriter.write("EDGE_WEIGHT_TYPE: "+"EUC_2D"+"\n");
            fileWriter.write("NODE_COORD_SECTION"+"\n");

            for (int i = 0; i < graph.getNodes().size(); i++) {
                Node node = graph.getNodes().get(i);
                fileWriter.write((i+1)+" "+node.getPos().getX()+" "+node.getPos().getY()+"\n");
            }
            fileWriter.write("EOF");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
