package com.todense.viewmodel.scope;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class BackgroundScope implements Scope {

    private final Color INITIAL_BACKGROUND_COLOR = Color.rgb(40,40,40);

    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(INITIAL_BACKGROUND_COLOR);
    private final BooleanProperty showingGridProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty gridGapProperty = new SimpleIntegerProperty(50);
    private final IntegerProperty gridBrightnessProperty = new SimpleIntegerProperty(50);
    private final DoubleProperty gridWidthProperty = new SimpleDoubleProperty(1);

    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public boolean isShowingGrid() {
        return showingGridProperty.get();
    }

    public BooleanProperty showingGridProperty() {
        return showingGridProperty;
    }

    public int getGridGap() {
        return gridGapProperty.get();
    }

    public IntegerProperty gridGapProperty() {
        return gridGapProperty;
    }

    public int getGridBrightness() {
        return gridBrightnessProperty.get();
    }

    public IntegerProperty gridBrightnessProperty() {
        return gridBrightnessProperty;
    }

    public double getGridWidth() {
        return gridWidthProperty.get();
    }

    public DoubleProperty gridWidthProperty() {
        return gridWidthProperty;
    }
    
    
}
