package com.todense.viewmodel.scope;


import com.todense.model.graph.Node;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputScope implements Scope {

    private final ImageCursor ERASE_CURSOR = new ImageCursor(
            new Image(Objects.requireNonNull(getClass().getResource("/trash.png")).toExternalForm()));

    private final ObservableSet<KeyCode> pressedKeys = FXCollections.observableSet();

    private final ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>(new Point2D(0, 0));

    private final BooleanProperty editLockedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty eraseModeOnProperty = new SimpleBooleanProperty(false);

    private final ObjectProperty<Cursor> canvasCursorProperty = new SimpleObjectProperty<>();

    private boolean selecting = false;
    private boolean connecting = false;

    private Rectangle2D selectRect = new Rectangle2D(0,0,0,0);
    private double rectStartX = 0;
    private double rectStartY = 0;

    private List<Node> dummyEdgeStartNodes = new ArrayList<>();
    private Point2D dummyEdgeEnd = new Point2D(0,0);

    public InputScope(){
        eraseModeOnProperty.addListener((obs, oldVal, newVal)->{
            if(!isEditLocked()){
                if(newVal){
                    setCanvasCursor(ERASE_CURSOR);
                }else{
                    setCanvasCursor(Cursor.DEFAULT);
                }
            }
        });

        editLockedProperty.addListener((obs, oldVal, newVal)->{
            if(newVal){
                setEraseModeOn(false);
            }
        });
    }

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

    public boolean isEraseModeOn() {
        return eraseModeOnProperty.get();
    }

    public BooleanProperty eraseModeOnProperty() {
        return eraseModeOnProperty;
    }

    public void setEraseModeOn(boolean eraseModeOn) {
        this.eraseModeOnProperty.set(eraseModeOn);
    }

    public List<Node> getDummyEdgeStartNodes() {
        return dummyEdgeStartNodes;
    }

    public void setDummyEdgeStartNodes(List<Node> dummyEdgeStartNodes) {
        this.dummyEdgeStartNodes = dummyEdgeStartNodes;
    }

    public Cursor getCanvasCursor() {
        return canvasCursorProperty.get();
    }

    public ObjectProperty<Cursor> canvasCursorProperty() {
        return canvasCursorProperty;
    }

    public void setCanvasCursor(Cursor cursor) {
        this.canvasCursorProperty.set(cursor);
    }

    public ObservableSet<KeyCode> getPressedKeys() {
        return pressedKeys;
    }

    public Point2D getMousePosition() {
        return mousePositionProperty.get();
    }

    public ObjectProperty<Point2D> mousePositionProperty() {
        return mousePositionProperty;
    }
}
