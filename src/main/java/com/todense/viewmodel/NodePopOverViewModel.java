package com.todense.viewmodel;

import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.List;

public class NodePopOverViewModel implements ViewModel {
    
    private ObjectProperty<Color> nodeColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private ObjectProperty<String> labelProperty = new SimpleObjectProperty<>("");

    private List<Node> nodes;

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    public void bindToNodes(List<Node> nodes){
        this.nodes = nodes;
        this.nodeColorProperty.addListener((obs, oldVal, newVal) -> {
                nodes.forEach((node -> node.setColor(nodeColorProperty.get())));
                notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });
        this.labelProperty.addListener((obs, oldVal, newVal) -> {
                nodes.forEach(node -> node.setLabelText(labelProperty.get()));
                notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        });

        //remove buttons if more than one node is selected
        if(nodes.size() > 1){
            publish("MULTIPLE");
        }
    }

    public void deleteNodes(){
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable)() ->{
            for (Node node : nodes) {
                graphScope.getGraphManager().getGraph().removeNode(node);
            }
        });
        notificationCenter.publish("HIDE");
    }

    public void setStartNode() {
        notificationCenter.publish("SET_START", this.nodes.get(0));
        notificationCenter.publish("HIDE");
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void setGoalNode() {
        notificationCenter.publish("SET_GOAL", this.nodes.get(0));
        notificationCenter.publish("HIDE");
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void copySubgraph(){
        graphScope.getGraphManager().copySelectedSubgraph();
        notificationCenter.publish("HIDE");
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

}
