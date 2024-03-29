package com.todense.viewmodel;

import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.MouseHandler;
import com.todense.viewmodel.canvas.Painter;
import com.todense.viewmodel.canvas.drawlayer.layers.UpperDrawLayer;
import com.todense.viewmodel.popover.PopOverManager;
import com.todense.viewmodel.scope.*;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

import javax.inject.Inject;

public class CanvasViewModel implements ViewModel {

    public final static String REPAINT_REQUEST = "REPAINT";
    public final static String HIDE_POPOVER = "HIDE_POPOVER";

    @InjectScope
    GraphScope graphScope;

    @InjectScope
    BackgroundScope backgroundScope;

    @InjectScope
    AlgorithmScope algorithmScope;

    @InjectScope
    CanvasScope canvasScope;

    @InjectScope
    AnimationScope animationScope;

    @InjectScope
    AntsScope antsScope;

    @InjectScope
    InputScope inputScope;

    @Inject
    NotificationCenter notificationCenter;

    private MouseHandler mouseHandler;
    private PopOverManager popOverManager;



    public void initialize(){

        Painter painter = new Painter(animationScope);

        popOverManager = new PopOverManager();

        mouseHandler = new MouseHandler(
                canvasScope.getCamera(),
                inputScope,
                painter,
                graphScope,
                popOverManager
        );

        canvasScope.setPainter(painter);

        inputScope.editLockedProperty().addListener((obs, oldVal, newVal)->{
            if(newVal)
                canvasCursorProperty().set(Cursor.MOVE);
            else
                canvasCursorProperty().set(Cursor.DEFAULT);
        });

        Platform.runLater(() ->{
            UpperDrawLayer upperDrawLayer = new UpperDrawLayer(
                    graphScope,
                    inputScope,
                    backgroundScope,
                    algorithmScope,
                    antsScope,
                    animationScope);
            painter.addDrawLayer(upperDrawLayer);
            painter.repaint();
        });

        notificationCenter.subscribe(CanvasViewModel.HIDE_POPOVER, (key, payload) -> {
            mouseHandler.hidePopOver();
            mouseHandler.clearSelection();
            painter.repaint();
        });

        notificationCenter.subscribe(REPAINT_REQUEST,  (key, payload) -> painter.repaint());

        notificationCenter.subscribe(GraphViewModel.NEW_GRAPH_REQUEST, (key, payload) -> {
            graphScope.displayModeProperty().set(DisplayMode.DEFAULT);
            painter.repaint();
        });

        notificationCenter.subscribe("ADJUST", (key, payload) -> {
            double leftPanelWidth = (double) payload[0];
            double rightPanelWidth = (double) payload[1];
            canvasScope.getCamera().adjustToGraph(
                    graphScope.getGraphManager().getGraph(),
                    canvasScope.getCanvasWidth(),
                    canvasScope.getCanvasHeight(),
                    leftPanelWidth,
                    rightPanelWidth,
                    graphScope.getNodeSize()
            );
        });
    }

    public DoubleProperty canvasWidthProperty() {
        return canvasScope.canvasWidthProperty();
    }

    public DoubleProperty canvasHeightProperty() {
        return canvasScope.canvasHeightProperty();
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        canvasScope.getPainter().setGraphicContext(graphicsContext);
    }

    public void setCanvasNode(Node canvasNode){
        popOverManager.setOwner(canvasNode);
    }

    public PopOverManager getPopOverManager() {
        return popOverManager;
    }

    public BooleanProperty editLockedProperty(){
        return inputScope.editLockedProperty();
    }


    public ObjectProperty<Cursor> canvasCursorProperty() {
        return inputScope.canvasCursorProperty();
    }
}
