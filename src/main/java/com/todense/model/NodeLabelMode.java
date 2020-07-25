package com.todense.model;

public enum NodeLabelMode {
    NONE("None"),
    NUMBER("Number"),
    CUSTOM("Custom");

    private final String name;

    NodeLabelMode(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
