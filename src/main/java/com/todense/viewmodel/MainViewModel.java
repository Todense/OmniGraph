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
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@ScopeProvider(scopes = {AlgorithmScope.class, GraphScope.class,
        BackgroundScope.class, CanvasScope.class,
        AnimationScope.class, AntsScope.class, AlgorithmTaskScope.class,
        InputScope.class, LayoutScope.class, FileScope.class})
public class MainViewModel implements ViewModel {

    // algorithm management notification ids (manage "AlgorithmTask" tasks)
    public final static String ALGORITHM_STARTED = "ALGORITHM_STARTED";
    public final static String ALGORITHM_FINISHED = "ALGORITHM_FINISHED";
    public final static String ALGORITHM_CANCELLED = "ALGORITHM_CANCELLED";

    // task management notification ids
    public final static String TASK_STARTED = "TASK_STARTED";
    public final static String TASK_FINISHED = "TASK_FINISHED";

    // graph edit request notification ids - used for sending GraphOperation instances
    public final static String GRAPH_EDIT_REQUEST = "GRAPH_EDIT_REQUEST";
    public final static String WRITE = "WRITE";
    public final static String RESET = "RESET";

    // text properties
    private final ObjectProperty<String> eventTextProperty = new SimpleObjectProperty<>("");
    private final ObjectProperty<String> infoTextProperty = new SimpleObjectProperty<>();

    // application theme color
    private final ObjectProperty<Color> appColorProperty = new SimpleObjectProperty<>(Color.rgb(55,85,125));

    // current mouse position
    private final ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>(new Point2D(0,0));

    // indicates if any task is now running
    private final BooleanProperty workingProperty = new SimpleBooleanProperty(false);

    // indicates if any AlgorithmTask is now running
    private final BooleanProperty algorithmRunningProperty = new SimpleBooleanProperty(false);

    // indicates if any LayoutTask is now running
    private final BooleanProperty layoutRunningProperty = new SimpleBooleanProperty(false);

    // indicates if AutoD3Layout is now running
    private final BooleanProperty autoLayoutOnProperty = new SimpleBooleanProperty(false);

    // indicates if graph edition is locked by user
    private final BooleanProperty manualEditLockProperty = new SimpleBooleanProperty(false);

    // indicates if graph edition is locked by running task
    private final BooleanProperty autoEditLockProperty = new SimpleBooleanProperty(false);

    // indicates if graph edition is locked
    private final BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);

    // side panel widths
    private final DoubleProperty leftPanelWidthProperty = new SimpleDoubleProperty();
    private final DoubleProperty rightPanelWidthProperty = new SimpleDoubleProperty();

    // date formats
    private final DateFormat durationFormatter = new SimpleDateFormat("mm:ss:SSS");
    private final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    private AutoD3LayoutTask autoLayout;

    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    AlgorithmTaskScope algorithmTaskScope;

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

        // ---------NOTIFICATIONS----------

        // receive write notification
        notificationCenter.subscribe(MainViewModel.WRITE, (key, payload) -> writeEvent((String) payload[0]));

        // receive notification on algorithm cancelled
        notificationCenter.subscribe(ALGORITHM_CANCELLED, (key, payload) ->{
            // set info & event texts
            writeInfo("");
            writeEvent(payload[0]+ " cancelled");

            // set flags
            workingProperty.set(false);
            autoEditLockProperty.set(false);
            algorithmRunningProperty.set(false);
            layoutRunningProperty.set(false);
        });

        // receive notification on algorithm started
        notificationCenter.subscribe(ALGORITHM_STARTED, (key, payload) -> {
            AlgorithmTask task = (AlgorithmTask) payload[0];
            onAlgorithmStarted(task);
        });

        // receive notification on algorithm finished
        notificationCenter.subscribe(ALGORITHM_FINISHED, (key, payload) -> { //payload = name, duration, result
            String name = (String) payload[0];
            long duration = (long) payload[1];
            String result = (String) payload[2];

            onAlgorithmFinished(name, duration, result);
        });

        // receive notification on task started
        notificationCenter.subscribe(TASK_STARTED, (key, payload) -> {
            stopCurrentAlgorithm();
            writeInfo((String) payload[0]);
            workingProperty.set(true);
        });

        // receive notification on task finished
        notificationCenter.subscribe(TASK_FINISHED, (key, payload) -> {
            writeEvent((String) payload[0]);
            writeInfo("");
            workingProperty.set(false);
        });

        // receive notification on graph edit request - perform received GraphOperation and repaint
        notificationCenter.subscribe(GRAPH_EDIT_REQUEST, (key, payload) -> {
            if(!algorithmRunningProperty.get()) {
                GraphOperation operation = ((GraphOperation) payload[0]);
                graphManager.performOperation(operation);
                canvasScope.getPainter().repaint();
            }
        });

        // receive notification when new graph is set
        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_SET, (key, payload) -> {
            // reset auto layout
            if(isAutoLayoutOn()) {
                stopAutoLayout();
                startAutoLayout();
            }
        });

        // ---------BINDINGS----------

        // lock properties bindings
        editLockedProperty.bind(Bindings.createBooleanBinding(
                () -> manualEditLockProperty.get() || autoEditLockProperty.get(),
                manualEditLockProperty,
                autoEditLockProperty
        ));

        inputScope.editLockedProperty().bind(editLockedProperty);
        mousePositionProperty.bind(inputScope.mousePositionProperty());

        // ---------LISTENERS----------

        // listen to auto layout on/off - start/stop layout
        autoLayoutOnProperty.addListener((obs, oldVal, newVal) -> {
            if(newVal){
                startAutoLayout();
            }else {
                stopAutoLayout();
                canvasScope.getPainter().stopAnimationTimer();
            }
        });


        // unlock graph operations when layout is paused
        animationScope.pausedProperty().addListener((obs, oldVal, newVal) ->{
            if(layoutRunningProperty.get()){
                graphManager.setQueueGraphOperationsOn(!newVal);
                writeEvent("Queue set to: "+(!newVal));
            }

        });

        // repaint when animatedProperty is changed
        animationScope.animatedProperty().addListener((obs, oldVal, newVel) -> canvasScope.getPainter().repaint());
    }

    private void onAlgorithmStarted(AlgorithmTask task){
        String taskName = task.getAlgorithmName();

        // set info & event texts
        writeEvent(taskName + " started");
        writeInfo("Running: "+ taskName);

        // set flags
        autoEditLockProperty.set(!(task instanceof LayoutTask)); // when layout, don't lock graph edit
        autoLayoutOnProperty.set(false);
        algorithmRunningProperty.set(true);

        // reset graph
        graphManager.resetGraph();
    }

    private void onAlgorithmFinished(String name, long duration, String result){
        // set info & event texts
        if(name != null){
            writeEvent(name + " finished in "+ durationFormatter.format(duration));
        }
        if(!result.isEmpty()){
            writeEvent(result);
        }
        writeInfo("");

        // set flags
        workingProperty.set(false);
        autoEditLockProperty.set(false);
        algorithmRunningProperty.set(false);
        layoutRunningProperty.set(false);
    }

    private void startAutoLayout(){
        autoLayout = new AutoD3LayoutTask(layoutScope, graphManager);
        autoLayout.setPainter(canvasScope.getPainter());
        Thread thread = new Thread(autoLayout);
        thread.start();

        layoutRunningProperty.set(true);
        autoLayoutOnProperty.set(true);
    }

    private void stopAutoLayout(){
        if (autoLayout != null) {
            autoLayout.cancel();
            layoutRunningProperty.set(false);
            autoLayoutOnProperty.set(false);
        }
    }

    public void nextStep() {
        animationScope.nextStepProperty().setValue(true);
    }

    public void openGraph(File file) {

        if(algorithmTaskScope.getAlgorithmTask() != null && algorithmTaskScope.getAlgorithmTask().isRunning())
            return;

        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        GraphReader graphReader = null;

        switch (extension){
            case "ogr": graphReader = new OgrReader(); break;
            case "tsp": graphReader = new TspReader(); break;
            case "graphml" : graphReader = new GraphMLReader(); break;
            case "mtx": graphReader = new MtxReader(canvasScope.getCanvasHeight() * 0.9); break;
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
            writeEvent("Graph opened");
        }
    }

    public void stopCurrentAlgorithm() {
        algorithmTaskScope.stopAlgorithmTask();
    }

    public void writeEvent(String s){
        Platform.runLater(() -> {
            String message = "["+timeFormatter.format(System.currentTimeMillis())+"]"+" "+s;
            String text = eventTextProperty.get() + "\n"+message;
            eventTextProperty.setValue(text);
            System.out.println(message);
        });
    }

    public void writeInfo(String s){
        Platform.runLater(() -> infoTextProperty.setValue(s));
    }

    public void generateRandomGraph(){
        notificationCenter.publish(RandomGeneratorViewModel.RANDOM_GRAPH_REQUEST);
    }

    public void createPresetGraph(){
        notificationCenter.publish(PresetCreatorViewModel.PRESET_GRAPH_REQUEST);
    }

    public void deleteGraph() {
        notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, new Graph());
    }

    public void resetGraph(){
        if(!algorithmRunningProperty.get()){
            graphManager.resetGraph();
            notificationCenter.publish(MainViewModel.RESET);
            notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        }
    }

    public void adjustCameraToGraph() {
        notificationCenter.publish("ADJUST", leftPanelWidthProperty.get(), rightPanelWidthProperty.get());
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void setKeyInput(Scene scene){

        // toggle pause
        KeyCodeCombination pauseComb = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(pauseComb, () ->  animationScope.setPaused(!animationScope.isPaused()));

        // next step
        KeyCodeCombination nextStepComb = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(nextStepComb, () -> animationScope.nextStepProperty().set(true));

        // stop algorithm
        KeyCodeCombination stopComb = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(stopComb, this::stopCurrentAlgorithm);

        // start layout
        KeyCodeCombination layoutComb = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(layoutComb, () -> notificationCenter.publish(LayoutViewModel.LAYOUT_START));

        // toggle continuous layout
        KeyCodeCombination contLayoutComb = new KeyCodeCombination(KeyCode.L,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        scene.getAccelerators().put(contLayoutComb, () -> autoLayoutOnProperty.set(autoLayoutOnProperty.not().get()));

        // toggle erase mode
        KeyCodeCombination eraseComb = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(eraseComb, () -> {
            if(!editLockedProperty().get()){
                eraseModeOnProperty().set(eraseModeOnProperty().not().get());
            }
        });

        // copy selected subgraph
        KeyCodeCombination copyComb = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(copyComb, () -> graphScope.getGraphManager().copySelectedSubgraph());

        scene.setOnKeyPressed(keyEvent -> inputScope.getPressedKeys().add(keyEvent.getCode()));
        scene.setOnKeyReleased(keyEvent -> inputScope.getPressedKeys().remove(keyEvent.getCode()));
    }

    // ---------GETTERS/SETTERS---------

    public ObjectProperty<String> eventTextProperty() {
        return eventTextProperty;
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

    public BooleanProperty algorithmRunningProperty() {
        return algorithmRunningProperty;
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
