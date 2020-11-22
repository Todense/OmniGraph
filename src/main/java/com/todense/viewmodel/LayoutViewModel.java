package com.todense.viewmodel;

import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.algorithm.task.ForceDirectedLayoutTask;
import com.todense.viewmodel.layout.LongRangeForce;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;

import javax.inject.Inject;

public class LayoutViewModel implements ViewModel {

    private IntegerProperty optDistProperty = new SimpleIntegerProperty(30);
    private DoubleProperty stepProperty = new SimpleDoubleProperty(5d);
    private DoubleProperty toleranceProperty = new SimpleDoubleProperty(0.01);
    private BooleanProperty coolingOnProperty = new SimpleBooleanProperty(true);
    private BooleanProperty barnesHutOnProperty = new SimpleBooleanProperty(true);
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

    private AlgorithmTask task;

    public void initialize(){
        notificationCenter.subscribe("LAYOUT", (key, payload) -> start());
    }

    public void start(){
        AlgorithmTask currentTask = taskScope.getTask();

        if(currentTask != null && currentTask.isRunning()) return;

        task = new ForceDirectedLayoutTask(graphScope.getGraphManager(),
                this,new Point2D(canvasScope.getCanvasWidth()/2,
                canvasScope.getCanvasHeight()/2));
        task.setPainter(canvasScope.getPainter());
        taskScope.setTask(task);

        EventHandler<WorkerStateEvent> handler = workerStateEvent -> {
            notificationCenter.publish(MainViewModel.TASK_FINISHED,
                    "Force-Directed Layout",
                    System.currentTimeMillis() - task.getStartTime(),
                    "");
        };

        task.setOnSucceeded(handler);
        task.setOnCancelled(handler);
        notificationCenter.publish(MainViewModel.TASK_STARTED, "Force-Directed Layout");
        new Thread(task).start();
    }

    public void stop(){
        if(task != null && task.isRunning()){
            task.cancel();
        }
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
}
