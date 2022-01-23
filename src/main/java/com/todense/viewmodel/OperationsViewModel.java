package com.todense.viewmodel;

import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.graph.GraphOperation;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.inject.Inject;
import java.util.List;

public class OperationsViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    private BooleanProperty editSubgraphProperty = new SimpleBooleanProperty(false);

    GraphManager graphManager;

    public void initialize(){
        graphManager = graphScope.getGraphManager();
    }

    public void createPath() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation) () -> graphManager.createPath(getEditedNodes()));
    }



    public void createCompleteGraph() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation) () -> graphManager.createCompleteGraph(getEditedNodes()));

    }

    public void createComplementGraph() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation) () -> graphManager.createComplementGraph(getEditedNodes()));

    }

    public void subdivideEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation) () -> graphManager.subdivideEdges(getEditedNodes()));
    }

    public void deleteEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (GraphOperation) () -> graphManager.deleteEdges(getEditedNodes()));
    }

    public boolean isEditSubgraphOn() {
        return editSubgraphProperty.get();
    }

    public BooleanProperty editSubgraphProperty() {
        return editSubgraphProperty;
    }

    private List<Node> getEditedNodes() {
        if(isEditSubgraphOn())
            return graphManager.getSelectedNodes();
        else return graphManager.getGraph().getNodes();
    }
}
