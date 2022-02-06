package com.todense.viewmodel.scope;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AnimationScope implements Scope {

    private final IntegerProperty stepTimeProperty = new SimpleIntegerProperty(100);
    private final BooleanProperty animatedProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty pausedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty nextStepProperty = new SimpleBooleanProperty(false);
    public int getStepTime() {
        return stepTimeProperty.get();
    }

    public IntegerProperty stepTimeProperty() {
        return stepTimeProperty;
    }

    public boolean isAnimated() {
        return animatedProperty.get();
    }

    public BooleanProperty animatedProperty() {
        return animatedProperty;
    }


    public boolean isPaused() {
        return pausedProperty.get();
    }

    public void setPaused(boolean paused){
        pausedProperty.set(paused);
    }

    public BooleanProperty pausedProperty() {
        return pausedProperty;
    }

    public boolean isNextStep() {
        return nextStepProperty.get();
    }

    public BooleanProperty nextStepProperty() {
        return nextStepProperty;
    }
}
