package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.ants.AntColonyService;
import com.todense.viewmodel.ants.AntColonyVariant;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.layers.AntsDrawLayer;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.ServiceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
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
    ServiceScope serviceScope;

    @Inject
    NotificationCenter notificationCenter;

    private AntColonyService service;

    DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");

    private long startTime;

    public void initialize(){
        AntsDrawLayer antsDrawLayer = new AntsDrawLayer(antsScope, graphScope);
        Platform.runLater(() -> canvasScope.getPainter().addDrawLayer(antsDrawLayer));
    }

    public void startAlgorithm(){
        AlgorithmService currentService = serviceScope.getService();

        if(currentService != null && currentService.isRunning()) return;

        Graph graph = graphScope.getGraphManager().getGraph();

        if(graph.getNodes().size() < 3) return;

        graphScope.getGraphManager().createCompleteGraph();
        startTime = System.currentTimeMillis();
        notificationCenter.publish(MainViewModel.serviceStarted, algorithmProperty().get().toString());
        graphScope.displayModeProperty().set(DisplayMode.ANT_COLONY);

        service = new AntColonyService(antsScope.algorithmProperty().get(), antsScope, graph);
        service.setPainter(canvasScope.getPainter());
        service.bgLengthProperty().addListener((obs, oldVal, newVal) ->
                notificationCenter.publish("WRITE",
                        "Best Length: " + String.format("%.2f", newVal.doubleValue()) +
                                " found in "+ dateFormat.format(System.currentTimeMillis()-startTime)));
        serviceScope.setService(service);

        EventHandler<WorkerStateEvent> finishHandler = workerStateEvent ->
                notificationCenter.publish(MainViewModel.serviceFinished,
                algorithmProperty().get().toString(),
                System.currentTimeMillis() - service.getStartTime(),
                "");

        service.setOnSucceeded(finishHandler);
        service.setOnCancelled(finishHandler);
        service.start();
    }

    public void stopAlgorithm(){
        if(service != null && service.isRunning()){
            service.cancel();
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
