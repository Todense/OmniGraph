package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.ants.*;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.layers.AntsDrawLayer;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AntsViewModel implements ViewModel {

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    AntsScope antsScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    TaskScope taskScope;

    @Inject
    NotificationCenter notificationCenter;

    private AntColonyAlgorithmTask algorithmTask;

    DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");

    private long startTime;

    public void initialize(){
        AntsDrawLayer antsDrawLayer = new AntsDrawLayer(antsScope, graphScope);
        Platform.runLater(() -> canvasScope.getPainter().addDrawLayer(antsDrawLayer));
    }

    public void startAlgorithm(){
        AlgorithmTask currentTask = taskScope.getTask();

        if(currentTask != null && currentTask.isRunning()) return;

        Graph graph = graphScope.getGraphManager().getGraph();

        if(graph.getNodes().size() < 3) return;

        graphScope.getGraphManager().createCompleteGraph();
        startTime = System.currentTimeMillis();
        notificationCenter.publish(MainViewModel.TASK_STARTED, algorithmProperty().get().toString());
        graphScope.displayModeProperty().set(DisplayMode.ANT_COLONY);

        switch (antsScope.algorithmProperty().get()){
            case ACS:
                algorithmTask = new AntColonySystemTask(graph, antsScope);
                break;
            case AS:
                algorithmTask = new AntSystemTask(graph, antsScope);
                break;
            case MMAS:
                algorithmTask = new MaxMinAntSystemTask(graph, antsScope);
                break;
            case RANK_AS:
                algorithmTask = new RankedAntSystem(graph, antsScope);
                break;
        }
        this.algorithmTask.setPainter(canvasScope.getPainter());
        this.algorithmTask.bestSolutionLengthProperty().addListener((obs, oldVal, newVal) ->
                notificationCenter.publish(MainViewModel.WRITE,
                        "Best Length: " + String.format("%.2f", newVal.doubleValue()) +
                                " found in "+ dateFormat.format(System.currentTimeMillis()-startTime)+
                                " after "+ algorithmTask.getIterationCounter()+ " iterations"));
        taskScope.setTask(this.algorithmTask);

        EventHandler<WorkerStateEvent> finishHandler = workerStateEvent ->
                notificationCenter.publish(MainViewModel.TASK_FINISHED,
                algorithmProperty().get().toString(),
                System.currentTimeMillis() - this.algorithmTask.getStartTime(),
                "");

        this.algorithmTask.setOnSucceeded(finishHandler);
        this.algorithmTask.setOnCancelled(finishHandler);
        this.algorithmTask.run();
    }

    public void stopAlgorithm(){
        if(algorithmTask != null && algorithmTask.isRunning()){
            algorithmTask.cancel();
        }
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

    public DoubleProperty q0Property() {
        return antsScope.exploitationStrengthProperty();
    }

    public DoubleProperty ksiProperty() {
        return antsScope.localEvaporationProperty();
    }

    public DoubleProperty scaleProperty() {
        return antsScope.scaleProperty();
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
