package com.todense.viewmodel;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.layers.GraphDrawLayer;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;

import javax.inject.Inject;


public class GraphViewModel implements ViewModel {

    public static final String NEW_GRAPH_REQUEST = "NEW_GRAPH";

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    BackgroundScope backgroundScope;

    @InjectScope
    AntsScope antsScope;

    @Inject
    NotificationCenter notificationCenter;

    private GraphManager GM;

    public void initialize(){
        GM = graphScope.getGraphManager();

        Platform.runLater(() -> {
            GraphDrawLayer graphDrawLayer = new GraphDrawLayer(graphScope, backgroundScope, antsScope);
            canvasScope.getPainter().addDrawLayer(graphDrawLayer);
        });

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> {
            GM.setGraph((Graph) payload[0]);
        });

        notificationCenter.subscribe("RESET", (key, payload) ->
                graphScope.displayModeProperty().set(DisplayMode.DEFAULT));

        ChangeListener<Object> listener = (obs, oldVal, newVal) -> canvasScope.getPainter().repaint();

        nodeSizeProperty().addListener(listener);
        nodeColorProperty().addListener(listener);
        edgeWidthProperty().addListener(listener);
        edgeColorProperty().addListener(listener);
        edgeWeightColorProperty().addListener(listener);
        nodeLabelColorProperty().addListener(listener);
        nodeLabelModeProperty().addListener(listener);
        edgeWeightModeProperty().addListener(listener);
        nodeBorderProperty().addListener(listener);
    }

    public void applyColorToNodes() {
        for(Node node : GM.getGraph().getNodes()){
            node.setColor(graphScope.nodeColorProperty().get());
        }
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void applyColorToEdges() {
        for (Edge edge : GM.getGraph().getEdges()) {
            edge.setColor(edgeColorProperty().get());
        }
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public DoubleProperty nodeSizeProperty() {
        return graphScope.nodeSizeProperty();
    }

    public DoubleProperty edgeWidthProperty() {
        return graphScope.edgeWidthProperty();
    }

    public ObjectProperty<Color> nodeColorProperty() {
        return graphScope.nodeColorProperty();
    }

    public ObjectProperty<Color> edgeColorProperty() {
        return graphScope.edgeColorProperty();
    }

    public ObjectProperty<Color> nodeLabelColorProperty() {
        return graphScope.nodeLabelColorProperty();
    }

    public ObjectProperty<Color> edgeWeightColorProperty() {
        return graphScope.edgeWeightColorProperty();
    }

    public ObjectProperty<NodeLabelMode> nodeLabelModeProperty() {
        return graphScope.nodeLabelModeProperty();
    }

    public ObjectProperty<EdgeWeightMode> edgeWeightModeProperty() {
        return graphScope.edgeWeightModeProperty();
    }

    public BooleanProperty nodeBorderProperty() {
        return graphScope.nodeBorderProperty();
    }
}
