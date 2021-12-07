package com.todense.viewmodel.scope;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.model.graph.Node;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.graph.GraphManager;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.function.Function;

public class GraphScope implements Scope {

    private Color INITIAL_NODE_COLOR = Color.rgb(50,90,170);
    private Color INITIAL_EDGE_COLOR = Color.rgb(120,160,200);

    private final DoubleProperty nodeSizeProperty = new SimpleDoubleProperty(30d);
    private final DoubleProperty edgeWidthProperty = new SimpleDoubleProperty(0.15);
    private final DoubleProperty edgeWidthDecayProperty = new SimpleDoubleProperty(0.06);
    private final DoubleProperty edgeOpacityDecayProperty = new SimpleDoubleProperty(0.06);
    private final BooleanProperty nodeBorderProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty edgeVisibilityProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty edgeWidthDecayOnProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty edgeOpacityDecayOnProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<Color> nodeColorProperty = new SimpleObjectProperty<>(INITIAL_NODE_COLOR);
    private final ObjectProperty<Color> edgeColorProperty = new SimpleObjectProperty<>(INITIAL_EDGE_COLOR);
    private final ObjectProperty<Color> nodeLabelColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> edgeWeightColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<NodeLabelMode> nodeLabelModeProperty = new SimpleObjectProperty<>(NodeLabelMode.NONE);
    private final ObjectProperty<EdgeWeightMode> edgeWeightModeProperty = new SimpleObjectProperty<>(EdgeWeightMode.NONE);
    private final ObjectProperty<DisplayMode> displayModeProperty = new SimpleObjectProperty<>(DisplayMode.DEFAULT);

    private Function<Node, Double> nodeScaleFunction = node -> 1.0;

    private final GraphManager graphManager = new GraphManager();

    public double getNodeSize() {
        return nodeSizeProperty.get();
    }

    public DoubleProperty nodeSizeProperty() {
        return nodeSizeProperty;
    }

    public double getEdgeWidth() {
        return edgeWidthProperty.get();
    }

    public DoubleProperty edgeWidthProperty() {
        return edgeWidthProperty;
    }

    public Color getNodeColor() {
        return nodeColorProperty.get();
    }

    public ObjectProperty<Color> nodeColorProperty() {
        return nodeColorProperty;
    }

    public Color getEdgeColor() {
        return edgeColorProperty.get();
    }

    public ObjectProperty<Color> edgeColorProperty() {
        return edgeColorProperty;
    }

    public Color getNodeLabelColor() {
        return nodeLabelColorProperty.get();
    }

    public ObjectProperty<Color> nodeLabelColorProperty() {
        return nodeLabelColorProperty;
    }

    public Color getEdgeWeightColor() {
        return edgeWeightColorProperty.get();
    }

    public ObjectProperty<Color> edgeWeightColorProperty() {
        return edgeWeightColorProperty;
    }

    public NodeLabelMode getNodeLabelMode() {
        return nodeLabelModeProperty.get();
    }

    public ObjectProperty<NodeLabelMode> nodeLabelModeProperty() {
        return nodeLabelModeProperty;
    }

    public EdgeWeightMode getEdgeWeightMode() {
        return edgeWeightModeProperty.get();
    }

    public ObjectProperty<EdgeWeightMode> edgeWeightModeProperty() {
        return edgeWeightModeProperty;
    }

    public GraphManager getGraphManager() {
        return graphManager;
    }

    public DisplayMode getDisplayMode() {
        return displayModeProperty.get();
    }

    public ObjectProperty<DisplayMode> displayModeProperty() {
        return displayModeProperty;
    }

    public boolean showingNodeBorder() {
        return nodeBorderProperty.get();
    }

    public BooleanProperty nodeBorderProperty() {
        return nodeBorderProperty;
    }

    public boolean areEdgesVisibile() {
        return edgeVisibilityProperty.get();
    }

    public BooleanProperty edgeVisibilityProperty() {
        return edgeVisibilityProperty;
    }

    public Function<Node, Double> getNodeScaleFunction() {
        return nodeScaleFunction;
    }

    public void setNodeScaleFunction(Function<Node, Double> nodeScaleFunction) {
        this.nodeScaleFunction = nodeScaleFunction;
    }

    public DoubleProperty edgeWidthDecayProperty() {
        return edgeWidthDecayProperty;
    }

    public double getEdgeWidthDecay(){
        return edgeWidthDecayProperty.get();
    }

    public boolean isEdgeWidthDecayOn() {
        return edgeWidthDecayOnProperty.get();
    }

    public BooleanProperty edgeWidthDecayOnProperty() {
        return edgeWidthDecayOnProperty;
    }

    public double getEdgeOpacityDecay() {
        return edgeOpacityDecayProperty.get();
    }

    public DoubleProperty edgeOpacityDecayProperty() {
        return edgeOpacityDecayProperty;
    }

    public boolean isEdgeOpacityDecayOn() {
        return edgeOpacityDecayOnProperty.get();
    }

    public BooleanProperty edgeOpacityDecayOnProperty() {
        return edgeOpacityDecayOnProperty;
    }
}
