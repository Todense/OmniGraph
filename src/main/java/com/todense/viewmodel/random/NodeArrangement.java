package com.todense.viewmodel.random;

public enum NodeArrangement {
    RANDOM("Random"),
    CIRCLE("Circle"),
    MIN_DIST("Minimum distance");

    private final String name;

    NodeArrangement(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
