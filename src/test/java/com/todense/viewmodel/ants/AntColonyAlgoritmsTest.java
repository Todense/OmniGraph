package com.todense.viewmodel.ants;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.file.format.tsp.TspReader;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.preset.PresetCreator;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.geometry.Point2D;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.GuideTableDiscreteSampler;
import org.apache.commons.rng.sampling.distribution.SharedStateDiscreteSampler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
        AlgorithmScope algorithmScope = new AlgorithmScope();
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
                AntColonyAlgorithmTask antService = new AntSystemTask(graph, antScope, algorithmScope);
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
    @Disabled
    void gridTest() {
        Graph graph = PresetCreator.createHexagonalGrid(5, new Point2D(1,1));
        GraphScope graphScope = new GraphScope();
        graphScope.getGraphManager().setGraph(graph);
        graphScope.getGraphManager().createCompleteGraph();
        AntsScope antScope = new AntsScope();
        AlgorithmScope algorithmScope = new AlgorithmScope();
        AntColonySystemTask antAlgorithm = new AntColonySystemTask(graphScope.getGraphManager().getGraph(), antScope, algorithmScope);
        antAlgorithm.setMaxTimeSec(10);
        antAlgorithm.perform();
        System.out.println(antAlgorithm.getIterationCounter());
    }

    class Distribution{
        double[] cumulativeProbs;
        double sumProb;
        Random rand = new Random();

        Distribution(double[] probabilities){
            cumulativeProbs = new double[probabilities.length];
            for (int i = 0; i < probabilities.length; i++) {
                sumProb += probabilities[i];
                this.cumulativeProbs[i] = sumProb;
            }
        }

        public int sample(){
            double prob = rand.nextDouble()*sumProb;
            return Arrays.binarySearch(cumulativeProbs, prob);
        }
    }

    @Test
    @Disabled
    void apacheDistributionsTest(){
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            ArrayList<Pair<Integer, Double>> probabilities = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                probabilities.add(Pair.create(j, (double)j));
            }
            EnumeratedDistribution<Integer> enumeratedDistribution = new EnumeratedDistribution<>(probabilities);
            enumeratedDistribution.sample();
        }
        long duration = (System.nanoTime()-startTime)/1000000;
        System.out.println(duration);
    }

    @Test
    @Disabled
    void customDistributionsTest(){
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            double[] probabilities = new double[100];
            for (int j = 0; j < 100; j++) {
                probabilities[j] = j;
            }
            Distribution distribution = new Distribution(probabilities);
            for (int j = 0; j < 10000; j++) {
                distribution.sample();
            }
        }
        long duration = (System.nanoTime()-startTime)/1000000;
        System.out.println(duration);
    }

    @Test
    @Disabled
    void guideTableDistributionsTest(){
        long startTime = System.nanoTime();
        UniformRandomProvider provider = new UniformRandomProvider() {
            final Random random = new Random();
            @Override
            public void nextBytes(byte[] bytes) {

            }

            @Override
            public void nextBytes(byte[] bytes, int i, int i1) {

            }

            @Override
            public int nextInt() {
                return 0;
            }

            @Override
            public int nextInt(int i) {
                return 0;
            }

            @Override
            public long nextLong() {
                return 0;
            }

            @Override
            public long nextLong(long l) {
                return 0;
            }

            @Override
            public boolean nextBoolean() {
                return false;
            }

            @Override
            public float nextFloat() {
                return 0;
            }

            @Override
            public double nextDouble() {
                return random.nextDouble();
            }
        };
        for (int i = 0; i < 1000000; i++) {
            double[] probabilities = new double[20];
            for (int j = 0; j < 20; j++) {
                probabilities[j] = j;
            }
            SharedStateDiscreteSampler sampler = GuideTableDiscreteSampler.of(provider, probabilities);
            sampler.sample();
        }
        long duration = (System.nanoTime()-startTime)/1000000;
        System.out.println(duration);
    }


    @Test
    void cycleLengthTest(){
        AntsScope antsScope = new AntsScope();
        AlgorithmScope algorithmScope = new AlgorithmScope();
        GraphManager GM = new GraphManager();
        Graph graph = GM.getGraph();
        graph.addNode(new Point2D(0, 0));
        graph.addNode(new Point2D(0, 1));
        graph.addNode(new Point2D(1, 1));
        graph.addNode(new Point2D(1, 0));

        GM.createCompleteGraph();
        antsScope.with2OptProperty().setValue(false);

        List<AntColonyAlgorithmTask> algorithms = new ArrayList<>(Arrays.asList(
                new AntSystemTask(graph, antsScope, algorithmScope),
                new AntColonySystemTask(graph, antsScope, algorithmScope),
                new MaxMinAntSystemTask(graph, antsScope, algorithmScope),
                new RankedAntSystem(graph, antsScope, algorithmScope)));

        for(var algorithm : algorithms){
            algorithm.setMaxTimeSec(0.2);
            algorithm.perform();
            Assertions.assertEquals(4, algorithm.getResult(), 0.01);
        }
    }
}