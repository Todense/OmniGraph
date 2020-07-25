package com.todense.viewmodel.scope;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.graph.GraphManager;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class GraphScope implements Scope {

    private final Color INITIAL_NODE_COLOR = Color.rgb(60,100,230);
    private final Color INITIAL_EDGE_COLOR = Color.rgb(50,190,180);

    private IntegerProperty nodeSizeProperty = new SimpleIntegerProperty(30);
    private DoubleProperty edgeWidthProperty = new SimpleDoubleProperty(0.15);
    private BooleanProperty nodeBorderProperty = new SimpleBooleanProperty(false);
    private ObjectProperty<Color> nodeColorProperty = new SimpleObjectProperty<>(INITIAL_NODE_COLOR);
    private ObjectProperty<Color> edgeColorProperty = new SimpleObjectProperty<>(INITIAL_EDGE_COLOR);
    private ObjectProperty<Color> nodeLabelColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private ObjectProperty<Color> edgeWeightColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private ObjectProperty<NodeLabelMode> nodeLabelModeProperty = new SimpleObjectProperty<>(NodeLabelMode.NONE);
    private ObjectProperty<EdgeWeightMode> edgeWeightModeProperty = new SimpleObjectProperty<>(EdgeWeightMode.NONE);

    private ObjectProperty<DisplayMode> displayModeProperty = new SimpleObjectProperty<>(DisplayMode.DEFAULT);

    private GraphManager graphManager = new GraphManager();

    public int getNodeSize() {
        return nodeSizeProperty.get();
    }

    public IntegerProperty nodeSizeProperty() {
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
}
