package com.todense.viewmodel;

import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.ServiceScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

import javax.inject.Inject;

public class OperationsViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    ServiceScope serviceScope;

    @Inject
    NotificationCenter notificationCenter;

    GraphManager graphManager;

    public void initialize(){
        graphManager = graphScope.getGraphManager();
    }

    public void createPath() {
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable) () -> graphManager.createPath());

    }

    public void createCompleteGraph() {
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable) () -> graphManager.createCompleteGraph());

    }

    public void createComplementGraph() {
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable) () -> graphManager.createComplementGraph());

    }

    public void subdivideEdges() {
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable) () -> graphManager.subdivideEdges());
    }

    public void deleteEdges() {
        notificationCenter.publish(MainViewModel.graphEditRequest, (Runnable) () -> graphManager.deleteEdges());
    }
}
