package com.todense.util;

import javafx.util.StringConverter;

public class SpinnerDoubleConverter extends StringConverter<Double> {

    @Override
    public String toString(Double aDouble) {
        return aDouble.toString();
    }

    @Override
    public Double fromString(String s) {
        try{
            return Double.parseDouble(s);
        }catch (NumberFormatException exception){
            exception.printStackTrace();
        }
        return 0d;
    }
}
