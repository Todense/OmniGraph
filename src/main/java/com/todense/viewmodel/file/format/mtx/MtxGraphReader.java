package com.todense.viewmodel.file.format.mtx;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.GraphReader;
import com.todense.viewmodel.random.arrangement.generators.RandomSquarePointGenerator;
import javafx.geometry.Point2D;

import java.util.Scanner;

public class MtxGraphReader implements GraphReader {

    private Point2D center;
    private double areaSize;

    public MtxGraphReader(Point2D center, double areaSize){
        this.center = center;
        this.areaSize = areaSize;
    }

    @Override
    public Graph readGraph(Scanner scanner) {
        Graph graph = new Graph("graph0");
        String line = scanner.nextLine();
        while (line.startsWith("%")){
            line = scanner.nextLine();
        }
        String[] graphInfo = line.split(" ");
        int nodeCount = Integer.parseInt(graphInfo[0]);
        RandomSquarePointGenerator pointGenerator = new RandomSquarePointGenerator(areaSize, center);
        for (int i = 0; i < nodeCount; i++) {
            graph.addNode(pointGenerator.next());
        }
        while (scanner.hasNextLine()){
            line = scanner.nextLine();
            String[] nodeInfo = line.split(" ");
            int i = Integer.parseInt(nodeInfo[0])-1;
            int j = Integer.parseInt(nodeInfo[1])-1;
            if(i != j){
                graph.addEdge(graph.getNodes().get(i), graph.getNodes().get(j));
            }

        }
        return graph;
    }
}
