package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.file.FileManager;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.*;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@ScopeProvider(scopes = {AlgorithmScope.class, GraphScope.class, BackgroundScope.class, CanvasScope.class, AnimationScope.class, KeysScope.class, AntsScope.class, ServiceScope.class, InputScope.class})
public class MainViewModel implements ViewModel {

    public final static String serviceStarted = "SERVICE_STARTED";
    public final static String serviceFinished = "SERVICE_FINISHED";
    public final static String serviceCancelled = "SERVICE_CANCELLED";
    public final static String threadStarted = "THREAD_STARTED";
    public final static String threadFinished = "THREAD_FINISHED";
    public final static String graphEditRequest = "EDIT";

    private ObjectProperty<String> textProperty = new SimpleObjectProperty<>("");
    private ObjectProperty<String> infoTextProperty = new SimpleObjectProperty<>();
    private BooleanProperty workingProperty = new SimpleBooleanProperty(false);
    private BooleanProperty serviceRunningProperty = new SimpleBooleanProperty(false);
    private BooleanProperty editManuallyLockedProperty = new SimpleBooleanProperty(false);
    private BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);

    private DateFormat durationFormatter = new SimpleDateFormat("mm:ss:SSS");
    private DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    KeysScope keysScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    ServiceScope serviceScope;

    @InjectScope
    InputScope inputScope;

    private FileManager fileManager;
    private GraphManager graphManager;

    public void initialize(){

        fileManager = new FileManager();
        fileManager.errorTextProperty().addListener((obs, oldVal, newVal) -> write("Error: "+newVal));

        graphManager = graphScope.getGraphManager();

        notificationCenter.subscribe("WRITE", (key, payload) -> write((String) payload[0]));

        notificationCenter.subscribe(serviceCancelled, (key, payload) ->{
            writeInfo("");
            write(payload[0]+ " cancelled");
            workingProperty.set(false);
            serviceRunningProperty.set(false);
        });

        notificationCenter.subscribe(serviceStarted, (key, payload) -> {
            String serviceName = (String) payload[0];
            write(serviceName + " started");
            writeInfo("Running: "+ payload[0]);
            workingProperty.set(true);
            serviceRunningProperty.set(true);
        });

        notificationCenter.subscribe(serviceFinished, (key, payload) -> { //payload = name, duration, result
            write(payload[0] + " finished in "+ durationFormatter.format(payload[1]));
            if(!((String) payload[2]).isEmpty()){
                write((String) payload[2]); // result message
            }
            writeInfo("");
            workingProperty.set(false);
            serviceRunningProperty.set(false);
        });

        notificationCenter.subscribe(threadStarted, (key, payload) -> {
            stop();
            writeInfo((String) payload[0]);
            workingProperty.set(true);
        });

        notificationCenter.subscribe(threadFinished, (key, payload) -> {
            write((String) payload[0]);
            writeInfo("");
            workingProperty.set(false);
        });

        notificationCenter.subscribe(graphEditRequest, (key, payload) -> {
            if(!workingProperty.get()) {
                Runnable runnable = (Runnable) payload[0];
                runnable.run();
            }
        });


        editLockedProperty.bind(Bindings.createBooleanBinding(
                () -> editManuallyLockedProperty.get() || workingProperty.get(),
                editManuallyLockedProperty,
                workingProperty
        ));

        inputScope.editLockedProperty().bind(editLockedProperty);

    }

    public void saveGraph()  {
        fileManager.saveGraph(graphScope.getGraphManager().getGraph());

    }

    public void openGraph() {
        Graph openedGraph = fileManager.openGraph();
        if(openedGraph != null){
            notificationCenter.publish(GraphViewModel.newGraphRequest, openedGraph);
            notificationCenter.publish(CanvasViewModel.repaintRequest);
        }
    }

    public void stop() {
        serviceScope.stop();
    }

    public void setKeyInput(Scene scene){
        scene.setOnKeyPressed(keyEvent -> {
            keysScope.getPressedKeys().add(keyEvent.getCode());
            keyEvent.consume();

            if(keysScope.getPressedKeys().contains(KeyCode.CONTROL)) {
                if (keyEvent.getCode() == KeyCode.R) {
                    notificationCenter.publish("RANDOM");
                } else if (keyEvent.getCode() == KeyCode.DELETE) {
                    deleteGraph();
                } else if (keyEvent.getCode() == KeyCode.P) {
                    notificationCenter.publish(graphEditRequest, (Runnable)() -> graphManager.createPath());
                } else if (keyEvent.getCode() == KeyCode.K) {
                    notificationCenter.publish(graphEditRequest, (Runnable)() -> graphManager.createCompleteGraph());
                } else if(keyEvent.getCode() == KeyCode.L){
                    notificationCenter.publish("LAYOUT");
                } else if(keyEvent.getCode() == KeyCode.Q){
                    adjustCameraToGraph();
                }
            }
        });

        scene.setOnKeyReleased(keyEvent -> keysScope.getPressedKeys().remove(keyEvent.getCode()));

        fileManager.setStage((Stage) scene.getWindow());
    }

    public void write(String s){
        Platform.runLater(() ->
                textProperty.setValue(textProperty.get()
                        + "\n"+"["+timeFormatter.format(System.currentTimeMillis())+"]"+" "+s));
    }

    public void writeInfo(String s){
        Platform.runLater(() -> infoTextProperty.setValue(s));
    }

    public void deleteGraph() {
        notificationCenter.publish(graphEditRequest, (Runnable)() -> {
            graphManager.clearGraph();
            notificationCenter.publish("RESET");
        });
    }

    public void resetGraph(){
        notificationCenter.publish(graphEditRequest, (Runnable)() -> {
            graphManager.resetGraph();
            notificationCenter.publish("RESET");
        });
    }

    public void adjustCameraToGraph() {
        notificationCenter.publish("ADJUST");
        notificationCenter.publish(CanvasViewModel.repaintRequest);
    }

    public ObjectProperty<String> textProperty() {
        return textProperty;
    }

    public ObjectProperty<String> infoTextProperty() {
        return infoTextProperty;
    }

    public BooleanProperty workingProperty() {
        return workingProperty;
    }

    public boolean isEditManuallyLocked() {
        return editManuallyLockedProperty.get();
    }

    public BooleanProperty editManuallyLockedProperty() {
        return editManuallyLockedProperty;
    }

    public BooleanProperty editLockedProperty() {
        return editLockedProperty;
    }

    public BooleanProperty serviceRunningProperty() {
        return serviceRunningProperty;
    }
}
