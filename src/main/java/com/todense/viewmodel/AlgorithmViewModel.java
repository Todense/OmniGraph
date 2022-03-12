package com.todense.viewmodel;

import com.todense.model.EdgeWeightMode;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.Algorithm;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.algorithm.AlgorithmTaskManager;
import com.todense.viewmodel.algorithm.task.*;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.AlgorithmTaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;

public class AlgorithmViewModel extends AlgorithmTaskManager implements ViewModel {

    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    AlgorithmScope algorithmScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    AlgorithmTaskScope algorithmTaskScope;

    private GraphManager graphManager;
    private final BooleanProperty connectivityChecksProperty = new SimpleBooleanProperty(true);
    private double startTime;

    public void initialize(){

        graphManager = graphScope.getGraphManager();

        Platform.runLater(() ->
                algorithmProperty().addListener((obs, oldVal, newVal) -> canvasScope.getPainter().repaint())
        );
        showingEndpointsProperty().addListener((obs, oldVal, newVal) -> canvasScope.getPainter().repaint());

        notificationCenter.subscribe("SET_START", (key, payload) -> startNodeProperty().set((Node) payload[0]));
        notificationCenter.subscribe("SET_GOAL", (key, payload) -> goalNodeProperty().set((Node) payload[0]));

        notificationCenter.subscribe(MainViewModel.RESET, (key, payload ) -> {
            startNodeProperty().set(null);
            goalNodeProperty().set(null);
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> {
            Platform.runLater(this::stopTask);
            startNodeProperty().set(null);
            goalNodeProperty().set(null);
        });

        super.initialize(algorithmTaskScope, canvasScope, notificationCenter);
    }


    @Override
    protected AlgorithmTask createAlgorithmTask() {
        Graph g = graphManager.getGraph();

        if(g.getOrder() == 0){
            throw new IllegalArgumentException("Graph is empty");
        }

        graphScope.displayModeProperty().set(DisplayMode.ALGORITHMIC);
        graphManager.resetGraph();

        if(algorithmScope.getStartNode() == null){
            algorithmScope.setStartNode(g.getNodes().get(0));
        }

        if(algorithmScope.getGoalNode() == null){
            algorithmScope.setGoalNode(g.getNodes().get(g.getNodes().size()-1));
        }

        Node startNode = algorithmScope.getStartNode();
        Node goalNode = algorithmScope.getGoalNode();

        boolean customWeight = graphScope.getEdgeWeightMode().equals(EdgeWeightMode.CUSTOM);

        AlgorithmTask task = null;

        switch (getAlgorithm()){
            case BFS: task = new BFSTask(startNode, g); break;
            case DFS: task = new DFSTask(startNode, g); break;
            case PRIM: task = new PrimTask(startNode, g, customWeight); break;
            case KRUSKAL: task = new KruskalTask(g, customWeight); break;
            case DIJKSTRA: task = new DijkstraTask(startNode, goalNode, g, customWeight); break;
            case HCSEARCH: task = new HamiltonianCycleSearchTask(startNode, g, connectivityChecksProperty.get()); break;
            case ASTAR: task = new AStarTask(startNode, goalNode, g, customWeight); break;
        }

        return task;
    }


    public Algorithm getAlgorithm() {
        return algorithmScope.getAlgorithm();
    }

    public ObjectProperty<Algorithm> algorithmProperty() {
        return algorithmScope.algorithmProperty();
    }

    public BooleanProperty connectivityChecksProperty() {
        return connectivityChecksProperty;
    }

    public ObjectProperty<Node> startNodeProperty() {
        return algorithmScope.startNodeProperty();
    }

    public ObjectProperty<Node> goalNodeProperty() {
        return algorithmScope.goalNodeProperty();
    }

    public BooleanProperty showingEndpointsProperty() {
        return algorithmScope.showingEndpointsProperty();
    }

}
