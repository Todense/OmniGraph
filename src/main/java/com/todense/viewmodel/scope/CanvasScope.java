package com.todense.viewmodel.scope;

import com.todense.viewmodel.canvas.Camera;
import com.todense.viewmodel.canvas.Painter;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class CanvasScope implements Scope {

    private final DoubleProperty canvasWidthProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty canvasHeightProperty = new SimpleDoubleProperty(0.0);
    private final ObjectProperty<Color> borderColorProperty = new SimpleObjectProperty<>(Color.WHITE);


    private Painter painter;

    private final Camera camera = new Camera();

    public CanvasScope(){

        canvasWidthProperty.addListener((obs, oldVal, newVal) -> {
            camera.translate(new Point2D((newVal.doubleValue()-oldVal.doubleValue())/2, 0));
            painter.repaint();
        });
        canvasHeightProperty.addListener((obs, oldVal, newVal) -> {
            camera.translate(new Point2D(0, (newVal.doubleValue()-oldVal.doubleValue())/2));
            painter.repaint();
        });
    }

    public double getCanvasWidth() {
        return canvasWidthProperty.get();
    }

    public DoubleProperty canvasWidthProperty() {
        return canvasWidthProperty;
    }

    public double getCanvasHeight() {
        return canvasHeightProperty.get();
    }

    public DoubleProperty canvasHeightProperty() {
        return canvasHeightProperty;
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
    }

    public Camera getCamera() {
        return camera;
    }

    public Color getBorderColor() {
        return borderColorProperty.get();
    }

    public ObjectProperty<Color> borderColorProperty() {
        return borderColorProperty;
    }
}
