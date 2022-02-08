package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.file.GraphReader;
import com.todense.viewmodel.file.format.graphml.GraphMLReader;
import com.todense.viewmodel.file.format.mtx.MtxReader;
import com.todense.viewmodel.file.format.ogr.OgrReader;
import com.todense.viewmodel.file.format.tsp.TspReader;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.graph.GraphOperation;
import com.todense.viewmodel.layout.LayoutTask;
import com.todense.viewmodel.layout.task.AutoD3LayoutTask;
import com.todense.viewmodel.scope.*;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
        AnimationScope.class, AntsScope.class, TaskScope.class,
        InputScope.class, LayoutScope.class, FileScope.class})
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
    private final ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>(new Point2D(0,0));
    private final BooleanProperty workingProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty taskRunningProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty layoutRunningProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty manualEditLockProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty autoEditLockProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty autoLayoutOnProperty = new SimpleBooleanProperty(false);

    private final DoubleProperty leftPanelWidthProperty = new SimpleDoubleProperty();
    private final DoubleProperty rightPanelWidthProperty = new SimpleDoubleProperty();

    private final DateFormat durationFormatter = new SimpleDateFormat("mm:ss:SSS");
    private final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    private AutoD3LayoutTask autoLayout;


    @Inject
    NotificationCenter notificationCenter;

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

    @InjectScope
    AnimationScope animationScope;

    @InjectScope
    FileScope fileScope;

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
            layoutRunningProperty.set(false);
        });

        notificationCenter.subscribe(TASK_STARTED, (key, payload) -> {
            AlgorithmTask task = (AlgorithmTask) payload[0];
            onTaskStarted(task);
        });

        notificationCenter.subscribe(TASK_FINISHED, (key, payload) -> { //payload = name, duration, result
            String name = (String) payload[0];
            long duration = (long) payload[1];
            String result = (String) payload[2];

            onTaskFinished(name, duration, result);
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
            if(!taskRunningProperty.get()) {
                GraphOperation operation = ((GraphOperation) payload[0]);
                graphManager.performOperation(operation);
                canvasScope.getPainter().repaint();
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
                canvasScope.getPainter().stopAnimationTimer();
            }
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> stopAutoLayout());

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_SET, (key, payload) -> {
            if(isAutoLayoutOn()) {
                stopAutoLayout();
                startAutoLayout();
            }
        });

        inputScope.editLockedProperty().bind(editLockedProperty);
        mousePositionProperty.bind(inputScope.mousePositionProperty());

        // unlock graph operations when layout is paused
        animationScope.pausedProperty().addListener((obs, oldVal, newVal) ->{
            if(!taskRunningProperty.get())
                graphManager.setQueueGraphOperationsOn(!newVal);
        });

        animationScope.animatedProperty().addListener((obs, oldVal, newVel) -> canvasScope.getPainter().repaint());
        appColorProperty.addListener((obs, oldVal, newVel) -> canvasScope.getPainter().repaint());
    }

    private void onTaskStarted(AlgorithmTask task){
        String taskName = task.getAlgorithmName();
        write(taskName + " started");
        writeInfo("Running: "+ taskName);
        workingProperty.set(true);
        if (!(task instanceof LayoutTask)){
            autoEditLockProperty.set(true);
        } else{
            layoutRunningProperty.set(true);
        }

        autoLayoutOnProperty.set(false);
        taskRunningProperty.set(true);
        graphManager.resetGraph();
    }

    private void onTaskFinished(String name, long duration, String result){
        if(name != null){
            write(name + " finished in "+ durationFormatter.format(duration));
        }
        if(!result.isEmpty()){
            write(result); // result message
        }
        writeInfo("");
        workingProperty.set(false);
        autoEditLockProperty.set(false);
        taskRunningProperty.set(false);
        layoutRunningProperty.set(false);
    }

    private void startAutoLayout(){
        autoLayout = new AutoD3LayoutTask(layoutScope, graphManager);
        autoLayout.setPainter(canvasScope.getPainter());
        Thread thread = new Thread(autoLayout);
        thread.start();
        layoutRunningProperty.set(true);
    }

    private void stopAutoLayout(){
        if (autoLayout != null) {
            autoLayout.cancel();
            layoutRunningProperty.set(false);
        }
    }

    public void nextStep() {
        animationScope.nextStepProperty().setValue(true);
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
            inputScope.getPressedKeys().add(keyEvent.getCode());
            keyEvent.consume();

            if(inputScope.getPressedKeys().contains(KeyCode.CONTROL)) {
                if (keyEvent.getCode() == KeyCode.R) {
                    resetGraph();
                } else if (keyEvent.getCode() == KeyCode.DELETE) {
                    deleteGraph();
                } else if (keyEvent.getCode() == KeyCode.P) {
                    notificationCenter.publish(GRAPH_EDIT_REQUEST, (GraphOperation)() -> graphManager.createPath());
                } else if (keyEvent.getCode() == KeyCode.K) {
                    notificationCenter.publish(GRAPH_EDIT_REQUEST, (GraphOperation)() -> graphManager.createCompleteGraph());
                } else if(keyEvent.getCode() == KeyCode.L){
                    notificationCenter.publish(LayoutViewModel.LAYOUT_START);
                } else if(keyEvent.getCode() == KeyCode.J){
                    adjustCameraToGraph();
                }  else if(keyEvent.getCode() == KeyCode.E){
                    notificationCenter.publish("PRESET");
                } else if(keyEvent.getCode() == KeyCode.Q){
                    notificationCenter.publish("RANDOM");
                } else if(keyEvent.getCode() == KeyCode.SPACE){
                    animationScope.setPaused(!animationScope.isPaused());
                } else if(keyEvent.getCode() == KeyCode.RIGHT){
                    animationScope.nextStepProperty().set(true);
                } else if(keyEvent.getCode() == KeyCode.BACK_SPACE){
                    stopAll();
                }
            }
        });

        scene.setOnKeyReleased(keyEvent -> inputScope.getPressedKeys().remove(keyEvent.getCode()));
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
        notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, new Graph());
    }

    public void resetGraph(){
        if(!taskRunningProperty.get()){
            graphManager.resetGraph();
            notificationCenter.publish(MainViewModel.RESET);
            notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        }

    }

    public void adjustCameraToGraph() {
        notificationCenter.publish("ADJUST", leftPanelWidthProperty.get(), rightPanelWidthProperty.get());
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

    public DoubleProperty leftPanelWidthProperty() {
        return leftPanelWidthProperty;
    }

    public DoubleProperty rightPanelWidthProperty() {
        return rightPanelWidthProperty;
    }

    public BooleanProperty eraseModeOnProperty(){
        return inputScope.eraseModeOnProperty();
    }

    public ObjectProperty<Point2D> mousePositionProperty() {
        return mousePositionProperty;
    }

    public IntegerProperty stepTimeProperty() {
        return animationScope.stepTimeProperty();
    }

    public BooleanProperty animatedProperty() {
        return animationScope.animatedProperty();
    }

    public BooleanProperty pausedProperty() {
        return animationScope.pausedProperty();
    }

    public BooleanProperty nextStepProperty() {
        return animationScope.nextStepProperty();
    }

    public LayoutScope getLayoutScope() {
        return layoutScope;
    }

    public FileScope getFileScope() {
        return fileScope;
    }

    public BooleanProperty layoutRunningProperty() {
        return layoutRunningProperty;
    }
}
