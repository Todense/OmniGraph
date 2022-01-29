package com.todense.viewmodel;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.util.Util;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.graph.GraphOperation;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.ArrayList;

public class EdgePopOverViewModel implements ViewModel {

    public static final String EDGES = "EDGES";
    private ObjectProperty<Color> edgeColorProperty = new SimpleObjectProperty<>(Edge.DEFAULT_COLOR);
    private final StringProperty edgeWeightProperty = new SimpleStringProperty();

    private ArrayList<Edge> edges;

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;
    private GraphManager graphManager;

    public void bindToEdges(ArrayList<Edge> edges){
        this.edges = edges;
        edgeColorProperty.addListener((obs, oldVal, newVal) ->{
                edges.forEach(edge -> edge.setColor(edgeColorProperty.get()));
            notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });

        edgeWeightProperty.addListener((obs, newVal, oldVal) ->{
                edges.forEach(edge -> {
                    if(Util.isDouble(edgeWeightProperty.get()))
                        edge.setWeight(Double.parseDouble(edgeWeightProperty.get()));
                });
                notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });

        Color firstEdgeColor = edges.get(0).getColor();
        if (edges.stream().allMatch(edge -> edge.getColor().equals(firstEdgeColor))){
            edgeColorProperty.set(firstEdgeColor);
        }

        publish(EDGES, edges);
    }

    public void deleteEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation)() ->{
            var GM = graphScope.getGraphManager();
            Graph graph = GM.getGraph();
            for (Edge edge : edges) {
                graph.removeEdge(edge);
            }
        });
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void subdivideEdges(){
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation)() ->{
            for (Edge edge : edges) {
                graphManager.subdivideEdge(edge);
            }
        });
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void contractEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation)() ->{
            for (Edge edge : edges) {
                if(graphManager.getGraph().getEdge(edge.getN1(), edge.getN2()) != null){
                    graphManager.contractEdge(edge);
                }
            }
        });
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public ObjectProperty<Color> edgeColorProperty() {
        return edgeColorProperty;
    }

    public StringProperty edgeWeightProperty() {
        return edgeWeightProperty;
    }

    public void setGraphManager(GraphManager graphManager) {
        this.graphManager = graphManager;
    }

    public GraphScope getGraphScope(){
        return graphScope;
    }

}
