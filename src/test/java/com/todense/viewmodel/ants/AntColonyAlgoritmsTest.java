package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.format.tsp.TspReader;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.AntsScope;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;


class AntColonyAlgoritmsTest {

    @Test
    @Disabled
    void antTest(){
        TspReader tspReader = new TspReader();
        Graph graph = tspReader.readGraph(new File("C:\\Users\\DELL\\Desktop\\Graphs\\Tsp\\ch130.tsp"));
        for(int i = 0 ; i < graph.getNodes().size(); i++){
            Node n = graph.getNodes().get(i);
            for (int j = i+1; j < graph.getNodes().size(); j++){
                Node m = graph.getNodes().get(j);
                graph.addEdge(n, m);
            }
        }
        double trails = 1;
        AntsScope antScope = new AntsScope();
        antScope.betaProperty().setValue(8.2);
        antScope.neighbourhoodSizeProperty().setValue(25);
        antScope.antCountProperty().setValue(20);
        antScope.exploitationStrengthProperty().set(0.8);
        antScope.evaporationProperty().set(0.1);
        antScope.localEvaporationProperty().set(0.21);
        //int[] values = IntStream.range(5, 13).toArray();
        //double[] values = DoubleStream.iterate(0.02, d -> d + 0.01).limit(10).toArray();
        double[] values = DoubleStream.iterate(7.5, d -> d + 0.1).limit(10).toArray();

        for(Double d : values){
            double sum = 0;
            double min = Double.POSITIVE_INFINITY;
            double iterSum = 0;
            double explorSum = 0;
            for (int j = 0; j < trails; j++) {
                antScope.betaProperty().set(d);
                AntColonyAlgorithmTask antService = new AntSystemTask(graph, antScope);
                antService.setMaxTimeSec(10);
                antService.setMinSolution(6111);
                antService.perform();
                sum += antService.getResult();
                iterSum += antService.getIterationCounter();
                explorSum += antService.getExplorationRate();
                if(antService.getResult() < min){
                    min = antService.getResult();
                }
            }
            System.out.println("Value: "+String.format("%.3f", d)+
                            " avg: " +String.format("%.3f", sum/trails)+
                            " best: "+String.format("%.3f", min)+
                            " explor: "+ String.format("%.3f", explorSum/trails)+
                            " iter: "+ iterSum/trails);
        }

    }

    @Test
    void cycleLengthTest(){
        AntsScope antsScope = new AntsScope();
        GraphManager GM = new GraphManager();
        Graph graph = GM.getGraph();
        graph.addNode(new Point2D(0, 0));
        graph.addNode(new Point2D(0, 1));
        graph.addNode(new Point2D(1, 1));
        graph.addNode(new Point2D(1, 0));

        GM.createCompleteGraph();
        antsScope.with2OptProperty().setValue(false);

        List<AntColonyAlgorithmTask> algorithms = new ArrayList<>(Arrays.asList(
                new AntSystemTask(graph, antsScope),
                new AntColonySystemTask(graph, antsScope),
                new MaxMinAntSystemTask(graph, antsScope),
                new RankedAntSystem(graph, antsScope)));

        for(var algorithm : algorithms){
            algorithm.setMaxTimeSec(0.2);
            algorithm.perform();
            Assertions.assertEquals(4, algorithm.getResult(), 0.01);
        }
    }
}