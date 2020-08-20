package com.todense.viewmodel.file.format.ogr;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.GraphReader;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Scanner;

public class OgrGraphReader implements GraphReader {


    @Override
    public Graph readGraph(Scanner scanner) {
        String[] nameLine = scanner.nextLine().split(" ");
        assert nameLine[0].equals("NAME:");
        String name = nameLine[1];
        String[] nodeCountLine = scanner.nextLine().split(" ");
        assert nodeCountLine[0].equals("NODES:");
        int nodeCount = Integer.parseInt(nodeCountLine[1]);
        String[] edgeCountLine = scanner.nextLine().split(" ");
        assert edgeCountLine[0].equals("EDGES:");
        int edgeCount = Integer.parseInt(edgeCountLine[1]);

        Graph graph = new Graph(name);
        for (int i = 0; i < nodeCount; i++) {
            scanner.nextInt();
            Point2D pos = new Point2D(Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()));
            Node newNode = graph.addNode(pos);
            newNode.setLabelText(scanner.next().replace("\"",""));
            String colorText = scanner.next().replace("\"","");
            if(!colorText.isEmpty()){
                newNode.setColor(Color.valueOf(colorText));
            }

        }
        for (int i = 0; i < edgeCount; i++) {
            Edge newEdge = graph.addEdge(graph.getNodes().get(scanner.nextInt()), graph.getNodes().get(scanner.nextInt()));
            newEdge.setWeight(Double.parseDouble(scanner.next()));
            String colorText = scanner.next().replace("\"","");
            if(!colorText.isEmpty()){
                newEdge.setColor(Color.valueOf(colorText));
            }
        }
        return graph;
    }
}
