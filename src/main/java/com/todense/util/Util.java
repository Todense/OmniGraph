package com.todense.util;

import javafx.scene.paint.Color;

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
    public static Color getFaintColor(Color color, Color backgroundColor) {
        double opacity = 0.2;
        int r = (int) (backgroundColor.getRed() * 255 + (color.getRed() * 255 - backgroundColor.getRed() * 255) * opacity);
        int g = (int) (backgroundColor.getGreen() * 255 + (color.getGreen()* 255 - backgroundColor.getGreen()* 255) * opacity);
        int b = (int) (backgroundColor.getBlue() * 255 + (color.getBlue()* 255 - backgroundColor.getBlue()* 255) * opacity);

        return Color.rgb(r, g, b);
    }

}
