package com.todense.viewmodel;

import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

import javax.inject.Inject;

public class OperationsViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    TaskScope taskScope;

    @Inject
    NotificationCenter notificationCenter;

    GraphManager graphManager;

    public void initialize(){
        graphManager = graphScope.getGraphManager();
    }

    public void createPath() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable) () -> graphManager.createPath());

    }

    public void createCompleteGraph() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable) () -> graphManager.createCompleteGraph());

    }

    public void createComplementGraph() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable) () -> graphManager.createComplementGraph());

    }

    public void subdivideEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable) () -> graphManager.subdivideEdges());
    }

    public void deleteEdges() {
        notificationCenter.publish(MainViewModel.GRAPH_EDIT_REQUEST, (Runnable) () -> graphManager.deleteEdges());
    }
}
