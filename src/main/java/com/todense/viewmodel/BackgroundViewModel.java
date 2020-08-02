package com.todense.viewmodel;

import com.todense.viewmodel.canvas.drawlayer.layers.BackgroundDrawLayer;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.CanvasScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;

public class BackgroundViewModel implements ViewModel {


    @InjectScope
    BackgroundScope backgroundScope;

    @InjectScope
    CanvasScope canvasScope;

    public void initialize(){

        Platform.runLater(() -> {
            BackgroundDrawLayer backgroundDrawLayer = new BackgroundDrawLayer(backgroundScope, canvasScope);
            canvasScope.getPainter().addDrawLayer(backgroundDrawLayer);
        });

        ChangeListener<Object> listener = (obs, oldVal, newVal) -> canvasScope.getPainter().repaint();

        backgroundColorProperty().addListener(listener);
        gridGapProperty().addListener(listener);
        gridBrightnessProperty().addListener(listener);
        gridWidthProperty().addListener(listener);
        showingGridProperty().addListener(listener);
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundScope.backgroundColorProperty();
    }

    public BooleanProperty showingGridProperty() {
        return backgroundScope.showingGridProperty();
    }

    public IntegerProperty gridGapProperty() {
        return backgroundScope.gridGapProperty();
    }

    public IntegerProperty gridBrightnessProperty() {
        return backgroundScope.gridBrightnessProperty();
    }

    public DoubleProperty gridWidthProperty() {
        return backgroundScope.gridWidthProperty();
    }

    
}
