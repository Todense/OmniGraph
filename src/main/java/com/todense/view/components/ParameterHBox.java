package com.todense.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import org.apache.commons.math3.util.Precision;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class ParameterHBox extends HBox {

    private Label label = new Label();
    private TextField textField = new TextField();

    private Property<Number> property;

    private double previousMouseY = -1.0;

    private static final StringConverter<Number> converter = new StringConverter<>() {
        @Override
        public String toString(Number n) {
            return String.valueOf(n);
        }

        @Override
        public Number fromString(String s) {
            return Double.parseDouble(s);
        }
    };

    public ParameterHBox(
            String labelText,
            Property<Number> property,
            int precision,
            double defVal,
            double minVal,
            double maxVal
    ){
        this.property = property;
        this.label.setText(labelText);
        this.setPrefHeight(25);
        this.setPrefWidth(180);
        this.setAlignment(Pos.CENTER);

        textField.setPrefHeight(25);
        textField.setPrefWidth(60);
        textField.setAlignment(Pos.CENTER);
        HBox.setHgrow(textField, Priority.ALWAYS);
        label.setPrefHeight(25);
        label.setPrefWidth(125);
        label.setText(labelText);

        this.getChildren().add(label);
        this.getChildren().add(textField);

        setUpMouseControls(precision, minVal, maxVal);
        Bindings.bindBidirectional(textField.textProperty(), property, converter);
        property.setValue(defVal);
    }

    private void setUpMouseControls(int precision, double minVal, double maxVal){
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d*)?(E-?\\d+)?");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);

        textField.setTextFormatter(formatter);
        label.setOnMousePressed(mouseEvent -> previousMouseY = mouseEvent.getY());
        label.setOnMouseDragged(mouseEvent -> {
            double delta = (mouseEvent.getY() - previousMouseY) * Math.pow(10, -precision);
            double oldValue = Double.parseDouble(textField.getText());
            double newValue = oldValue - delta;
            if(newValue > maxVal){
                newValue = maxVal;
            }else if(newValue < minVal){
                newValue = minVal;
            }else{
                newValue = Precision.round(newValue, precision);
            }
            textField.setText(String.valueOf(newValue));
            previousMouseY = mouseEvent.getY();
            label.effectProperty().set(new Bloom());
        });

        label.cursorProperty().set(Cursor.N_RESIZE);
        label.getStyleClass().add("parameterLabel");

        label.setOnMouseReleased(mouseEvent -> label.effectProperty().set(null));
    }
}
