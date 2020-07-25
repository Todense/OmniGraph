package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.NodeArrangement;
import com.todense.viewmodel.random.RandomGraphGenerator;
import com.todense.viewmodel.scope.CanvasScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

import javax.inject.Inject;

public class RandomGeneratorViewModel implements ViewModel {

    private ObjectProperty<Generator> generatorProperty = new SimpleObjectProperty<>();
    private ObjectProperty<NodeArrangement> nodeArrangementProperty = new SimpleObjectProperty<>();

    private ObjectProperty<Integer> nodeCountObjectProperty = new SimpleObjectProperty<>(50);
    private IntegerProperty nodeCountProperty = IntegerProperty.integerProperty(nodeCountObjectProperty);

    private ObjectProperty<Double> minNodeDistObjectProperty = new SimpleObjectProperty<>(0.1);
    private DoubleProperty minNodeDistProperty = DoubleProperty.doubleProperty(minNodeDistObjectProperty);

    private ObjectProperty<Double> doubleParameterObjectProperty = new SimpleObjectProperty<>(0.18);

    private DoubleProperty doubleParameterProperty = DoubleProperty.doubleProperty(doubleParameterObjectProperty);

    private ObjectProperty<Integer> intParameter1ObjectProperty = new SimpleObjectProperty<>(2);
    private IntegerProperty intParameter1Property = IntegerProperty.integerProperty(intParameter1ObjectProperty);

    private ObjectProperty<Integer> intParameter2ObjectProperty = new SimpleObjectProperty<>(2);
    private IntegerProperty intParameter2Property = IntegerProperty.integerProperty(intParameter2ObjectProperty);
    
    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    CanvasScope canvasScope;

    public void initialize(){
        notificationCenter.subscribe("RANDOM", (key, payload) -> generate());
    }

    private void generateGraph(){

        double height = canvasScope.getCanvasHeight() * 0.9;
        Point2D canvasCenter = new Point2D(canvasScope.getCanvasWidth()/2, canvasScope.getCanvasHeight()/2);

        Graph randomGraph = new Graph("RandomGraph0", false);

        switch (nodeArrangementProperty.get()){
            case CIRCLE:
                RandomGraphGenerator.generateNodesCircle(
                        nodeCountProperty.get(),
                        height/2,
                        canvasCenter,
                        randomGraph
                );
                break;
            case MIN_DIST:
                boolean minDistAccepted = RandomGraphGenerator.generateNodesMinDist(
                        nodeCountProperty.get(),
                        minNodeDistProperty.get(),
                        height,
                        canvasCenter,
                        randomGraph
                );
                if(!minDistAccepted){
                    notificationCenter.publish(MainViewModel.threadFinished, "Minimum node distance is too high!");
                    return;
                }
                break;
            case RANDOM:
                RandomGraphGenerator.generateNodesRandom(
                        nodeCountProperty.get(),
                        height,
                        canvasCenter,
                        randomGraph
                );
                break;
        }

        switch (generatorProperty.get()){
            case GEOMETRIC:
                RandomGraphGenerator.generateGeometric(
                        doubleParameterProperty.get(),
                        false,
                        height,
                        randomGraph
                );
                break;
            case GEOMETRIC_RANDOMIZED:
                RandomGraphGenerator.generateGeometric(
                        doubleParameterProperty.get(),
                        true,
                        height,
                        randomGraph
                );
                break;
            case ERDOS_RENYI:
                RandomGraphGenerator.generateErdosRenyi(
                        doubleParameterProperty.get(),
                        randomGraph
                );
                break;
            case BARABASI_ALBERT:
                RandomGraphGenerator.generateBarabasiAlbert(
                        intParameter1Property.get(),
                        intParameter2Property.get(),
                        randomGraph
                );
                break;
        }

        notificationCenter.publish(GraphViewModel.newGraphRequest, randomGraph);
        notificationCenter.publish(MainViewModel.threadFinished, "Random graph generated");
    }

    public void generate(){
        notificationCenter.publish(MainViewModel.threadStarted, "Generating random graph...");
        Thread thread = new Thread(this::generateGraph);
        thread.start();
    }

    public ObjectProperty<Generator> generatorProperty() {
        return generatorProperty;
    }

    public ObjectProperty<NodeArrangement> nodeArrangementProperty() {
        return nodeArrangementProperty;
    }

    public ObjectProperty<Integer> nodeCountObjectProperty() {
        return nodeCountObjectProperty;
    }

    public ObjectProperty<Double> minNodeDistObjectProperty() {
        return minNodeDistObjectProperty;
    }

    public ObjectProperty<Double> doubleParameterObjectProperty() {
        return doubleParameterObjectProperty;
    }

    public ObjectProperty<Integer> intParameter1ObjectProperty() {
        return intParameter1ObjectProperty;
    }

    public ObjectProperty<Integer> intParameter2ObjectProperty() {
        return intParameter2ObjectProperty;
    }

}
