package com.todense.viewmodel.scope;

import com.todense.viewmodel.layout.LayoutAlgorithm;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;

public class LayoutScope implements Scope {

    private final ObjectProperty<LayoutAlgorithm> layoutAlgorithm = new SimpleObjectProperty<>();

    // General Options
    private final IntegerProperty stepTimeProperty = new SimpleIntegerProperty(7);


    // General Parameters
    private final BooleanProperty barnesHutOnProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty gravityOnProperty = new SimpleBooleanProperty(true);
    private final DoubleProperty gravityStrengthProperty = new SimpleDoubleProperty(0.1);
    private final BooleanProperty multilevelOnProperty = new SimpleBooleanProperty(false);
    private final DoubleProperty d3OptDistProperty = new SimpleDoubleProperty(50.0);

    // Yifan Hu Layout Parameters
    private final DoubleProperty initialStepSizeProperty = new SimpleDoubleProperty(5.0);
    private final DoubleProperty huOptDistProperty = new SimpleDoubleProperty(200.0);
    private final DoubleProperty stepSizeProperty = new SimpleDoubleProperty(5.0);
    private final DoubleProperty toleranceProperty = new SimpleDoubleProperty(0.01);
    private final BooleanProperty coolingOnProperty = new SimpleBooleanProperty(true);
    private final DoubleProperty coolingSpeedProperty = new SimpleDoubleProperty(0.02);
    private final DoubleProperty longRangeForceProperty = new SimpleDoubleProperty(2.0);

    // D3 Layout Parameters
    private final DoubleProperty d3AlphaProperty = new SimpleDoubleProperty(1.0);
    private final DoubleProperty d3OptimalAlphaProperty = new SimpleDoubleProperty(0.1);
    private final DoubleProperty d3MinAlphaProperty = new SimpleDoubleProperty(0.1);
    private final DoubleProperty d3AlphaDecayProperty = new SimpleDoubleProperty(0.001);
    private final DoubleProperty d3SpeedDecayProperty = new SimpleDoubleProperty(0.7);
    private final DoubleProperty d3RepulsiveStrengthProperty = new SimpleDoubleProperty(50.0);
    private final DoubleProperty d3ToleranceProperty = new SimpleDoubleProperty(0.001);

    public LayoutScope(){
        ChangeListener<Object> d3ParamListener = (obs, oldVal, newVal) -> d3AlphaProperty.set(1.0);

        d3RepulsiveStrengthProperty.addListener(d3ParamListener);
        d3OptDistProperty.addListener(d3ParamListener);
        gravityOnProperty.addListener(d3ParamListener);
        gravityStrengthProperty.addListener(d3ParamListener);
        huOptDistProperty.addListener(d3ParamListener);
    }


    public double getHuOptDist() {
        return huOptDistProperty.get();
    }

    public DoubleProperty huOptDistProperty() {
        return huOptDistProperty;
    }

    public double getInitialStepSize() {
        return initialStepSizeProperty.get();
    }

    public DoubleProperty initialStepSizeProperty() {
        return initialStepSizeProperty;
    }

    public double getTolerance() {
        return toleranceProperty.get();
    }

    public DoubleProperty toleranceProperty() {
        return toleranceProperty;
    }

    public boolean isCoolingOn() {
        return coolingOnProperty.get();
    }

    public BooleanProperty coolingOnProperty() {
        return coolingOnProperty;
    }

    public double getCoolingSpeed() {
        return coolingSpeedProperty.get();
    }

    public DoubleProperty coolingSpeedProperty() {
        return coolingSpeedProperty;
    }

    public boolean isGravityOn() {
        return gravityOnProperty.get();
    }

    public BooleanProperty gravityOnProperty() {
        return gravityOnProperty;
    }

    public double gravityPullStrength() {
        return gravityStrengthProperty.get();
    }

    public DoubleProperty gravityStrengthProperty() {
        return gravityStrengthProperty;
    }

    public boolean isMultilevelOn() {
        return multilevelOnProperty.get();
    }

    public BooleanProperty multilevelOnProperty() {
        return multilevelOnProperty;
    }

    public boolean isBarnesHutOn() {
        return barnesHutOnProperty.get();
    }

    public BooleanProperty barnesHutOnProperty() {
        return barnesHutOnProperty;
    }

    public double getLongRangeForce() {
        return longRangeForceProperty.get();
    }

    public DoubleProperty longRangeForceProperty() {
        return longRangeForceProperty;
    }


    public LayoutAlgorithm getLayoutAlgorithm() {
        return layoutAlgorithm.get();
    }

    public ObjectProperty<LayoutAlgorithm> layoutAlgorithmProperty() {
        return layoutAlgorithm;
    }

    public double getD3Alpha() {
        return d3AlphaProperty.get();
    }

    public DoubleProperty d3AlphaProperty() {
        return d3AlphaProperty;
    }

    public double getD3OptimalAlpha() {
        return d3OptimalAlphaProperty.get();
    }

    public DoubleProperty d3OptimalAlphaProperty() {
        return d3OptimalAlphaProperty;
    }

    public double getD3AlphaDecay() {
        return d3AlphaDecayProperty.get();
    }

    public DoubleProperty d3AlphaDecayProperty() {
        return d3AlphaDecayProperty;
    }

    public void setD3Alpha(double d3AlphaProperty) {
        this.d3AlphaProperty.set(d3AlphaProperty);
    }

    public void setD3OptimalAlpha(double d3OptimalAlphaProperty) {
        this.d3OptimalAlphaProperty.set(d3OptimalAlphaProperty);
    }

    public double getD3MinAlpha() {
        return d3MinAlphaProperty.get();
    }

    public DoubleProperty d3MinAlphaProperty() {
        return d3MinAlphaProperty;
    }

    public double getD3SpeedDecay() {
        return d3SpeedDecayProperty.get();
    }

    public DoubleProperty d3SpeedDecayProperty() {
        return d3SpeedDecayProperty;
    }

    public double getD3RepulsiveStrength() {
        return d3RepulsiveStrengthProperty.get();
    }

    public DoubleProperty d3RepulsiveStrengthProperty() {
        return d3RepulsiveStrengthProperty;
    }

    public double getD3Tolerance() {
        return d3ToleranceProperty.get();
    }

    public DoubleProperty d3ToleranceProperty() {
        return d3ToleranceProperty;
    }

    public double getStepSize() {
        return stepSizeProperty.get();
    }

    public DoubleProperty stepSizeProperty() {
        return stepSizeProperty;
    }

    public void setStepSize(double stepSizeProperty) {
        this.stepSizeProperty.set(stepSizeProperty);
    }

    public double getD3OptDist() {
        return d3OptDistProperty.get();
    }

    public DoubleProperty d3OptDistProperty() {
        return d3OptDistProperty;
    }

    public int getStepTime() {
        return stepTimeProperty.get();
    }

    public IntegerProperty stepTimeProperty() {
        return stepTimeProperty;
    }
}
