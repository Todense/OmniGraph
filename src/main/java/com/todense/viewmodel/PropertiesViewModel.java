package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.graph.GraphAnalyzer;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.ServiceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.inject.Inject;

public class PropertiesViewModel implements ViewModel {

    private StringProperty orderProperty = new SimpleStringProperty();
    private StringProperty sizeProperty = new SimpleStringProperty();
    private StringProperty componentsProperty = new SimpleStringProperty();
    private StringProperty maxDegreeProperty = new SimpleStringProperty();
    private StringProperty minDegreeProperty = new SimpleStringProperty();
    private StringProperty avgDegreeProperty = new SimpleStringProperty();
    private StringProperty diameterProperty = new SimpleStringProperty();
    private StringProperty radiusProperty = new SimpleStringProperty();

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    ServiceScope serviceScope;

    @Inject
    NotificationCenter notificationCenter;

    private Thread propertiesThread = new Thread();


    private void calculate() {
        Graph graph = graphScope.getGraphManager().getGraph();
        int size = graph.getNodes().size();
        int order = graph.getEdges().size();
        int minDegree = GraphAnalyzer.calculateMinDegree(graph);
        int maxDegree = GraphAnalyzer.calculateMaxDegree(graph);
        double avgDegree = GraphAnalyzer.calculateAvgDegree(graph);
        int componentCount = GraphAnalyzer.getComponentCount(graph);
        int[] eccentricityBounds = GraphAnalyzer.calculateEccentricities(graph);

        Platform.runLater(() -> {
            sizeProperty.setValue(validText(size));
            orderProperty.setValue(validText(order));
            minDegreeProperty.setValue(validText(minDegree));
            maxDegreeProperty.setValue(validText(maxDegree));
            avgDegreeProperty.setValue(validText(avgDegree));
            componentsProperty.setValue(validText(componentCount));
            radiusProperty.setValue(validText(eccentricityBounds[0]));
            diameterProperty.setValue(validText(eccentricityBounds[1]));
        });
    }

    public void start(){
        if(!propertiesThread.isAlive()){
            notificationCenter.publish(MainViewModel.threadStarted, "Calculating graph properties...");
            propertiesThread = new Thread(() -> {
                calculate();
                notificationCenter.publish(MainViewModel.threadFinished, "Calculated Properties");
            });
            serviceScope.setThread(propertiesThread);
            propertiesThread.start();
        }
    }

    public void stop(){
        if(propertiesThread.isAlive()){
            propertiesThread.interrupt();
        }
    }

    private String validText(int i){
        if(i == -1) return "inf";
        else if (i == -2) return "";
        else return String.valueOf(i);
    }

    private String validText(double d){
        if(d < 0) return "";
        else return String.format("%.2f", d);
    }

    public StringProperty orderProperty() {
        return orderProperty;
    }

    public StringProperty sizeProperty() {
        return sizeProperty;
    }

    public StringProperty componentsProperty() {
        return componentsProperty;
    }

    public StringProperty maxDegreeProperty() {
        return maxDegreeProperty;
    }

    public StringProperty minDegreeProperty() {
        return minDegreeProperty;
    }

    public StringProperty avgDegreeProperty() {
        return avgDegreeProperty;
    }

    public StringProperty diameterProperty() {
        return diameterProperty;
    }

    public StringProperty radiusProperty() {
        return radiusProperty;
    }
}
