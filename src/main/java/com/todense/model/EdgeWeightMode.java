package com.todense.model;

public enum EdgeWeightMode {
    NONE("None"),
    LENGTH("Length"),
    CUSTOM("Custom");

    private final String name;

    EdgeWeightMode(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}