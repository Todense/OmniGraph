package com.todense.util;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.layout.barnesHut.IncorrectGraphBoundaryException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class Util {

    public static boolean isInteger(String strNum){
        if(strNum == null){
            return  false;
        }
        try{
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe){
            return false;
        }
        return  true;
    }

    public static boolean isDouble(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //calculates rgb values of a color with given opacity over a background color
    public static Color getFaintColor(Color color, Color backgroundColor, double opacity) {
        int r = (int) (backgroundColor.getRed() * 255 + (color.getRed() * 255 - backgroundColor.getRed() * 255) * opacity);
        int g = (int) (backgroundColor.getGreen() * 255 + (color.getGreen()* 255 - backgroundColor.getGreen()* 255) * opacity);
        int b = (int) (backgroundColor.getBlue() * 255 + (color.getBlue()* 255 - backgroundColor.getBlue()* 255) * opacity);

        return Color.rgb(r, g, b);
    }

    public static Color getFaintColor(Color color, Color backgroundColor) {
        return getFaintColor(color, backgroundColor, 0.3);
    }

    public static void bindSliderAndTextField(Slider slider, TextField textField, String pattern){
        StringProperty sp = textField.textProperty();
        DoubleProperty dp = slider.valueProperty();
        StringConverter<Number> converter = new NumberStringConverter(pattern);
        Bindings.bindBidirectional(sp, dp, converter);
    }

    public static void bindSliderAndTextField(Slider slider, TextField textField){
        bindSliderAndTextField(slider, textField, "###.###");
    }

    public static Rectangle2D getGraphBoundary(Graph graph, boolean asSquare){
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double x = node.getPos().getX();
            if(x < xMin) xMin = x;
            if(x > xMax) xMax = x;

            double y = node.getPos().getY();
            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }

        double width, height;

        if(asSquare){
            double squareLength = Math.max(xMax-xMin, yMax-yMin);
            width = squareLength;
            height = squareLength;
        }
        else{
            width = xMax-xMin;
            height = yMax-yMin;
        }

        if(Double.isInfinite(width) || Double.isInfinite(height)){
            throw new IncorrectGraphBoundaryException("Infinite boundary width or height");
        }else if(width <= 0 || height <= 0){
            throw new IncorrectGraphBoundaryException("Non positive boundary width or height");
        }
        return new Rectangle2D(xMin, yMin, width, height);
    }

}
