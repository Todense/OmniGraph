package com.todense.viewmodel.file.format.tsp;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphReader;
import javafx.geometry.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TspReader implements GraphReader {

    @Override
    public Graph readGraph(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String name = scanner.nextLine().split(":")[1];
        Graph graph = new Graph(name);
        String line = "";
        while(!line.equals("NODE_COORD_SECTION")){
            line = scanner.nextLine();
            if(line.startsWith("EDGE_WEIGHT_TYPE")){
                if(!line.split(": ")[1].equals("EUC_2D")){
                    throw new RuntimeException("Unsupported Edge Weight Type");
                }
            }
        }
        line = scanner.nextLine();
        while(!line.equals("EOF")){
            String[] lineData = line.split("\\s+");
            graph.addNode(new Point2D(Double.parseDouble(lineData[1]), Double.parseDouble(lineData[2])));
            line = scanner.nextLine();
        }
        return graph;
    }
}
