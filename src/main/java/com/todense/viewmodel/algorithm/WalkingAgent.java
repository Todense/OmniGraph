package com.todense.viewmodel.algorithm;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class WalkingAgent {

    DoubleProperty x  = new SimpleDoubleProperty();
    DoubleProperty y  = new SimpleDoubleProperty();

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }
}
