package com.todense.viewmodel.layout.task;

import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.scope.LayoutScope;

public class AutoD3LayoutTask extends D3LayoutTask{


    public AutoD3LayoutTask(LayoutScope layoutScope, GraphManager graphManager) {
        super(layoutScope, graphManager);
    }

    @Override
    public void perform() throws InterruptedException {
        graphManager.setQueueGraphOperationsOn(true);
        layout(graph);
    }

    @Override
    protected void succeeded() { // doesn't stop animation timer
        painter.repaint();
    }

    @Override
    protected void cancelled() { // doesn't stop animation timer
        cancelled = true;
        painter.repaint();
    }

    @Override
    protected boolean stopConditionMet() {
        return super.isCancelled();
    }
}
