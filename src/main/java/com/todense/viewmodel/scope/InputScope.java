package com.todense.viewmodel.scope;


import com.todense.model.graph.Node;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;

public class InputScope implements Scope {

    private BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);

    private boolean selecting = false;
    private boolean connecting = false;

    private Rectangle2D selectRect = new Rectangle2D(0,0,0,0);
    private double rectStartX = 0;
    private double rectStartY = 0;

    private List<Node> dummyEdgeStartNodes = new ArrayList<>();
    private Point2D dummyEdgeEnd = new Point2D(0,0);

    public double getRectStartX() {
        return rectStartX;
    }

    public void setRectStartX(double rectStartX) {
        this.rectStartX = rectStartX;
    }

    public double getRectStartY() {
        return rectStartY;
    }

    public void setRectStartY(double rectStartY) {
        this.rectStartY = rectStartY;
    }

    public boolean isSelecting() {
        return selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public Rectangle2D getSelectRect() {
        return selectRect;
    }

    public void setSelectRect(Rectangle2D selectRect) {
        this.selectRect = selectRect;
    }

    public Point2D getDummyEdgeEnd() {
        return dummyEdgeEnd;
    }

    public void setDummyEdgeEnd(Point2D dummyEdgeEnd) {
        this.dummyEdgeEnd = dummyEdgeEnd;
    }

    public boolean isEditLocked() {
        return editLockedProperty.get();
    }

    public BooleanProperty editLockedProperty() {
        return editLockedProperty;
    }

    public List<Node> getDummyEdgeStartNodes() {
        return dummyEdgeStartNodes;
    }

    public void setDummyEdgeStartNodes(List<Node> dummyEdgeStartNodes) {
        this.dummyEdgeStartNodes = dummyEdgeStartNodes;
    }
}
