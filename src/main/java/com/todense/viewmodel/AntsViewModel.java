package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.algorithm.AlgorithmTaskManager;
import com.todense.viewmodel.ants.*;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.scope.*;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AntsViewModel extends AlgorithmTaskManager implements ViewModel {

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    AlgorithmScope algorithmScope;

    @InjectScope
    AntsScope antsScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    TaskScope taskScope;

    @Inject
    NotificationCenter notificationCenter;

    DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");

    public void initialize(){
        super.initialize(taskScope, canvasScope, notificationCenter);
    }

    @Override
    protected AntColonyAlgorithmTask createAlgorithmTask() {

        Graph graph = graphScope.getGraphManager().getGraph();

        if(graph.getNodes().size() < 3){
            throw new IllegalArgumentException("Graph must have at least 3 nodes");
        }

        graphScope.getGraphManager().createCompleteGraph();
        //notificationCenter.publish(MainViewModel.TASK_STARTED, algorithmProperty().get().toString());
        graphScope.displayModeProperty().set(DisplayMode.ANT_COLONY);

        AntColonyAlgorithmTask task = null;

        switch (antsScope.algorithmProperty().get()){
            case ACS:
                task = new AntColonySystemTask(graph, antsScope, algorithmScope);
                break;
            case AS:
                task = new AntSystemTask(graph, antsScope, algorithmScope);
                break;
            case MMAS:
                task = new MaxMinAntSystemTask(graph, antsScope, algorithmScope);
                break;
            case RANK_AS:
                task = new RankedAntSystem(graph, antsScope, algorithmScope);
                break;
        }

        task.setPainter(canvasScope.getPainter());
        AntColonyAlgorithmTask finalTask = task;
        task.bestSolutionLengthProperty().addListener((obs, oldVal, newVal) ->
                notificationCenter.publish(MainViewModel.WRITE,
                        "Best Length: " + String.format("%.2f", newVal.doubleValue()) +
                                " found in "+ dateFormat.format(System.currentTimeMillis() - super.getStartTime())+
                                " after "+ finalTask.getIterationCounter()+ " iterations"));

        return task;
    }


    public ObjectProperty<AntColonyVariant> algorithmProperty() {
        return antsScope.algorithmProperty();
    }

    public IntegerProperty antCountProperty() {
        return antsScope.antCountProperty();
    }

    public DoubleProperty alphaProperty() {
        return antsScope.alphaProperty();
    }

    public DoubleProperty betaProperty() {
        return antsScope.betaProperty();
    }

    public DoubleProperty evaporationProperty() {
        return antsScope.evaporationProperty();
    }

    public DoubleProperty exploitationStrengthProperty() {
        return antsScope.exploitationStrengthProperty();
    }

    public DoubleProperty localEvaporationProperty() {
        return antsScope.localEvaporationProperty();
    }

    public BooleanProperty with2OptProperty() {
        return antsScope.with2OptProperty();
    }

    public BooleanProperty with3OptProperty() {
        return antsScope.with3OptProperty();
    }

    public BooleanProperty antsAnimationOnProperty(){
        return antsScope.antsAnimationOnProperty();
    }

    public BooleanProperty showPheromonesProperty(){
        return antsScope.showPheromonesProperty();
    }

    public ObjectProperty<Color> cycleColorProperty() {
        return antsScope.cycleColorProperty();
    }

    public ObjectProperty<Color> antColorProperty() {
        return antsScope.antColorProperty();
    }

    public IntegerProperty neighbourhoodSizeProperty() {
        return antsScope.neighbourhoodSizeProperty();
    }

    public IntegerProperty rankSizeProperty() {
        return antsScope.rankSizeProperty();
    }
}
