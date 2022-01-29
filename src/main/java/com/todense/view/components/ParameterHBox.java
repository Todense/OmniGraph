package com.todense.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.apache.commons.math3.util.Precision;

import java.util.regex.Pattern;


public class ParameterHBox extends HBox {

    private double defVal;
    private double minVal;
    private double maxVal;

    protected Label label = new Label();
    protected TextField textField = new TextField();

    private double previousMouseY = -1.0;
    private double previousMouseX = -1.0;

    private static final StringConverter<Number> DOUBLE_CONVERTER = new StringConverter<>() {
        @Override
        public String toString(Number n) {
            return String.valueOf(n);
        }

        @Override
        public Number fromString(String s) {
            return Double.parseDouble(s);
        }
    };

    private static final StringConverter<Number> INT_CONVERTER = new StringConverter<>() {
        @Override
        public String toString(Number n) {
            return String.valueOf(n);
        }

        @Override
        public Number fromString(String s) {
            return Integer.parseInt(s);
        }
    };

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("-?\\d+(\\.\\d*)?(E-?\\d+)?");
    private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");

    private final Pattern pattern;
    private final boolean isInt;
    private boolean horizontal = false;

    private static final Color defaultLabelTextColor = Color.rgb(187, 187, 187);
    private static final Color highlightLabelTextColor = defaultLabelTextColor.brighter();

    public ParameterHBox(
            String labelText,
            Property<Number> property,
            int precision,
            double defVal,
            double minVal,
            double maxVal
    ){
        this.defVal = defVal;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.isInt = property.getValue() instanceof Integer;
        this.label.setText(labelText);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(3);

        textField.setPrefHeight(25);
        textField.setPrefWidth(40);
        textField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setAlignment(Pos.CENTER);

        label.setPrefWidth(100);
        label.setText(labelText);
        HBox.setHgrow(label, Priority.ALWAYS);

        this.getChildren().add(label);
        this.getChildren().add(textField);

        this.pattern = isInt ?
                INT_PATTERN :
                DOUBLE_PATTERN;

        if(isInt)
            precision = 0;

        setUpMouseControls(precision);
        property.setValue(defVal);
        Bindings.bindBidirectional(textField.textProperty(), property, DOUBLE_CONVERTER);
    }

    private void setUpMouseControls(int precision){
        TextFormatter<String> formatter = new TextFormatter<>(change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);

        textField.setTextFormatter(formatter);
        label.setOnMousePressed(mouseEvent -> {
                previousMouseY = mouseEvent.getY();
                previousMouseX = mouseEvent.getX();
            }
        );
        label.setOnMouseDragged(mouseEvent -> {
            double delta = horizontal? (previousMouseX - mouseEvent.getX()) : (mouseEvent.getY() - previousMouseY);
            delta = delta * Math.pow(10, -precision);
            Number newValue;
            if(isInt){
                newValue = getNewValue(delta, (int)minVal, (int)maxVal);
            }
            else{
                newValue = getNewValue(delta, minVal, maxVal, precision);
            }
            textField.setText(String.valueOf(newValue));
            previousMouseX = mouseEvent.getX();
            previousMouseY = mouseEvent.getY();
            label.textFillProperty().set(highlightLabelTextColor);
        });

        label.cursorProperty().set(Cursor.V_RESIZE);
        label.getStyleClass().add("parameterLabel");

        label.setOnMouseReleased(mouseEvent -> label.textFillProperty().set(defaultLabelTextColor));
        label.setOnMouseEntered(mouseEvent -> label.textFillProperty().set(highlightLabelTextColor));
        label.setOnMouseExited(mouseEvent -> label.textFillProperty().set(defaultLabelTextColor));
    }

    private double getNewValue(double delta, double minVal, double maxVal, int precision){
        double oldValue = Double.parseDouble(textField.getText());
        double newValue = oldValue - delta;
        if(newValue > maxVal){
            newValue = maxVal;
        }else if(newValue < minVal){
            newValue = minVal;
        }else{
            newValue = Precision.round(newValue, precision);
        }
        return newValue;
    }

    private int getNewValue(double delta, int minVal, int maxVal){
        int oldValue = Integer.parseInt(textField.getText());
        int newValue = oldValue - (int)delta;
        if(newValue > maxVal){
            newValue = maxVal;
        }else if(newValue < minVal){
            newValue = minVal;
        }
        return newValue;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
        if(horizontal)
            label.cursorProperty().set(Cursor.H_RESIZE);
        else
            label.cursorProperty().set(Cursor.V_RESIZE);
    }

    public void setLabelWidth(double width){
        label.setPrefWidth(width);
    }

    public TextField getTextField() {
        return textField;
    }
}
