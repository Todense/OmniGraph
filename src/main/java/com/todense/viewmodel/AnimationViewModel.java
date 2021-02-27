package com.todense.viewmodel;

import com.todense.viewmodel.scope.AnimationScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

public class AnimationViewModel implements ViewModel {

    @InjectScope
    AnimationScope animationScope;

    public void nextStep() {
        nextStepProperty().setValue(true);
    }

    public IntegerProperty stepTimeProperty() {
        return animationScope.stepTimeProperty();
    }

    public BooleanProperty animatedProperty() {
        return animationScope.animatedProperty();
    }

    public BooleanProperty pausedProperty() {
        return animationScope.pausedProperty();
    }

    public BooleanProperty nextStepProperty() {
        return animationScope.nextStepProperty();
    }
}
