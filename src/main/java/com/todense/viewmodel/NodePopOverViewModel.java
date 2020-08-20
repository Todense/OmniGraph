package com.todense.viewmodel;

import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.ArrayList;

public class NodePopOverViewModel implements ViewModel {
    
    private ObjectProperty<Color> nodeColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private ObjectProperty<String> labelProperty = new SimpleObjectProperty<>("");

    private ArrayList<Node> nodes;

    @Inject
    NotificationCenter notificationCenter;
    private GraphManager graphManager;

    public void bindToNodes(ArrayList<Node> nodes){
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
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable)() ->{
            for (Node node : nodes) {
                graphManager.getGraph().removeNode(node);
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

    public ObjectProperty<Color> nodeColorProperty() {
        return nodeColorProperty;
    }

    public String getLabel() {
        return labelProperty.get();
    }

    public ObjectProperty<String> labelProperty() {
        return labelProperty;
    }

    public void setGraphManager(GraphManager graphManager) {
        this.graphManager = graphManager;
    }

}
