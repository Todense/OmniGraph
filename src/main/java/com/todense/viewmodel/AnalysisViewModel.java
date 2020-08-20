package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphAnalyzer;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;

import java.util.HashMap;

public class AnalysisViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    private int minDegree;
    private int maxDegree;
    private double avgDegree;
    private int radius;
    private int diameter;
    private double density;
    private double minDegreePerc;
    private double maxDegreePerc;
    private double avgDegreePerc;
    private double radiusPerc;
    private double diameterPerc;
    HashMap<Integer, Integer> degreeDistribution;


    public void calculateAll(){
        Graph graph = graphScope.getGraphManager().getGraph();
        degreeDistribution = calcDegreeDistribution();
        minDegree = GraphAnalyzer.calculateMinDegree(graph);
        maxDegree = GraphAnalyzer.calculateMaxDegree(graph);
        avgDegree = GraphAnalyzer.calculateAvgDegree(graph);
        var ecc = GraphAnalyzer.calculateEccentricities(graph);
        radius = ecc[0];
        diameter = ecc[1];
        density = GraphAnalyzer.getDensity(graph);

        minDegreePerc  = (double)minDegree/(graph.getNodes().size()-1);
        maxDegreePerc  = (double)maxDegree/(graph.getNodes().size()-1);
        avgDegreePerc  = avgDegree/(graph.getNodes().size()-1);
        radiusPerc  = (double)radius/(graph.getNodes().size()-1);
        diameterPerc  = (double)diameter/(graph.getNodes().size()-1);
    }

    public HashMap<Integer, Integer> calcDegreeDistribution(){
        HashMap<Integer, Integer> distribution = new HashMap<>();
        Graph graph = graphScope.getGraphManager().getGraph();
        int maxDegree = 0;
        for(Node n : graph.getNodes()){
            int degree = n.getNeighbours().size();
            if(degree > maxDegree) maxDegree = degree;
            distribution.putIfAbsent(degree, 0);
            distribution.put(degree, distribution.get(degree)+1);
        }

        for (int i = 0; i < maxDegree; i++) {
            distribution.putIfAbsent(i, 0);
        }
        return distribution;
    }

    public int getMinDegree() {
        return minDegree;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public double getMinDegreePerc() {
        return minDegreePerc;
    }

    public double getMaxDegreePerc() {
        return maxDegreePerc;
    }

    public double getAvgDegree() {
        return avgDegree;
    }

    public double getAvgDegreePerc() {
        return avgDegreePerc;
    }

    public int getRadius() {
        return radius;
    }

    public int getDiameter() {
        return diameter;
    }

    public double getDensity() {
        return density;
    }

    public double getRadiusPerc() {
        return radiusPerc;
    }

    public double getDiameterPerc() {
        return diameterPerc;
    }

    public HashMap<Integer, Integer> getDegreeDistribution() {
        return degreeDistribution;
    }

}
