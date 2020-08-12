package com.todense.viewmodel.file.format.ogr;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.GraphWriter;

import java.io.FileWriter;
import java.io.IOException;

public class OgrGraphWriter implements GraphWriter {


    @Override
    public void writeGraph(Graph graph, FileWriter fileWriter) {
        try {
            fileWriter.write("NAME: "+graph.toString()+"\n");
            fileWriter.write("NODES: "+graph.getNodes().size()+"\n");
            fileWriter.write("EDGES: "+graph.getEdges().size()+"\n");

            for(Node node : graph.getNodes()){
                fileWriter.write(node.getIndex()+" "
                        +node.getPos().getX()+" "
                        +node.getPos().getY()+" "
                        +"\""+node.getLabelText()+"\" "
                        +node.getColor().toString()
                        +"\n"
                );
            }
            fileWriter.write("\n");
            for(Edge edge : graph.getEdges()){
                fileWriter.write(edge.getN1().getIndex()+" "
                        +edge.getN2().getIndex()+" "
                        +edge.getWeight()+" "
                        +edge.getColor().toString()
                        +"\n"
                );
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
