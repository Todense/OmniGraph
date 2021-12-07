package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.layout.LayoutTask;
import com.todense.viewmodel.layout.task.AutoD3LayoutTask;
import com.todense.viewmodel.layout.task.D3LayoutTask;
import com.todense.viewmodel.file.GraphReader;
import com.todense.viewmodel.file.format.graphml.GraphMLReader;
import com.todense.viewmodel.file.format.mtx.MtxReader;
import com.todense.viewmodel.file.format.ogr.OgrReader;
import com.todense.viewmodel.file.format.tsp.TspReader;
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
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@ScopeProvider(scopes = {AlgorithmScope.class, GraphScope.class,
        BackgroundScope.class, CanvasScope.class,
        AnimationScope.class, KeysScope.class,
        AntsScope.class, TaskScope.class, InputScope.class, LayoutScope.class})
public class MainViewModel implements ViewModel {

    public final static String TASK_STARTED = "TASK_STARTED";
    public final static String TASK_FINISHED = "TASK_FINISHED";
    public final static String TASK_CANCELLED = "TASK_CANCELLED";
    public final static String THREAD_STARTED = "THREAD_STARTED";
    public final static String THREAD_FINISHED = "THREAD_FINISHED";
    public final static String GRAPH_EDIT_REQUEST = "GRAPH_EDIT_REQUEST";
    public final static String WRITE = "WRITE";
    public final static String RESET = "RESET";

    private final ObjectProperty<String> textProperty = new SimpleObjectProperty<>("");
    private final ObjectProperty<String> infoTextProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> appColorProperty = new SimpleObjectProperty<>(Color.rgb(55,85,125));
    private final BooleanProperty workingProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty taskRunningProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty manualEditLockProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty autoEditLockProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty autoLayoutOnProperty = new SimpleBooleanProperty(false);

    private final DateFormat durationFormatter = new SimpleDateFormat("mm:ss:SSS");
    private final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    private AutoD3LayoutTask autoLayout;


    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    KeysScope keysScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    TaskScope taskScope;

    @InjectScope
    InputScope inputScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    LayoutScope layoutScope;

    private GraphManager graphManager;

    public void initialize(){
        graphManager = graphScope.getGraphManager();

        notificationCenter.subscribe(MainViewModel.WRITE, (key, payload) -> write((String) payload[0]));

        notificationCenter.subscribe(TASK_CANCELLED, (key, payload) ->{
            writeInfo("");
            write(payload[0]+ " cancelled");
            workingProperty.set(false);
            autoEditLockProperty.set(false);
            taskRunningProperty.set(false);
        });

        notificationCenter.subscribe(TASK_STARTED, (key, payload) -> {
            AlgorithmTask task = (AlgorithmTask) payload[0];
            String taskName = task.getAlgorithmName();
            write(taskName + " started");
            writeInfo("Running: "+ taskName);
            workingProperty.set(true);
            if (!(task instanceof LayoutTask)){
                autoEditLockProperty.set(true);
            }

            autoLayoutOnProperty.set(false);
            taskRunningProperty.set(true);
            graphManager.resetGraph();
        });

        notificationCenter.subscribe(TASK_FINISHED, (key, payload) -> { //payload = name, duration, result
            write(payload[0] + " finished in "+ durationFormatter.format(payload[1]));
            if(!((String) payload[2]).isEmpty()){
                write((String) payload[2]); // result message
            }
            writeInfo("");
            workingProperty.set(false);
            autoEditLockProperty.set(false);
            taskRunningProperty.set(false);
        });

        notificationCenter.subscribe(THREAD_STARTED, (key, payload) -> {
            stopAll();
            writeInfo((String) payload[0]);
            workingProperty.set(true);
        });

        notificationCenter.subscribe(THREAD_FINISHED, (key, payload) -> {
            write((String) payload[0]);
            writeInfo("");
            workingProperty.set(false);
        });

        notificationCenter.subscribe(GRAPH_EDIT_REQUEST, (key, payload) -> {
            if(!editLockedProperty.get()) {
                Runnable runnable = (Runnable) payload[0];
                Thread thread = new Thread(runnable);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        });


        editLockedProperty.bind(Bindings.createBooleanBinding(
                () -> manualEditLockProperty.get() || autoEditLockProperty.get(),
                manualEditLockProperty,
                autoEditLockProperty
        ));

        autoLayoutOnProperty.addListener((obs, oldVal, newVal) -> {
            if(newVal){
                startAutoLayout();
            }else {
                stopAutoLayout();
            }
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> {
            stopAutoLayout();
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_SET, (key, payload) -> {
            if(isAutoLayoutOn()) {
                stopAutoLayout();
                startAutoLayout();
            }
        });

        inputScope.editLockedProperty().bind(editLockedProperty);
        canvasScope.borderColorProperty().bind(appColorProperty);

        appColorProperty.addListener((obs, oldVal, newVel) -> canvasScope.getPainter().repaint());
    }

    private void startAutoLayout(){
        autoLayout = new AutoD3LayoutTask(layoutScope, graphManager);
        autoLayout.setPainter(canvasScope.getPainter());
        Thread thread = new Thread(autoLayout);
        thread.start();
    }

    private void stopAutoLayout(){
        if (autoLayout != null) {
            autoLayout.cancel();
        }
    }

    public void openGraph(File file) {

        if(taskScope.getTask() != null && taskScope.getTask().isRunning())
            return;

        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        GraphReader graphReader = null;

        switch (extension){
            case "ogr": graphReader = new OgrReader(); break;
            case "tsp": graphReader = new TspReader(); break;
            case "graphml" : graphReader = new GraphMLReader(); break;
            case "mtx": graphReader = new MtxReader(
                    new Point2D(canvasScope.getCanvasWidth()/2, canvasScope.getCanvasHeight()/2),
                    canvasScope.getCanvasHeight() * 0.9); break;
        }
        assert graphReader != null;
        Graph openedGraph = null;
        try{
            openedGraph = graphReader.readGraph(file);
        } catch (RuntimeException e){
            if (e.getMessage() != null){
                notificationCenter.publish(MainViewModel.WRITE,"ERROR: "+e.getMessage());
            }else{
                notificationCenter.publish(MainViewModel.WRITE, "Error: File is corrupted");
            }

            e.printStackTrace();
        }
        if(openedGraph != null){
            notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, openedGraph);
            notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
            write("Graph opened");
        }
    }


    public void stopAll() {
        taskScope.stopTask();
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
                    notificationCenter.publish(GRAPH_EDIT_REQUEST, (Runnable)() -> graphManager.createPath());
                } else if (keyEvent.getCode() == KeyCode.K) {
                    notificationCenter.publish(GRAPH_EDIT_REQUEST, (Runnable)() -> graphManager.createCompleteGraph());
                } else if(keyEvent.getCode() == KeyCode.L){
                    notificationCenter.publish("LAYOUT");
                } else if(keyEvent.getCode() == KeyCode.Q){
                    adjustCameraToGraph();
                }  else if(keyEvent.getCode() == KeyCode.E){
                    notificationCenter.publish("PRESET");
                }
            }
        });

        scene.setOnKeyReleased(keyEvent -> keysScope.getPressedKeys().remove(keyEvent.getCode()));
    }

    public void write(String s){
        Platform.runLater(() -> {
            String message = "["+timeFormatter.format(System.currentTimeMillis())+"]"+" "+s;
            String text = textProperty.get() + "\n"+message;
            textProperty.setValue(text);
            System.out.println(message);
        });

    }

    public void writeInfo(String s){
        Platform.runLater(() -> infoTextProperty.setValue(s));
    }

    public void deleteGraph() {
        notificationCenter.publish(GRAPH_EDIT_REQUEST, (Runnable)() -> {
            graphManager.clearGraph();
        });
        notificationCenter.publish(GraphViewModel.NEW_GRAPH_SET);
    }

    public void resetGraph(){
        notificationCenter.publish(GRAPH_EDIT_REQUEST, (Runnable)() -> {
            graphManager.resetGraph();
            notificationCenter.publish(MainViewModel.RESET);
        });
    }

    public void adjustCameraToGraph() {
        notificationCenter.publish("ADJUST");
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
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

    public boolean isManualEditLockOn() {
        return manualEditLockProperty.get();
    }

    public BooleanProperty manualEditLockProperty() {
        return manualEditLockProperty;
    }

    public BooleanProperty editLockedProperty() {
        return editLockedProperty;
    }

    public boolean isAutoEditLockOn() {
        return autoEditLockProperty.get();
    }

    public BooleanProperty autoEditLockProperty() {
        return autoEditLockProperty;
    }

    public BooleanProperty taskRunningProperty() {
        return taskRunningProperty;
    }

    public Color getAppColor() {
        return appColorProperty.get();
    }

    public ObjectProperty<Color> appColorProperty() {
        return appColorProperty;
    }

    public boolean isAutoLayoutOn() {
        return autoLayoutOnProperty.get();
    }

    public BooleanProperty autoLayoutOnProperty() {
        return autoLayoutOnProperty;
    }

    public void setAutoLayoutOnProperty(boolean autoLayoutOnProperty) {
        this.autoLayoutOnProperty.set(autoLayoutOnProperty);
    }
}
