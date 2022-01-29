package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.graph.GraphOperation;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.List;

public class NodePopOverViewModel implements ViewModel {

    public static String NODES = "NODES";
    
    private final ObjectProperty<Color> nodeColorProperty = new SimpleObjectProperty<>(Node.DEFAULT_COLOR);
    private final ObjectProperty<String> labelProperty = new SimpleObjectProperty<>("");
    private final DoubleProperty rotationProperty = new SimpleDoubleProperty(0d);
    private List<Node> nodes;

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    public void bindToNodes(Point2D clickPoint, List<Node> nodes){
        this.nodes = nodes;
        this.nodeColorProperty.addListener((obs, oldVal, newVal) -> {
                nodes.forEach((node -> node.setColor(nodeColorProperty.get())));
                notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });
        this.labelProperty.addListener((obs, oldVal, newVal) -> {
                nodes.forEach(node -> node.setLabelText(labelProperty.get()));
                notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });
        this.rotationProperty.addListener((obs, oldVal, newVal) -> {
            var GM = graphScope.getGraphManager();
            double angle = Math.toRadians(newVal.doubleValue() - oldVal.doubleValue());
            for(Node n : nodes){
                GM.rotateNode(n, clickPoint, angle);
            }
            notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });

        Color firstNodeColor = nodes.get(0).getColor();
        if (nodes.stream().allMatch(node -> node.getColor().equals(firstNodeColor))){
            nodeColorProperty.set(firstNodeColor);
        }

        //remove buttons if more than one node is selected
        publish(NODES, nodes);
    }

    public void deleteNodes(){
        GraphManager GM =  graphScope.getGraphManager();
        Graph graph = GM.getGraph();

        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation)() ->{
            for (Node node : nodes) {
                graph.removeNode(node);
            }
        });
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void pinNode() {
        this.nodes.get(0).setPinned(true);
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void unpinNode() {
        this.nodes.get(0).setPinned(false);
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void pinSelectedNodes() {
        for (Node node : nodes) {
            node.setPinned(true);
        }
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void unpinSelectedNodes() {
        for (Node node : nodes) {
            node.setPinned(false);
        }
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
    }

    public void setStartNode() {
        notificationCenter.publish("SET_START", this.nodes.get(0));
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void setGoalNode() {
        notificationCenter.publish("SET_GOAL", this.nodes.get(0));
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void copySubgraph(){
        graphScope.getGraphManager().copySelectedSubgraph();
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
        notificationCenter.publish(MainViewModel.WRITE, "Subgraph copied");
    }

    public ObjectProperty<Color> nodeColorProperty() {
        return nodeColorProperty;
    }

    public String getLabel() {
        return labelProperty.get();
    }

    public ObjectProperty<String> labelProperty() {
        return labelProperty;
    }

    public DoubleProperty rotationProperty() {
        return rotationProperty;
    }

    public GraphScope getGraphScope() {
        return graphScope;
    }
}
