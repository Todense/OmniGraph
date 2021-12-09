package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.algorithm.AlgorithmTaskManager;
import com.todense.viewmodel.layout.task.D3LayoutTask;
import com.todense.viewmodel.layout.task.AdaptiveCoolingLayoutTask;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.arrangement.generators.RandomCirclePointGenerator;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.LayoutScope;
import com.todense.viewmodel.scope.TaskScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.geometry.Point2D;

import javax.inject.Inject;

public class LayoutViewModel extends AlgorithmTaskManager implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    TaskScope taskScope;

    @InjectScope
    LayoutScope layoutScope;

    @Inject
    NotificationCenter notificationCenter;


    public void initialize(){
        notificationCenter.subscribe("LAYOUT", (key, payload) -> startTask());
        super.initialize(taskScope, canvasScope, notificationCenter);
    }

    @Override
    public AlgorithmTask createAlgorithmTask() {
        AlgorithmTask algorithmTask;
        var GM = graphScope.getGraphManager();

        if(GM.getGraph().getOrder() == 0){
            throw new IllegalArgumentException("Graph is empty");
        }

        switch (layoutScope.getLayoutAlgorithm()){
            case D3:
                algorithmTask = new D3LayoutTask(layoutScope, GM);
                break;
            case ADAPTIVE_COOLING:
                algorithmTask = new AdaptiveCoolingLayoutTask(layoutScope, GM);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + layoutScope.getLayoutAlgorithm());
        }
        return algorithmTask;
    }


    public void randomLayout() {
        double height = canvasScope.getCanvasHeight() * 0.9;
        Generator<Point2D> generator = new RandomCirclePointGenerator(height/2);
        Graph graph = graphScope.getGraphManager().getGraph();
        for(Node n: graph.getNodes()){
            graph.setNodePosition(n, generator.next());
        }
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
        layoutScope.setD3Alpha(1.0);
    }

    public LayoutScope getLayoutScope(){
        return layoutScope;
    }

}
