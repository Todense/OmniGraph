package com.todense.viewmodel;

import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.algorithm.AlgorithmTaskManager;
import com.todense.viewmodel.algorithm.task.ForceDirectedLayoutTask;
import com.todense.viewmodel.layout.LongRangeForce;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.arrangement.generators.RandomCirclePointGenerator;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.*;
import javafx.geometry.Point2D;

import javax.inject.Inject;
import java.util.HashMap;

public class LayoutViewModel extends AlgorithmTaskManager implements ViewModel {

    private IntegerProperty optDistProperty = new SimpleIntegerProperty(30);
    private DoubleProperty smoothnessProperty = new SimpleDoubleProperty(0.9);
    private DoubleProperty stepProperty = new SimpleDoubleProperty(5d);
    private DoubleProperty toleranceProperty = new SimpleDoubleProperty(0.01);
    private BooleanProperty coolingOnProperty = new SimpleBooleanProperty(true);
    private BooleanProperty barnesHutOnProperty = new SimpleBooleanProperty(true);
    private BooleanProperty smoothnessOnProperty = new SimpleBooleanProperty(false);
    private DoubleProperty coolingStrengthProperty = new SimpleDoubleProperty(0.02);
    private BooleanProperty centerPullOnProperty = new SimpleBooleanProperty(true);
    private BooleanProperty multilevelOnProperty = new SimpleBooleanProperty(false);
    private DoubleProperty centerPullStrengthProperty = new SimpleDoubleProperty(0.1d);
    private ObjectProperty<LongRangeForce> longRangeForceProperty = new SimpleObjectProperty<>();

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    TaskScope taskScope;

    @Inject
    NotificationCenter notificationCenter;

    private HashMap<Node, Point2D> nodeSmoothedPositionMap;


    public void initialize(){
        notificationCenter.subscribe("LAYOUT", (key, payload) -> super.startTask());
        super.initialize(taskScope, canvasScope, notificationCenter);
    }

    @Override
    public AlgorithmTask createAlgorithmTask() {
        return new ForceDirectedLayoutTask(graphScope.getGraphManager(), this, canvasScope.getCanvasCenter());
    }

    @Override
    public void startTask() {
        nodeSmoothedPositionMap = new HashMap<>();
        graphScope.setNodePositionFunction(node -> nodeSmoothedPositionMap.get(node) != null ? nodeSmoothedPositionMap.get(node) : node.getPos());
        super.startTask();
    }

    public void randomLayout() {
        double height = canvasScope.getCanvasHeight() * 0.9;
        Point2D canvasCenter = new Point2D(canvasScope.getCanvasWidth()/2, canvasScope.getCanvasHeight()/2);
        Generator<Point2D> generator = new RandomCirclePointGenerator(height/2, canvasCenter);
        for(Node n: graphScope.getGraphManager().getGraph().getNodes()){
            n.setPos(generator.next());
        }
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public int getOptDist() {
        return optDistProperty.get();
    }

    public IntegerProperty optDistProperty() {
        return optDistProperty;
    }

    public double getStep() {
        return stepProperty.get();
    }

    public DoubleProperty stepProperty() {
        return stepProperty;
    }

    public double getTolerance() {
        return toleranceProperty.get();
    }

    public DoubleProperty toleranceProperty() {
        return toleranceProperty;
    }

    public boolean isCoolingOn() {
        return coolingOnProperty.get();
    }

    public BooleanProperty coolingOnProperty() {
        return coolingOnProperty;
    }

    public double getCoolingStrength() {
        return coolingStrengthProperty.get();
    }

    public DoubleProperty coolingStrengthProperty() {
        return coolingStrengthProperty;
    }

    public boolean isCenterPull() {
        return centerPullOnProperty.get();
    }

    public BooleanProperty centerPullOnProperty() {
        return centerPullOnProperty;
    }

    public double getCenterPullStrength() {
        return centerPullStrengthProperty.get();
    }

    public DoubleProperty centerPullStrengthProperty() {
        return centerPullStrengthProperty;
    }

    public boolean isMultilevelOn() {
        return multilevelOnProperty.get();
    }

    public BooleanProperty multilevelOnProperty() {
        return multilevelOnProperty;
    }

    public boolean isBarnesHutOn() {
        return barnesHutOnProperty.get();
    }

    public BooleanProperty barnesHutOnProperty() {
        return barnesHutOnProperty;
    }

    public LongRangeForce getLongRangeForce() {
        return longRangeForceProperty.get();
    }

    public ObjectProperty<LongRangeForce> longRangeForceProperty() {
        return longRangeForceProperty;
    }

    public double getSmoothness() {
        return smoothnessProperty.get();
    }

    public DoubleProperty smoothnessProperty() {
        return smoothnessProperty;
    }

    public boolean isSmoothnessOn() {
        return smoothnessOnProperty.get();
    }

    public BooleanProperty smoothnessOnProperty() {
        return smoothnessOnProperty;
    }

    public HashMap<Node, Point2D> getNodeSmoothedPositionMap() {
        return nodeSmoothedPositionMap;
    }

}
