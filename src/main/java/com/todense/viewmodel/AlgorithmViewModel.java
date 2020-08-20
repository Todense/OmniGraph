package com.todense.viewmodel;

import com.todense.model.EdgeWeightMode;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.Algorithm;
import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.algorithm.service.*;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.ServiceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;

public class AlgorithmViewModel implements ViewModel {

    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    AlgorithmScope algorithmScope;

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    ServiceScope serviceScope;

    private GraphManager graphManager;
    private BooleanProperty connectivityChecksProperty = new SimpleBooleanProperty(true);

    private AlgorithmService service;
    private double startTime;

    public void initialize(){

        graphManager = graphScope.getGraphManager();

        Platform.runLater(() ->
                algorithmProperty().addListener((obs, oldVal, newVal) -> canvasScope.getPainter().repaint())
        );
        showingEndpointsProperty().addListener((obs, oldVal, newVal) -> canvasScope.getPainter().repaint());

        notificationCenter.subscribe("SET_START", (key, payload) -> startNodeProperty().set((Node) payload[0]));
        notificationCenter.subscribe("SET_GOAL", (key, payload) -> goalNodeProperty().set((Node) payload[0]));

        notificationCenter.subscribe("RESET", (key, payload ) -> {
            startNodeProperty().set(null);
            goalNodeProperty().set(null);
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> {
            Platform.runLater(this::stop);
            startNodeProperty().set(null);
            goalNodeProperty().set(null);
        });
    }

    public void start(){

        Graph g = graphManager.getGraph();

        AlgorithmService currentService = serviceScope.getService();

        if((currentService != null && currentService.isRunning()) || g.getNodes().size() == 0) return;

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

        switch (getAlgorithm()){
            case BFS: service = new BFSService(startNode, g); break;
            case DFS: service = new DFSService(startNode, g); break;
            case PRIM: service = new PrimService(startNode, g, customWeight); break;
            case KRUSKAL: service = new KruskalService(g, customWeight); break;
            case DIJKSTRA: service = new DijkstraService(startNode, goalNode, g, customWeight); break;
            case HCSEARCH: service = new HCSearchService(startNode, g, connectivityChecksProperty.get()); break;
            case ASTAR: service = new AStarService(startNode, goalNode, g, customWeight); break;
        }

        serviceScope.setService(service);

        service.setOnSucceeded(workerStateEvent -> notificationCenter.publish(MainViewModel.serviceFinished,
                getAlgorithm(),
                System.currentTimeMillis()- startTime,
                service.getResultMessage()));

        service.setOnCancelled(workerStateEvent ->
                notificationCenter.publish(MainViewModel.serviceCancelled, getAlgorithm()));

        service.setPainter(canvasScope.getPainter());

        notificationCenter.publish(MainViewModel.serviceStarted, getAlgorithm().toString());

        startTime = System.currentTimeMillis();
        service.start();
    }

    public void stop(){
        if(service != null){
            service.cancel();
        }
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
