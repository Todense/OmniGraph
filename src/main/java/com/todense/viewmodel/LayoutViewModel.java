package com.todense.viewmodel;

import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.algorithm.service.DynamicLayoutService;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.ServiceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;

import javax.inject.Inject;

public class LayoutViewModel implements ViewModel {

    private IntegerProperty optDistProperty = new SimpleIntegerProperty(50);
    private DoubleProperty stepProperty = new SimpleDoubleProperty(3d);
    private DoubleProperty toleranceProperty = new SimpleDoubleProperty(0.1);
    private BooleanProperty coolingOnProperty = new SimpleBooleanProperty(true);
    private DoubleProperty coolingStrengthProperty = new SimpleDoubleProperty(0.02);
    private BooleanProperty pullingOnProperty = new SimpleBooleanProperty(true);
    private BooleanProperty multilevelOnProperty = new SimpleBooleanProperty(true);
    private DoubleProperty pullingStrengthProperty = new SimpleDoubleProperty(3d);


    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    ServiceScope serviceScope;

    @Inject
    NotificationCenter notificationCenter;

    private AlgorithmService service;

    public void initialize(){
        notificationCenter.subscribe("LAYOUT", (key, payload) -> start());
    }

    public void start(){
        AlgorithmService currentService = serviceScope.getService();

        if(currentService != null && currentService.isRunning()) return;

        service = new DynamicLayoutService(graphScope.getGraphManager(),
                this,new Point2D(canvasScope.getCanvasWidth()/2,
                canvasScope.getCanvasHeight()/2));
        service.setPainter(canvasScope.getPainter());
        serviceScope.setService(service);

        EventHandler<WorkerStateEvent> handler = workerStateEvent -> {
            notificationCenter.publish(MainViewModel.serviceFinished,
                    "Force-Directed Layout",
                    System.currentTimeMillis() - service.getStartTime(),
                    "");
        };

        service.setOnSucceeded(handler);
        service.setOnCancelled(handler);
        notificationCenter.publish(MainViewModel.serviceStarted, "Force-Directed Layout");
        service.start();
    }

    public void stop(){
        if(service != null && service.isRunning()){
            service.cancel();
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

    public boolean isPullingOn() {
        return pullingOnProperty.get();
    }

    public BooleanProperty pullingOnProperty() {
        return pullingOnProperty;
    }

    public double getPullStrength() {
        return pullingStrengthProperty.get();
    }

    public DoubleProperty pullingStrengthProperty() {
        return pullingStrengthProperty;
    }

    public boolean isMultilevelOn() {
        return multilevelOnProperty.get();
    }

    public BooleanProperty multilevelOnProperty() {
        return multilevelOnProperty;
    }
}
