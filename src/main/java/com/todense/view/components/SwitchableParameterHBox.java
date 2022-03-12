package com.todense.view.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import org.controlsfx.control.ToggleSwitch;

public class SwitchableParameterHBox extends ParameterHBox{

    protected final ToggleSwitch toggleSwitch = new ToggleSwitch();

    public SwitchableParameterHBox(String labelText, Property<Number> property, BooleanProperty booleanProperty,
                                   int precision, double defVal, double minVal, double maxVal) {
        super(labelText, property, precision, defVal, minVal, maxVal);

        super.getChildren().add(1, toggleSwitch);
        toggleSwitch.setSelected(booleanProperty.get());
        booleanProperty.bindBidirectional(toggleSwitch.selectedProperty());
        textField.disableProperty().bind(booleanProperty.not());
        label.disableProperty().bind(booleanProperty.not());
    }
}
