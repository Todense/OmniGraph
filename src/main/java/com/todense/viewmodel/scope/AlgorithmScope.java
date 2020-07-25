package com.todense.viewmodel.scope;

import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.Algorithm;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AlgorithmScope implements Scope {

    private ObjectProperty<Node> startNodeProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Node> goalNodeProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Algorithm> algorithmProperty = new SimpleObjectProperty<>();
    private BooleanProperty showingEndpointsProperty = new SimpleBooleanProperty(true);

    public ObjectProperty<Node> startNodeProperty() {
        return startNodeProperty;
    }

    public ObjectProperty<Node> goalNodeProperty() {
        return goalNodeProperty;
    }

    public Node getStartNode() {
        return startNodeProperty.get();
    }

    public void setStartNode(Node node){
        startNodeProperty.set(node);
    }

    public Node getGoalNode() {
        return goalNodeProperty.get();
    }

    public void setGoalNode(Node node){
        goalNodeProperty.set(node);
    }

    public Algorithm getAlgorithm() {
        return algorithmProperty.get();
    }

    public ObjectProperty<Algorithm> algorithmProperty() {
        return algorithmProperty;
    }

    public boolean isShowingEndpoints() {
        return showingEndpointsProperty.get();
    }

    public BooleanProperty showingEndpointsProperty() {
        return showingEndpointsProperty;
    }
}
