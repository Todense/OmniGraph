package com.todense.viewmodel;

import com.google.inject.Inject;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.geometry.Point2D;


public class BackgroundPopOverViewModel implements ViewModel {

    @InjectScope
    GraphScope graphScope;

    @Inject
    NotificationCenter notificationCenter;

    private Point2D clickPoint;

    public void pasteSubgraph() {
        graphScope.getGraphManager().addSubgraph(clickPoint);
        notificationCenter.publish(CanvasViewModel.HIDE_POPOVER);
        notificationCenter.publish(CanvasViewModel.REPAINT_REQUEST);
    }

    public void setClickPoint(Point2D clickPoint) {
        this.clickPoint = clickPoint;
    }
}
